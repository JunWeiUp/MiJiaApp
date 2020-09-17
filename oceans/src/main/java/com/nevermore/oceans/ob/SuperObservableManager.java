package com.nevermore.oceans.ob;

import java.util.HashMap;

/**
 * 
 * author XuNeverMore
 * create on 2017/6/15 0015
 * github https://github.com/XuNeverMore
 */

public class SuperObservableManager {

    private static SuperObservableManager instance;

    private HashMap<Class,SuperObservable> mObservables;

    private SuperObservableManager() {
        mObservables = new HashMap<>();
    }

    public static SuperObservableManager getInstance(){
        if(instance==null){
            synchronized (SuperObservableManager.class){
                if(instance==null){
                    instance = new SuperObservableManager();
                }
            }
        }
        return instance;
    }


    /**
     * 获取被观察者
     * @param observableName
     * @return
     */
    @Deprecated
    public <Observer> SuperObservable<Observer> getObservable(Class<Observer> observableName){
        SuperObservable<Observer> superObservable = mObservables.get(observableName);

        if(superObservable==null){
            superObservable = new SuperObservable<>();
            mObservables.put(observableName,superObservable);
        }

        return superObservable;
    }


    /**
     * 移除被观察者
     * @param observableName
     * @return
     */
    public SuperObservable removeObservable(Class observableName){
        return mObservables.remove(observableName);
    }

    /**
     * 注册观察者
     * @param clazz
     * @param o
     * @param <Observer>
     */
    public static <Observer> void registerObserver(Class<Observer> clazz,Observer o){
        SuperObservable<Observer> observable = getInstance().getObservable(clazz);
        observable.registerObserver(o);
    }

    /**
     * 解除注册
     * @param clazz
     * @param o
     * @param <Observer>
     */
    public static <Observer> void unregisterObserver(Class<Observer> clazz,Observer o){
        SuperObservable<Observer> observable = getInstance().getObservable(clazz);
        observable.unregisterObserver(o);
    }


    public static <Observer> void notify(Class<Observer> clazz,Dispatcher<Observer> dispatcher){
        SuperObservable<Observer> observable = getInstance().getObservable(clazz);
        observable.notifyMethod(dispatcher);
    }



}
