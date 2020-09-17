package com.lzy.imagepicker.view;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/2/1 0001.
 */

public class UploadImgView extends NoScrollGridView {

    private int maxImageCount = 9;//上传图片的最大数量
    private List<String> imageList = new ArrayList<>();
    private ManageImageAdapter adapter;
    private boolean isFull = false;//图片达到最大数量

    public static final String PLACE_HOLDER_PATH = "PLACE_HOLDER_PATH";

    public UploadImgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UploadImgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 是否达到最大数量
     * 未达到数量图片集合会有一个空路径占位（上传按钮）
     *
     * @return
     */
    public boolean isFull() {
        return isFull;
    }


    public List<String> getImageList(){
        List<String> list = new ArrayList<>();
        for (int i = 0; i < getImageCount(); i++) {
            list.add(imageList.get(i));
        }
        return list;
    }

    public int getImageCount(){
        return isFull()?imageList.size():imageList.size()-1;
    }

    public void setMaxImageCount(int maxImageCount) {
        this.maxImageCount = maxImageCount;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        imageList.add(PLACE_HOLDER_PATH);
        adapter = new ManageImageAdapter(getContext(), R.layout.item_add_image, imageList);
        setAdapter(adapter);
    }

    private class ManageImageAdapter extends ArrayAdapter<String> {


        public ManageImageAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_add_image, parent, false);
                convertView.setTag(new ViewHolder(convertView));
            }
            holder = (ViewHolder) convertView.getTag();

            boolean isAddBtn = checkPosition(position);
            if (isAddBtn) {
                holder.llupload.setVisibility(VISIBLE);
                holder.ivImg.setVisibility(GONE);
                holder.ivDelete.setVisibility(GONE);
            } else {

                holder.llupload.setVisibility(GONE);
                holder.ivImg.setVisibility(VISIBLE);
                holder.ivDelete.setVisibility(VISIBLE);
                holder.ivDelete.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imageList.remove(position);
                        isFull = false;
                        if (imageList.size() == 0 || !TextUtils.equals(imageList.get(imageList.size() - 1), PLACE_HOLDER_PATH)) {
                            imageList.add(PLACE_HOLDER_PATH);
                        }
                        notifyDataSetChanged();
                    }
                });

            }
            //显示已选的图片

            String path = imageList.get(position);
            ImagePicker.getInstance().getImageLoader()
                    .displayImage(getContext(), path, holder.ivImg);

            return convertView;
        }

    }


    /**
     * 是否是添加按钮
     *
     * @param position
     * @return
     */
    public boolean checkPosition(int position) {
//            是最后一个item并且未达到最大图片数量
        if (position == imageList.size() - 1 && !isFull) {
            return true;
        }
        return false;
    }

    public void addImagePath(String path) {
        if (!isFull) {
            imageList.remove(imageList.size() - 1);
        }
        imageList.add(path);
        if (imageList.size() < maxImageCount) {
            imageList.add(PLACE_HOLDER_PATH);
            isFull = false;
        } else {
            isFull = true;
        }
        adapter.notifyDataSetChanged();
    }


    static class ViewHolder {
        ImageView ivDelete;
        ImageView ivImg;
        LinearLayout llupload;

        public ViewHolder(View convertView) {
            ivDelete = convertView.findViewById(R.id.iv_delete);
            ivImg = convertView.findViewById(R.id.iv_image);
            llupload = convertView.findViewById(R.id.ll_upload);
        }
    }

}
