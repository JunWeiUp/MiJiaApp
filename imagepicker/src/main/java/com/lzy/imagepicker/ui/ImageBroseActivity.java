package com.lzy.imagepicker.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.R;
import com.lzy.imagepicker.loader.ImageLoader;
import com.lzy.imagepicker.util.Utils;
import com.lzy.imagepicker.view.ViewPagerFixed;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;


/**
 * 图片浏览
 *
 * @ClassName:ImageBroseActivity
 * @PackageName:com.bluemobi.wenwanstyle.activities.common
 * @Create On 2017/9/20 0020   09:25
 * @Site:http://www.handongkeji.com
 * @author:xuchuanting
 * @Copyrights 2017/9/20 0020 handongkeji All rights reserved.
 */
public class ImageBroseActivity extends AppCompatActivity {




    public static final String PICS = "pics";
    public static final String CURRENT_POSITION = "current_position";
    private ArrayList<String> pics;
    private int position;
    private int screenWidth;
    private int screenHeight;
    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_brose);
//        StatusBarUtil.setColor(this,0xff666666);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        pics = intent.getStringArrayListExtra(PICS);
        position = intent.getIntExtra(CURRENT_POSITION, 0);


        ViewPagerFixed viewPager = (ViewPagerFixed) findViewById(R.id.view_pager);
        tvTitle = (TextView) findViewById(R.id.tv_title);


        DisplayMetrics dm = Utils.getScreenPix(this);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        String poiInfo = (position+1)+"/"+pics.size();
        tvTitle.setText(poiInfo);

        viewPager.setAdapter(new ImageAdapter(pics));
        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                String poiInfo = (position+1)+"/"+pics.size();
                tvTitle.setText(poiInfo);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public static void start(Activity context, ArrayList<String> pics, int position){
        Intent intent = new Intent(context, ImageBroseActivity.class);
        intent.putStringArrayListExtra(PICS,pics);
        intent.putExtra(CURRENT_POSITION,position);
        context.startActivity(intent);
        context.overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);

    }

    private class ImageAdapter extends PagerAdapter{

        private List<String> list;

        public ImageAdapter(List<String> list) {
            this.list = list==null?new ArrayList<String>():list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }



        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            LayoutInflater inflater = LayoutInflater.from(container.getContext());
            View view = inflater.inflate(R.layout.image_brose, null);
            PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);
            final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
            String pic = list.get(position);
            if(!TextUtils.isEmpty(pic)){
                pic=pic.replace("_mid","_big");
            }


            progressBar.setVisibility(View.GONE);


            ImagePicker.getInstance()
                    .getImageLoader()
                    .displayImage(ImageBroseActivity.this, pic, photoView, screenWidth, screenHeight
                            , new ImageLoader.OnImageResultListener() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onFailed() {
                            progressBar.setVisibility(View.GONE);
                        }
                    });

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
            container.removeView((View) object);
        }


    }


    @Override
    public void onBackPressed() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN,WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        ActivityCompat.finishAfterTransition(this);
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }
}
