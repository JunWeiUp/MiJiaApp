package com.mijia.app.socket;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 指令超时计时
 */

public class InstructionOutTimeObserver {

    private Observable observable = Observable.interval(0, 1, TimeUnit.SECONDS);
    private CompositeDisposable mDisposables = new CompositeDisposable();
    private Map<String, Disposable> mMap = new HashMap<>();
    private Map<String, InstructOutTimeListener> mInstructionListenerMap = new HashMap<>();
    private static InstructionOutTimeObserver mInstructionOutTimeObservable = new InstructionOutTimeObserver();

    public static InstructionOutTimeObserver getInstance() {
        return mInstructionOutTimeObservable;
    }

    public void addSendInstructionAndTime(String instruction, InstructOutTimeListener instructOutTimeListener) {
        if (!mMap.keySet().contains(instruction)) {
            mInstructionListenerMap.put(instruction, instructOutTimeListener);
            Disposable disposable = observable.subscribe(new TimeConsumer(instruction));
            mMap.put(instruction, disposable);
            mDisposables.add(disposable);
        }
    }

    private class TimeConsumer implements Consumer<Long> {
        String instruction;

        public TimeConsumer(String instruction) {
            this.instruction = instruction;
        }

        @Override
        public void accept(Long l) throws Exception {
            if (l > 10) {
                Disposable disposable = mMap.remove(instruction);
                if (mDisposables != null && disposable != null) {
                    mDisposables.remove(disposable);
                }
                Observable.create(emitter -> {
                    InstructOutTimeListener instructOutTimeListener = mInstructionListenerMap.get(instruction);
                    if (instructOutTimeListener != null) {
                        instructOutTimeListener.onOutTime(instruction);
                        mInstructionListenerMap.remove(instruction);
                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
            }
        }
    }

    public interface InstructOutTimeListener {
        void onOutTime(String instruction);
    }


    public void cancelInstruction(String instruction) {
        Disposable disposable = mMap.remove(instruction);
        if (disposable != null) {
            mDisposables.remove(disposable);
        }
        mInstructionListenerMap.remove(instruction);
    }
 
    public boolean isOutTiming(String instruction){
        return !mMap.containsKey(instruction);
    }

 
    public void clear() {
        mDisposables.clear();
        mMap.clear();
        mInstructionListenerMap.clear();
    }

}
