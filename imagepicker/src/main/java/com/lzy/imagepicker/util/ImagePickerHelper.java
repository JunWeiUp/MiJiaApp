package com.lzy.imagepicker.util;

import android.app.Activity;
import android.content.Intent;
import android.util.SparseArray;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/2/1 0001.
 */

public class ImagePickerHelper {

    public interface Callback{
        void onImageCallback(ArrayList<ImageItem> list);
    }

    private SparseArray<Callback> sparseArray = new SparseArray<>();

    public void pickImage(Activity activity,int requestCode,boolean takePhoto, Callback callback){
        pickImages(activity, requestCode, takePhoto, callback);

    }

    /**
     * 选择照片
     * @param activity
     * @param requestCode
     * @param takePhoto 是否打开相机
     * @param callback
     */
    private void pickImages(Activity activity, int requestCode, boolean takePhoto, Callback callback) {
        sparseArray.put(requestCode,callback);

        Intent intent = new Intent(activity, ImageGridActivity.class);
        if(takePhoto){
            intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS,true);
        }
        activity.startActivityForResult(intent,requestCode);
    }


    public void handleResult(int requestCode, int resultCode, Intent data){
        if(resultCode == ImagePicker.RESULT_CODE_ITEMS ){
            int i = sparseArray.indexOfKey(requestCode);
            if(i>=0){
                sparseArray.get(requestCode).onImageCallback((ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS));
            }
        }
    }
}
