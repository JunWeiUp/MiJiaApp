package com.mijia.app.ui.other.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.handong.framework.base.BaseFragment;
import com.handong.framework.base.BaseViewModel;
import com.hanlyjiang.library.fileviewer.FileViewer;
import com.lzy.imagepicker.util.ProviderUtil;
import com.mijia.app.MyApp;
import com.mijia.app.R;
import com.mijia.app.bean.UpRowTaskBean;
import com.mijia.app.bean.VideoVoiceFileBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.databinding.FragmentSelectVoiceVideoFileBinding;
import com.mijia.app.ui.other.activity.PlayMusicActivity;
import com.mijia.app.ui.other.activity.PlayVideoActivity;
import com.mijia.app.ui.other.activity.SelectVoiceVideoFileActivity;
import com.mijia.app.ui.other.adapter.VoiceVideoFileAdapter;
import com.mijia.app.utils.FileUtils;
import com.mijia.app.utils.NativeShareTool;
import com.mijia.app.utils.file.MediaBean;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.objectbox.Box;

import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_FILE;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_VIDEO;
import static com.mijia.app.dialog.UploadFileDialog.UPLOAD_VOICE;

/**
 * Created by Administrator on 2019/6/12.
 */

public class SelectVoiceVideoFileFragment extends BaseFragment<FragmentSelectVoiceVideoFileBinding, BaseViewModel> {

    private RecyclerView.OnScrollListener mOnScrollListener;

    /**
     * @param uploadType 上传类型
     * @param showType   展示数据类型 0 未上传 1 全部
     * @return
     */
    public static SelectVoiceVideoFileFragment instants(int uploadType, int showType) {
        SelectVoiceVideoFileFragment fragment = new SelectVoiceVideoFileFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", uploadType);
        bundle.putInt("showType", showType);
        fragment.setArguments(bundle);
        return fragment;
    }

    private NativeShareTool nativeShareTool;

    private int type;
    private int showType;

    private VoiceVideoFileAdapter mVoiceVideoFileAdapter;

    @Override
    public int getLayoutRes() {
        return R.layout.fragment_select_voice_video_file;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        nativeShareTool = NativeShareTool.getInstance(getActivity());
        type = getArguments() != null ? getArguments().getInt("type") : 0;
        showType = getArguments() != null ? getArguments().getInt("showType") : 0;
        mVoiceVideoFileAdapter = new VoiceVideoFileAdapter(R.layout.item_select_voice_video_file, type);
        binding.recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recycler.setAdapter(mVoiceVideoFileAdapter);
        mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (getActivity() != null) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        Glide.with(MyApp.getInstance()).resumeRequests();
                    } else {
                        Glide.with(MyApp.getInstance()).pauseRequests();
                    }
                }
            }
        };
        binding.recycler.addOnScrollListener(mOnScrollListener);


        initData();

        mVoiceVideoFileAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_select:
                    mVoiceVideoFileAdapter.getData().get(position).setSelect(!mVoiceVideoFileAdapter.getData().get(position).isSelect());
                    mVoiceVideoFileAdapter.notifyDataSetChanged();

                    for (VideoVoiceFileBean datum : mVoiceVideoFileAdapter.getData()) {
                        if (!datum.isSelect()) {
                            if (showType == 0) {
                                SelectVoiceVideoFileActivity.isNoUpdataAllSelect.set(false);
                            } else {
                                SelectVoiceVideoFileActivity.isAllAllSelect.set(false);
                            }
                            return;
                        }
                    }
                    if (showType == 0) {
                        SelectVoiceVideoFileActivity.isNoUpdataAllSelect.set(true);
                    } else {
                        SelectVoiceVideoFileActivity.isAllAllSelect.set(true);
                    }
                    break;
                case R.id.ll_item_view:
                    switch (type) {
                        case UPLOAD_VIDEO:
//                            Intent intent = new Intent();
//                            intent.setAction(Intent.ACTION_VIEW);
//                            //SDCard卡根目录下/DCIM/Camera/test.mp4文件
//                            Uri uri = null;
//
//
//                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
//
//                                String path =mVoiceVideoFileAdapter.getData().get(position).getPath();//该路径可以自定义
//                                File file = new File(path);
//                                uri = Uri.fromFile(file);
//                            } else {
//
//                                /**
//                                 * 7.0 调用系统相机拍照不再允许使用Uri方式，应该替换为FileProvider
//                                 * 并且这样可以解决MIUI系统上拍照返回size为0的情况
//                                 */
//                                uri = FileProvider.getUriForFile(getActivity(), ProviderUtil.getFileProviderName(getActivity()), new File(mVoiceVideoFileAdapter.getData().get(position).getPath()));
//                            }
//                            intent.setDataAndType(uri, "video/*");
//                            startActivity(intent);


                            startActivity(new Intent(getActivity(), PlayVideoActivity.class)
                                    .putExtra(Constants.VIDEOPATH, mVoiceVideoFileAdapter.getData().get(position).getPath()));
                            break;
                        case UPLOAD_FILE:
                            if (!StringUtils.isEmpty(mVoiceVideoFileAdapter.getData().get(position).getDisplayName())) {
                                if (mVoiceVideoFileAdapter.getData().get(position).getDisplayName().contains(".")) {
                                    String path = mVoiceVideoFileAdapter.getData().get(position).getPath();
                                    String strLater = mVoiceVideoFileAdapter.getData().get(position).getDisplayName();
                                    if (StringUtils.isEmpty(strLater)) {


                                    } else if (endWiths(strLater, Arrays.asList("doc", "DOCX", "DOC", "docx"))) {
//                                    Uri uri = Uri.fromFile(new File(mVoiceVideoFileAdapter.getData().get(position).getPath()));
//                                        FileViewer.viewFile(getActivity(), mVoiceVideoFileAdapter.getData().get(position).getPath());
                                        FileUtils.openFileReader(getActivity(), path);
                                    } else if (endWiths(strLater, Arrays.asList("execl"))) {
//                                        FileViewer.viewFile(getActivity(), mVoiceVideoFileAdapter.getData().get(position).getPath());
                                        FileUtils.openFileReader(getActivity(), path);
                                    } else if (endWiths(strLater, Arrays.asList("pdf"))) {
//                                        FileViewer.viewFile(getActivity(), mVoiceVideoFileAdapter.getData().get(position).getPath());
                                        FileUtils.openFileReader(getActivity(), path);
                                    } else if (endWiths(strLater, Arrays.asList("ppt"))) {
//                                        FileViewer.viewFile(getActivity(), mVoiceVideoFileAdapter.getData().get(position).getPath());
                                        FileUtils.openFileReader(getActivity(), path);
                                    } else if (endWiths(strLater, Arrays.asList("psd"))) {
//                                        FileViewer.viewFile(getActivity(), mVoiceVideoFileAdapter.getData().get(position).getPath());
                                        FileUtils.openFileReader(getActivity(), path);
                                    } else if (endWiths(strLater, Arrays.asList("txt"))) {
//                                        FileViewer.viewFile(getActivity(), mVoiceVideoFileAdapter.getData().get(position).getPath());
                                        FileUtils.openFileReader(getActivity(), path);
                                    } else if (endWiths(strLater, Arrays.asList("word"))) {
//                                        FileViewer.viewFile(getActivity(), mVoiceVideoFileAdapter.getData().get(position).getPath());
                                        FileUtils.openFileReader(getActivity(), path);
                                    } else if (endWiths(strLater, Arrays.asList("xls", "xlsx"))) {
//                                        FileViewer
//                                         FileUtils.openFileReader(getActivity(),path);.viewFile(getActivity(), mVoiceVideoFileAdapter.getData().get(position).getPath());
                                    } else if (endWiths(strLater, Arrays.asList("MP3", "WAV", "CDA", "WMA", "mp3", "AAC"))) {
                                        startActivity(new Intent(getActivity(), PlayMusicActivity.class));
                                    }
                                } else {
                                }
                            }
//
                            break;
                        case UPLOAD_VOICE:
                            startActivity(new Intent(getActivity(), PlayMusicActivity.class)
                                    .putExtra("MusicPath", mVoiceVideoFileAdapter.getData().get(position).getPath()));
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        });
    }

    public void allSelect() {
        for (VideoVoiceFileBean datum : mVoiceVideoFileAdapter.getData()) {
            datum.setSelect(showType == 0 ? SelectVoiceVideoFileActivity.isNoUpdataAllSelect.get() : SelectVoiceVideoFileActivity.isAllAllSelect.get());
        }
        mVoiceVideoFileAdapter.notifyDataSetChanged();
    }

    private boolean endWiths(String str, List<String> strings) {
        for (String string : strings) {
            if (str.endsWith(string)) {
                return true;
            }
        }
        return false;
    }

    private void initData() {
//        showLoading("");
        switch (type) {
            case UPLOAD_VIDEO://视频
                // TODO: 2019/11/30 0030  
                getAllVideo();
//                getVideos();
                break;
            case UPLOAD_FILE://文档
                getAllFile();
                break;
            case UPLOAD_VOICE://语音
                getMusic();
                break;
            default:
                break;
        }
        showLoading("");
    }


    /**
     * 获取本机视频列表
     *
     * @return
     */
    public void getVideos() {
        new Thread(() -> {

            String[] projection = {
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE
            };
            Cursor cursor = null;
            try {
                // String[] mediaColumns = { "_id", "_data", "_display_name",
                // "_size", "date_modified", "duration", "resolution" };
                cursor = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
                if (cursor == null) {
                    return;
                }

                Log.i("VIDEO_QUERY", "num===" + cursor.getCount());
                List<VideoVoiceFileBean> videoList = new ArrayList<>();

                try {
                    while (cursor.moveToNext()) {
                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                        String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                        long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                        String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)); //名字
                        long courseId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));

//                    MediaStore.Video.Thumbnails.getThumbnail(MyApp.getInstance().getContentResolver()
//                            , item.getCourseId()
//                            , MediaStore.Video.Thumbnails.MICRO_KIND, null);

                        String albumPath = "";
                        Cursor thumbCursor = getActivity().getApplicationContext().getContentResolver().query(
                                MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                null, MediaStore.Video.Thumbnails.VIDEO_ID
                                        + "=" + courseId, null, null);
                        if (thumbCursor.moveToFirst()) {
                            albumPath = thumbCursor.getString(thumbCursor
                                    .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                        }

                        VideoVoiceFileBean videoVoiceFileBean = new VideoVoiceFileBean();
                        videoVoiceFileBean.setCourseId(courseId);
                        videoVoiceFileBean.setDisplayName(displayName);
                        videoVoiceFileBean.setDuration(duration);
                        videoVoiceFileBean.setPath(path);
                        videoVoiceFileBean.setSize(size);
                        videoVoiceFileBean.setType(0);
                        videoVoiceFileBean.setThumb(albumPath);
                        videoList.add(videoVoiceFileBean);
                        Log.i("VIDEO_QUERY", "name===" + displayName + "\npath===" + path);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }

                getActivity().runOnUiThread(() -> {
                    if (videoList.size() == 0) {
                        binding.viewNoData.setVisibility(View.VISIBLE);
                    } else {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (showType == 0) {
                                    SelectVoiceVideoFileActivity.noUploadNum.set(videoList.size());
                                } else {
                                    SelectVoiceVideoFileActivity.allNum.set(videoList.size());
                                }
                            }
                        }, 300);


                        mVoiceVideoFileAdapter.replaceData(videoList);
                    }
                    dismissLoading();
                });
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }).start();

    }


    private void getAllVideo() {
        new Thread(() -> {
            String[] projection = {
                    MediaStore.Video.Media._ID,
                    MediaStore.Video.Media.DATA,
                    MediaStore.Video.Media.DISPLAY_NAME,
                    MediaStore.Video.Media.DURATION,
                    MediaStore.Video.Media.SIZE
            };
            //全部视频
            String where = MediaStore.Video.Media.MIME_TYPE + "=? or "
                    + MediaStore.Video.Media.MIME_TYPE + "=? or "
                    + MediaStore.Video.Media.MIME_TYPE + "=? or "
                    + MediaStore.Video.Media.MIME_TYPE + "=? or "
                    + MediaStore.Video.Media.MIME_TYPE + "=? or "
                    + MediaStore.Video.Media.MIME_TYPE + "=? or "
                    + MediaStore.Video.Media.MIME_TYPE + "=? or "
                    + MediaStore.Video.Media.MIME_TYPE + "=? or "
                    + MediaStore.Video.Media.MIME_TYPE + "=?";
            String[] whereArgs = {"video/mp4", "video/3gp", "video/x-msvideo", "audio/x-pn-realaudio", "video/vob", "video/flv",
                    "video/mkv", "video/mov", "video/mpg"};

            Cursor cursor = MyApp.getInstance().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    projection, where, whereArgs, MediaStore.Video.Media.DATE_ADDED + " DESC ");

            //SaveLogsUtils.initData("VIDEO_QUERY" + "start");
            if (cursor == null) {
                //SaveLogsUtils.initData("VIDEO_QUERY" + "error (cursor==null)");
                return;
            }


            //SaveLogsUtils.initData("VIDEO_QUERY" + "quering (cursor count ===)" + cursor.getCount());

            List<VideoVoiceFileBean> videoList = new ArrayList<>();

            int index = 0;
            try {
                while (cursor.moveToNext()) {
                    //SaveLogsUtils.initData("VIDEO_QUERY" + 1);
                    index++;
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)); // 大小
                    //SaveLogsUtils.initData("VIDEO_QUERY" + 2);

                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)); // 路径
                    //SaveLogsUtils.initData("VIDEO_QUERY" + 3);

                    long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)); // 时长
                    //SaveLogsUtils.initData("VIDEO_QUERY" + 4);

                    String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)); //名字
                    //SaveLogsUtils.initData("VIDEO_QUERY" + 5);

                    int courseId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID));
//                    long courseId = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                    //SaveLogsUtils.initData("VIDEO_QUERY" + 6 + "MediaStore.Video.Media._ID===" + courseId);
//                    MediaStore.Video.Thumbnails.getThumbnail(MyApp.getInstance().getContentResolver()
//                            , item.getCourseId()
//                            , MediaStore.Video.Thumbnails.MICRO_KIND, null);

                    String albumPath = "";
                    //SaveLogsUtils.initData("VIDEO_QUERY" + 7);


                    try {
                        Cursor thumbCursor =
                                MyApp.getInstance().getContentResolver().query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,
                                        new String[]{MediaStore.Video.Media._ID, MediaStore.Video.Thumbnails.DATA}, MediaStore.Video.Thumbnails.VIDEO_ID
                                                + "=" + courseId, null, null);
                        //SaveLogsUtils.initData("VIDEO_QUERY" + 8 + "thumbCursor.getCount===" + thumbCursor.getCount());
                        if (thumbCursor.moveToFirst()) {
                            //SaveLogsUtils.initData("VIDEO_QUERY" + 9);
                            albumPath = thumbCursor.getString(thumbCursor
                                    .getColumnIndex(MediaStore.Video.Thumbnails.DATA));
                        }
                    } catch (Exception e) {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "ErrorVideoName===" + displayName + "\n"
//                                + "ErrorVideoPath === " + path);

                        //SaveLogsUtils.initData("VIDEO_QUERY" + index + "thumbCursor.error===" + e.getMessage());
                    }

                    //SaveLogsUtils.initData("VIDEO_QUERY" + 10);

                    VideoVoiceFileBean videoVoiceFileBean = new VideoVoiceFileBean();
                    videoVoiceFileBean.setCourseId(courseId);
                    videoVoiceFileBean.setDisplayName(displayName);
                    videoVoiceFileBean.setDuration(duration);
                    videoVoiceFileBean.setPath(path);
                    videoVoiceFileBean.setSize(size);
                    videoVoiceFileBean.setType(0);
                    videoVoiceFileBean.setThumb(albumPath);
                    videoList.add(videoVoiceFileBean);
                    Log.i("VIDEO_QUERY", index + "   name===" + displayName + "\npath===" + path);
                    //SaveLogsUtils.initData("VIDEO_QUERY" + index + "name===" + displayName + "\npath===" + path);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //SaveLogsUtils.initData("VIDEO_QUERY" + index + "while error (" + e.getMessage() + ")");
            } finally {
                //SaveLogsUtils.initData("VIDEO_QUERY" + index + "while finfish  count==" + videoList.size());
                cursor.close();
            }


            //SaveLogsUtils.initData("VIDEO_QUERY" + "video number == " + videoList.size());

            getActivity().runOnUiThread(() -> {


                Box<UpRowTaskBean> upRowTaskBeanBox = MyApp.getInstance().getBoxStore().boxFor(UpRowTaskBean.class);
                List<UpRowTaskBean> upRowTaskBeans = upRowTaskBeanBox.getAll();


                List<VideoVoiceFileBean> realMediaBeanList = new ArrayList<>();
                for (VideoVoiceFileBean mediaBean : videoList) {
                    String mediaPath = mediaBean.getPath();
                    boolean ishave = false;
                    for (UpRowTaskBean upRowTaskBean : upRowTaskBeans) {
                        if (upRowTaskBean.getLocationPath().equals(mediaPath)) {
                            ishave = true;
                        }
                    }
                    if (!ishave) {
                        realMediaBeanList.add(mediaBean);
                    }
                }


                if (showType == 0) {

                    if (realMediaBeanList.size() == 0) {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number == 0");
                        binding.viewNoData.setVisibility(View.VISIBLE);
                    } else {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number === " + realMediaBeanList.size());
                        mVoiceVideoFileAdapter.replaceData(realMediaBeanList);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SelectVoiceVideoFileActivity.noUploadNum.set(realMediaBeanList.size());
                            }
                        }, 300);


                    }
                } else {
                    if (videoList.size() == 0) {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number == 0");
                        binding.viewNoData.setVisibility(View.VISIBLE);
                    } else {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number === " + videoList.size());
                        mVoiceVideoFileAdapter.replaceData(videoList);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SelectVoiceVideoFileActivity.allNum.set(videoList.size());
                            }
                        }, 300);


                    }
                }
                dismissLoading();
            });

        }).start();
    }


    private void getAllFile() {

        new Thread(() -> {
            List<VideoVoiceFileBean> fileList = new ArrayList<>();
            // 扫描files文件库
            String[] projection = {
                    MediaStore.Files.FileColumns._ID,
                    MediaStore.Files.FileColumns.DATA,
                    MediaStore.Files.FileColumns.TITLE,
                    MediaStore.Files.FileColumns.SIZE,
                    MediaStore.Files.FileColumns.MEDIA_TYPE,
                    MediaStore.Files.FileColumns.MIME_TYPE,
                    MediaStore.Files.FileColumns.PARENT
            };
            //全部文件
            String where = MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=? or "
                    + MediaStore.Files.FileColumns.MIME_TYPE + "=?";
            String[] whereArgs = {"application/msword",
                    "application/doc", "appl/text", "application/vnd.msword", "application/vnd.ms-word", "application/winword",
                    "application/word", "application/x-msw6", "application/x-msword",
                    "application/mspowerpoint",
                    "application/ms-powerpoint", "application/powerpoint", "application/x-powerpoint", "application/mspowerpnt", "application/vnd-mspowerpoint",
                    "application/vnd.ms-powerpoint", "application/x-mspowerpoint", "application/x-m",
                    "application/excel", "application/vnd.ms-excel", "application/msexcell", "application/x-msexcel", "application/x-excel",
                    "application/x-msexcel", "application/x-dos_ms_excel", "application/xls",
                    "application/pdf", "application/x-pdf", "application/acrobat", "applications/vnd.pdf", "text/pdf",
                    "text/x-pdf", "application/x-bzpdf", "application/x-gzpdf",
                    "application/msword",
                    "application/dot", "application/x-dot", "application/doc", "application/microsoft_word", "application/mswor2c",
                    " application/x-msword", "text/plain", "zz-application/zz-winassoc-dot"};

//            Cursor cursor1 = getActivity().getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                    projection, where, whereArgs, MediaStore.Video.Media.DATE_ADDED + " DESC ");

            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(MediaStore.Files.getContentUri("external")
                        , projection
                        , where, whereArgs, MediaStore.Files.FileColumns.DATE_ADDED + " DESC ");

                while (cursor.moveToNext()) {
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)); // 大小
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)); // 路径
                    String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.TITLE)); //名字
                    long courseId = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));

                    VideoVoiceFileBean videoVoiceFileBean = new VideoVoiceFileBean();
                    videoVoiceFileBean.setCourseId(courseId);
                    videoVoiceFileBean.setDisplayName(path.split("\\/")[path.split("\\/").length - 1]);
                    videoVoiceFileBean.setPath(path);
                    videoVoiceFileBean.setSize(size);
                    videoVoiceFileBean.setType(1);
                    fileList.add(videoVoiceFileBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            if (getActivity() == null) {
                return;
            }
            getActivity().runOnUiThread(() -> {


                Box<UpRowTaskBean> upRowTaskBeanBox = MyApp.getInstance().getBoxStore().boxFor(UpRowTaskBean.class);
                List<UpRowTaskBean> upRowTaskBeans = upRowTaskBeanBox.getAll();


                List<VideoVoiceFileBean> realMediaBeanList = new ArrayList<>();
                for (VideoVoiceFileBean mediaBean : fileList) {
                    String mediaPath = mediaBean.getPath();
                    boolean ishave = false;
                    for (UpRowTaskBean upRowTaskBean : upRowTaskBeans) {
                        if (upRowTaskBean.getLocationPath().equals(mediaPath)) {
                            ishave = true;
                        }
                    }
                    if (!ishave) {
                        realMediaBeanList.add(mediaBean);
                    }
                }

                if (showType == 0) {

                    if (realMediaBeanList.size() == 0) {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number == 0");
                        binding.viewNoData.setVisibility(View.VISIBLE);
                    } else {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number === " + realMediaBeanList.size());
                        mVoiceVideoFileAdapter.replaceData(realMediaBeanList);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SelectVoiceVideoFileActivity.noUploadNum.set(realMediaBeanList.size());
                            }
                        }, 300);


                    }
                } else {
                    if (fileList.size() == 0) {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number == 0");
                        binding.viewNoData.setVisibility(View.VISIBLE);
                    } else {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number === " + fileList.size());
                        mVoiceVideoFileAdapter.replaceData(fileList);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SelectVoiceVideoFileActivity.allNum.set(fileList.size());
                            }
                        }, 300);


                    }
                }

//
//                if (fileList.size() == 0) {
//                    binding.viewNoData.setVisibility(View.VISIBLE);
//                } else {
//
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            if (showType == 0) {
//                                SelectVoiceVideoFileActivity.noUploadNum.set(fileList.size());
//                            } else {
//                                SelectVoiceVideoFileActivity.allNum.set(fileList.size());
//                            }
//                        }
//                    }, 300);
//
//                    mVoiceVideoFileAdapter.replaceData(fileList);
//                }
                dismissLoading();
            });
        }).start();

    }


    private void getMusic() {
        new Thread(() -> {

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.ALBUM,
                    MediaStore.Audio.Media.YEAR,
                    MediaStore.Audio.Media.MIME_TYPE,
                    MediaStore.Audio.Media.SIZE,
                    MediaStore.Audio.Media.DATA
            };
            //全部视频
            String where = MediaStore.Audio.Media.MIME_TYPE + "=? or "
                    + MediaStore.Audio.Media.MIME_TYPE + "=?";
            String[] whereArgs = {"audio/mpeg", "audio/x-ms-wma"};

//            Cursor cursor = getActivity().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//                    projection, where, whereArgs, MediaStore.Audio.Media.DATE_ADDED + " DESC ");

            Cursor cursor = getActivity().getContentResolver().query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection, where, whereArgs,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

            List<VideoVoiceFileBean> voiceList = new ArrayList<>();
            if (cursor == null) {
                return;
            }

            try {
                while (cursor.moveToNext()) {
                    long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)); // 大小
                    String path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)); // 路径
                    long duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)); // 时长
                    String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)); //名字
                    long courseId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

//                    // 歌曲格式
//                    if ("audio/mpeg".equals(cursor.getString(7).trim())) {
//                        displayName=displayName+"mp3";
//                    } else if ("audio/x-ms-wma".equals(cursor.getString(7).trim())) {
//                        displayName=displayName+"wma";
//                    }

                    VideoVoiceFileBean videoVoiceFileBean = new VideoVoiceFileBean();
                    videoVoiceFileBean.setCourseId(courseId);
                    videoVoiceFileBean.setDisplayName(displayName);
                    videoVoiceFileBean.setDuration(duration);
                    videoVoiceFileBean.setPath(path);
                    videoVoiceFileBean.setSize(size);
                    videoVoiceFileBean.setType(2);
                    voiceList.add(videoVoiceFileBean);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                cursor.close();
            }

            getActivity().runOnUiThread(() -> {



                Box<UpRowTaskBean> upRowTaskBeanBox = MyApp.getInstance().getBoxStore().boxFor(UpRowTaskBean.class);
                List<UpRowTaskBean> upRowTaskBeans = upRowTaskBeanBox.getAll();


                List<VideoVoiceFileBean> realMediaBeanList = new ArrayList<>();
                for (VideoVoiceFileBean mediaBean : voiceList) {
                    String mediaPath = mediaBean.getPath();
                    boolean ishave = false;
                    for (UpRowTaskBean upRowTaskBean : upRowTaskBeans) {
                        if (upRowTaskBean.getLocationPath().equals(mediaPath)) {
                            ishave = true;
                        }
                    }
                    if (!ishave) {
                        realMediaBeanList.add(mediaBean);
                    }
                }

                if (showType == 0) {

                    if (realMediaBeanList.size() == 0) {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number == 0");
                        binding.viewNoData.setVisibility(View.VISIBLE);
                    } else {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number === " + realMediaBeanList.size());
                        mVoiceVideoFileAdapter.replaceData(realMediaBeanList);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SelectVoiceVideoFileActivity.noUploadNum.set(realMediaBeanList.size());
                            }
                        }, 300);


                    }
                } else {
                    if (voiceList.size() == 0) {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number == 0");
                        binding.viewNoData.setVisibility(View.VISIBLE);
                    } else {
                        //SaveLogsUtils.initData("VIDEO_QUERY" + "video number === " + voiceList.size());
                        mVoiceVideoFileAdapter.replaceData(voiceList);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                SelectVoiceVideoFileActivity.allNum.set(voiceList.size());
                            }
                        }, 300);


                    }
                }



//
//
//                if (voiceList.size() == 0) {
//                    binding.viewNoData.setVisibility(View.VISIBLE);
//                } else {
//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            if (showType == 0) {
//                                SelectVoiceVideoFileActivity.noUploadNum.set(voiceList.size());
//                            } else {
//                                SelectVoiceVideoFileActivity.allNum.set(voiceList.size());
//                            }
//                        }
//                    }, 300);
//
//
//                    mVoiceVideoFileAdapter.replaceData(voiceList);
//                }
                dismissLoading();
            });

        }).start();
    }

    public List<VideoVoiceFileBean> getSelectedList() {
        List<VideoVoiceFileBean> selectedList = new ArrayList<>();

        List<VideoVoiceFileBean> list = mVoiceVideoFileAdapter.getData();
        for (VideoVoiceFileBean itemValue : list) {
            if (itemValue.isSelect()) {
                selectedList.add(itemValue);
            }
        }
        return selectedList;
    }


    @Override
    public void onDestroy() {
        binding.recycler.removeOnScrollListener(mOnScrollListener);
        super.onDestroy();
    }
}
