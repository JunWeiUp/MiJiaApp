package com.mijia.app.ui.disk.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lzy.imagepicker.ui.ImageBroseActivity;
import com.mijia.app.R;
import com.mijia.app.bean.DownLoadRequestBean;
import com.mijia.app.bean.FileBean;
import com.mijia.app.socket.DownTask;
import com.mijia.app.socket.TempLoadingForMultiple;
import com.mijia.app.ui.disk.fragment.DiskManagerFragment;
import com.nevermore.oceans.uits.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiskPhotoArrayListAdapter extends BaseMultiItemQuickAdapter<PhotoArrayMultipleItem, BaseViewHolder> {

    private Context mContext;

    private List<PhotoAdapter> photoAdapterList = new ArrayList<>();

    public List<PhotoAdapter> getPhotoAdapterList() {
        return photoAdapterList;
    }

    @Override
    public void replaceData(@NonNull Collection<? extends PhotoArrayMultipleItem> data) {
        photoAdapterList.clear();
        super.replaceData(data);
    }

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DiskPhotoArrayListAdapter(Context mContext, List<PhotoArrayMultipleItem> data) {
        super(data);
        //设置不同的布局
        this.mContext = mContext;
        addItemType(PhotoArrayMultipleItem.TITLE, R.layout.item_disk_photo_array_title);
        addItemType(PhotoArrayMultipleItem.PHOTO, R.layout.item_disk_photo);
    }

    @Override
    protected void convert(BaseViewHolder helper, PhotoArrayMultipleItem item) {
        switch (helper.getItemViewType()) {
            case PhotoArrayMultipleItem.TITLE:
                TextView tvTitle = helper.getView(R.id.tv_photo_array_time);
                TextView tvNum = helper.getView(R.id.tv_photo_num);
                tvTitle.setText(item.getTitle());
                tvNum.setVisibility(View.GONE);
                break;
            case PhotoArrayMultipleItem.PHOTO:
                RecyclerView recyclerView = helper.getView(R.id.recycler);
                recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4));
                PhotoAdapter photoAdapter = new PhotoAdapter();
                photoAdapterList.add(photoAdapter);
                recyclerView.setAdapter(photoAdapter);
                photoAdapter.replaceData(item.getFileBean());
                break;
            default:
                break;
        }
    }

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemChildClick(PhotoAdapter adapter, View view, int position);
    }

    public class PhotoAdapter extends BaseQuickAdapter<FileBean, BaseViewHolder> {

        /**
         * 获取已选中的文件
         *
         * @return
         */
        public List<FileBean> getSelectedFileBean() {
            List<FileBean> list = new ArrayList<>();
            for (FileBean datum : getData()) {
                if (datum.isSelected()) {
                    list.add(datum);
                }
            }
            return list;
        }

        /**
         * 清除所有选择
         */
        public void clearAllSelect() {
            for (FileBean simpleBean : getData()) {
                simpleBean.setSelected(false);
            }
            refresh();
        }

        /**
         * 获取选中数量
         *
         * @return
         */
        public int getSelectedNum() {
            int selectedNum = 0;
            for (FileBean simpleBean : getData()) {
                if (simpleBean.isSelected()) {
                    selectedNum++;
                }
            }
            return selectedNum;
        }

        /**
         * 刷新
         */
        public void refresh() {
            changeSelectedNum();
            notifyDataSetChanged();
        }

        private void changeSelectedNum() {
            int selectedNum = getSelectedNum();
            if (selectedNum == 0) {
                DiskManagerFragment.isOpenSelect.set(false);
            } else {
                if (!DiskManagerFragment.isOpenSelect.get()) {
                    DiskManagerFragment.isOpenSelect.set(true);
                }
            }
            DiskManagerFragment.selectedNum.set(selectedNum);
            DiskManagerFragment.selectedFileList.clear();
            DiskManagerFragment.selectedFileList.addAll(getSelectedFileBean());
        }


        public void mingmichange() {
            if (DiskManagerFragment.isMiWenOpen.get()) {
                if (getSelectedNum() > 0) {
                    int index = 0;
                    for (FileBean simpleBean : getData()) {
                        if (simpleBean.isSelected() && index == 0) {
                            index = 1;
                        } else {
                            simpleBean.setSelected(false);
                        }
                    }
                }
            }
            refresh();
        }

        public PhotoAdapter() {
            super(R.layout.item_select_photo);
        }

        @Override
        protected void convert(BaseViewHolder helper, FileBean item) {
            ImageView ivImage = helper.getView(R.id.iv_image);
            ivImage.setImageResource(R.color.shadowColor);
            ImageView ivSelect = helper.getView(R.id.iv_select);
            if (item.isSelected()) {
                ivSelect.setSelected(true);
            } else {
                ivSelect.setSelected(false);
            }
            ivSelect.setOnClickListener(v -> {
                onItemClickListener.onItemChildClick(this, ivSelect, helper.getAdapterPosition());
            });


            long fileFize = Long.valueOf(item.getSize());
            String filePath = item.getName();
            DownLoadRequestBean downLoadRequestBean = new DownLoadRequestBean();
            downLoadRequestBean.setFullpath(filePath);
            downLoadRequestBean.setIndex("1");
            DownTask task = new DownTask(downLoadRequestBean, fileFize, true);

            String locationPath = task.getLocationFilePath();
            File locationFile = new File(locationPath);
            long length = locationFile.length();
            if (length >= fileFize || length >= fileFize / 10 * 9) {
                ivImage.setTag(null);
//                ImageLoader.loadImage(ivImage, locationPath);
                RequestOptions options = new RequestOptions();
                options.diskCacheStrategy(DiskCacheStrategy.NONE);
                Glide.with(mContext).asBitmap().apply(options).load(locationPath).into(ivImage);
                ivImage.setTag(task);
            } else {
                ivImage.setTag(null);
                RequestOptions options = new RequestOptions();
                options.diskCacheStrategy(DiskCacheStrategy.NONE);
                Glide.with(mContext).asBitmap().apply(options).load(locationPath).into(ivImage);
                ivImage.setTag(task);
                TempLoadingForMultiple.loadImage(ivImage);
            }

            ivImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add(locationPath);
                    mContext.startActivity(new Intent(mContext, ImageBroseActivity.class).putExtra(ImageBroseActivity.PICS, list));
                }
            });
        }
    }
}
