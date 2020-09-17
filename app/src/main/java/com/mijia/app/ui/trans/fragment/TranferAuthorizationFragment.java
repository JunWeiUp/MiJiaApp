package com.mijia.app.ui.trans.fragment;

import android.arch.lifecycle.Observer;
import android.databinding.Observable;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.mijia.app.R;
import com.mijia.app.bean.EmpowerListBean;
import com.mijia.app.bean.SimpleBean;
import com.mijia.app.databinding.FragmentTranferAuthorizationBinding;
import com.mijia.app.ui.trans.adapter.TransRefuseAuthorizationAdapter;
import com.mijia.app.ui.trans.adapter.TransSuccessAuthorizationAdapter;
import com.mijia.app.ui.trans.adapter.TransWaitAuthorizationAdapter;
import com.mijia.app.ui.trans.adapter.UploadFailAdapter;
import com.mijia.app.ui.trans.adapter.UploadFinishAdapter;
import com.mijia.app.ui.trans.adapter.UploadingAdapter;
import com.mijia.app.viewmodel.EmpowerListViewModel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.Arrays;

/**
 * Created by Administrator on 2019/6/11.
 */

public class TranferAuthorizationFragment extends BaseFragment<FragmentTranferAuthorizationBinding, BaseViewModel> {

    private EmpowerListViewModel mListViewModel;

    public ObservableBoolean isWaitAuthorizationOpen = new ObservableBoolean(true);
    public ObservableBoolean isRefuseAtuorizationOpen = new ObservableBoolean(true);
    public ObservableBoolean isSuccessAtuorizationOpen = new ObservableBoolean(true);

    public ObservableInt waitAuthorizationNum = new ObservableInt(0);
    public ObservableInt refuseAuthorizationNum = new ObservableInt(0);
    public ObservableInt successAuthorizationNum = new ObservableInt(0);

    private TransWaitAuthorizationAdapter mWaitAuthorizationAdapter;
    private TransRefuseAuthorizationAdapter mRefuseAuthorizationAdapter;
    private TransSuccessAuthorizationAdapter mSuccessAuthorizationAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_tranfer_authorization;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        binding.setHandler(this);
        binding.setIsOpenSelect(TransferListFragment.isOpenSelect);

        TransferListFragment.isOpenSelect.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (TransferListFragment.pageIndex.get() == 2) {
//                    isDownloadingOpen.set(TransferListFragment.isOpenSelect.get());
//                    isDownloadFailOpen.set(TransferListFragment.isOpenSelect.get());
//                    isDownloadFinishOpen.set(TransferListFragment.isOpenSelect.get());
                    mWaitAuthorizationAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());
                    mRefuseAuthorizationAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());
                    mSuccessAuthorizationAdapter.openOrCloseSelectMode(TransferListFragment.isOpenSelect.get());

                    if (!TransferListFragment.isOpenSelect.get()) {
                        mWaitAuthorizationAdapter.clearAllSelect();
                        mRefuseAuthorizationAdapter.clearAllSelect();
                        mSuccessAuthorizationAdapter.clearAllSelect();

                        binding.ivAllSelectWait.setSelected(false);
                        binding.ivAllSelectRefuse.setSelected(false);
                        binding.ivAllSelectSuccess.setSelected(false);

                    }
                }
            }
        });


        TransferListFragment.pageIndex.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                if (TransferListFragment.pageIndex.get() != 2) {
                    mWaitAuthorizationAdapter.clearAllSelect();
                    mRefuseAuthorizationAdapter.clearAllSelect();
                    mSuccessAuthorizationAdapter.clearAllSelect();
                }
            }
        });

        initWaitingAuthorizationRecycler();
        initRefuseAuthorizationRecycler();
        initAuthorizationSuccessRecycler();

        mListViewModel = new EmpowerListViewModel();
        initData();

        binding.smartview.setOnRefreshListener(refreshlayout -> mListViewModel.getEmpowerList());
        binding.smartview.autoRefresh();
        binding.smartview.setEnableLoadmore(false);
    }

    private void initData() {
        mListViewModel.mEporwerListBean.observe(this, empowerListBean -> {
            if (empowerListBean != null && empowerListBean.getErr().getCode() == 0) {
                mWaitAuthorizationAdapter.replaceData(empowerListBean.getWaitList());
                waitAuthorizationNum.set(empowerListBean.getWaitList().size());
                isWaitAuthorizationOpen.set(empowerListBean.getWaitList().size() > 0);
                mRefuseAuthorizationAdapter.replaceData(empowerListBean.getRefuseList());
                refuseAuthorizationNum.set(empowerListBean.getRefuseList().size());
                isRefuseAtuorizationOpen.set(empowerListBean.getRefuseList().size() > 0);
                mSuccessAuthorizationAdapter.replaceData(empowerListBean.getAgreedList());
                successAuthorizationNum.set(empowerListBean.getAgreedList().size());
                isSuccessAtuorizationOpen.set(empowerListBean.getAgreedList().size() > 0);

            }
            binding.smartview.finishRefresh();
        });
    }


    private void initAuthorizationSuccessRecycler() {
        mSuccessAuthorizationAdapter = new TransSuccessAuthorizationAdapter(R.layout.item_authorization_list);
        binding.recyclerSuccessAuthorization.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerSuccessAuthorization.setAdapter(mSuccessAuthorizationAdapter);
        mSuccessAuthorizationAdapter.setOnItemChildClickListener((adapter, view, position) -> {
//            switch (view.getId()) {
//                case R.id.iv_select:
//                    changeSelectedType(3);
//                    mSuccessAuthorizationAdapter.getData().get(position).setSelected(!mSuccessAuthorizationAdapter.getData().get(position).isSelected());
//                    mSuccessAuthorizationAdapter.refresh();
//                    binding.ivAllSelectSuccess.setSelected(mSuccessAuthorizationAdapter.getSelectedNum() == mSuccessAuthorizationAdapter.getData().size());
//                    refreshAllListStatus();
//                    break;
//                default:
//                    break;
//            }
        });
    }

    private void initRefuseAuthorizationRecycler() {
        mRefuseAuthorizationAdapter = new TransRefuseAuthorizationAdapter(R.layout.item_authorization_list);
        binding.recyclerRefuseAuthorization.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerRefuseAuthorization.setAdapter(mRefuseAuthorizationAdapter);
        mRefuseAuthorizationAdapter.setOnItemChildClickListener((adapter, view, position) -> {
//            switch (view.getId()) {
//                case R.id.iv_select:
//                    changeSelectedType(2);
//                    mRefuseAuthorizationAdapter.getData().get(position).setSelected(!mRefuseAuthorizationAdapter.getData().get(position).isSelected());
//                    mRefuseAuthorizationAdapter.refresh();
//                    binding.ivAllSelectRefuse.setSelected(mRefuseAuthorizationAdapter.getSelectedNum() == mRefuseAuthorizationAdapter.getData().size());
//                    refreshAllListStatus();
//                    break;
//                default:
//                    break;
//            }
        });
    }

    private void initWaitingAuthorizationRecycler() {
        mWaitAuthorizationAdapter = new TransWaitAuthorizationAdapter(R.layout.item_authorization_list);
        binding.recyclerWaitAuthorization.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerWaitAuthorization.setAdapter(mWaitAuthorizationAdapter);
        mWaitAuthorizationAdapter.setOnItemChildClickListener((adapter, view, position) -> {
//            switch (view.getId()) {
//                case R.id.iv_select:
//                    changeSelectedType(1);
//                    mWaitAuthorizationAdapter.getData().get(position).setSelected(!mWaitAuthorizationAdapter.getData().get(position).isSelected());
//                    mWaitAuthorizationAdapter.refresh();
//                    binding.ivAllSelectWait.setSelected(mWaitAuthorizationAdapter.getSelectedNum() == mWaitAuthorizationAdapter.getData().size());
//                    refreshAllListStatus();
//                    break;
//                default:
//                    break;
//            }
        });
    }

    private void refreshAllListStatus() {
        if (mWaitAuthorizationAdapter.getSelectedNum() == 0
                && mRefuseAuthorizationAdapter.getSelectedNum() == 0
                && mSuccessAuthorizationAdapter.getSelectedNum() == 0) {
            TransferListFragment.selectedNum.set(0);
            TransferListFragment.isOpenSelect.set(false);
            TransferListFragment.pageIndex.set(2);
            TransferListFragment.selectType.set(0);
        }
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_wait_authorization:
                isWaitAuthorizationOpen.set(!isWaitAuthorizationOpen.get());
                break;
            case R.id.ll_refuse_authorization:
                isRefuseAtuorizationOpen.set(!isRefuseAtuorizationOpen.get());
                break;
            case R.id.ll_success_authorization:
                isSuccessAtuorizationOpen.set(!isSuccessAtuorizationOpen.get());
                break;
            case R.id.iv_all_select_wait:
//                binding.ivAllSelectWait.setSelected(!binding.ivAllSelectWait.isSelected());
//                if (binding.ivAllSelectWait.isSelected()) {
//                    changeSelectedType(1);
//                    mWaitAuthorizationAdapter.selectAll();
//                } else {
//                    mWaitAuthorizationAdapter.clearAllSelect();
//                }
//                refreshAllListStatus();
//                break;
            case R.id.iv_all_select_refuse:
//                binding.ivAllSelectRefuse.setSelected(!binding.ivAllSelectRefuse.isSelected());
//                if (binding.ivAllSelectRefuse.isSelected()) {
//                    changeSelectedType(2);
//                    mRefuseAuthorizationAdapter.selectAll();
//                } else {
//                    mRefuseAuthorizationAdapter.clearAllSelect();
//                }
//                refreshAllListStatus();
                break;
            case R.id.iv_all_select_success:
//                binding.ivAllSelectSuccess.setSelected(!binding.ivAllSelectSuccess.isSelected());
//                if (binding.ivAllSelectSuccess.isSelected()) {
//                    changeSelectedType(3);
//                    mSuccessAuthorizationAdapter.selectAll();
//                } else {
//                    mSuccessAuthorizationAdapter.clearAllSelect();
//                }
//                refreshAllListStatus();
                break;
            default:
                break;
        }
    }

    private void changeSelectedType(int type) {
        switch (type) {
            case 1://等待
                mSuccessAuthorizationAdapter.clearAllSelect();
                mRefuseAuthorizationAdapter.clearAllSelect();
                binding.ivAllSelectSuccess.setSelected(false);
                binding.ivAllSelectRefuse.setSelected(false);
                if (TransferListFragment.selectType.get() != 1) {
                    TransferListFragment.selectType.set(1);
                }
                break;
            case 2://失败
                mRefuseAuthorizationAdapter.clearAllSelect();
                mWaitAuthorizationAdapter.clearAllSelect();
                binding.ivAllSelectSuccess.setSelected(false);
                binding.ivAllSelectWait.setSelected(false);
                if (TransferListFragment.selectType.get() != 1) {
                    TransferListFragment.selectType.set(1);
                }
                break;
            case 3://完成
                binding.ivAllSelectRefuse.setSelected(false);
                binding.ivAllSelectWait.setSelected(false);
                mRefuseAuthorizationAdapter.clearAllSelect();
                mWaitAuthorizationAdapter.clearAllSelect();
                if (TransferListFragment.selectType.get() != 1) {
                    TransferListFragment.selectType.set(1);
                }
                break;
            default:
                break;
        }
    }
}
