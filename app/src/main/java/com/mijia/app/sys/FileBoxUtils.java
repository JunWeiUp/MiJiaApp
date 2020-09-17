package com.mijia.app.sys;

import android.util.Log;

import com.blankj.utilcode.util.StringUtils;
import com.google.gson.Gson;
import com.mijia.app.MyApp;
import com.mijia.app.bean.FileBean;
import com.mijia.app.bean.FileBean_;
import com.mijia.app.bean.FloderRefreshBean;
import com.mijia.app.constants.Constants;
import com.mijia.app.utils.file.SaveLogsUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

public class FileBoxUtils {

    /**
     * 根据文件夹路径 获取该文件夹下所有子文件
     *
     * @param path
     * @return
     */
    public static List<FileBean> getFileListByPath(String path) {
//        String getPath = path.substring(0, path.lastIndexOf("/"));
        List<FileBean> fileBeanList = new ArrayList<>();
        Box<FileBean> mFileBox = MyApp.getInstance().getBoxStore().boxFor(FileBean.class);
        QueryBuilder<FileBean> queryBuilder = mFileBox.query();
        queryBuilder.equal(FileBean_.GsId, Objects.requireNonNull(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get()))
                .and().equal(FileBean_.diskId, Objects.requireNonNull(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get()));
        List<FileBean> allFileBean = queryBuilder.build().find();
        for (FileBean fileBean : allFileBean) {
            String thePath = fileBean.getName();

//            if (fileBean.getType().equals("file")) {
            String floderPath = thePath.substring(0, thePath.lastIndexOf("/"));
            String fileName = thePath.substring(thePath.lastIndexOf("/") + 1);
            if (path.equals(floderPath)) {
                if (!StringUtils.isEmpty(fileName)) {
                    fileBeanList.add(fileBean);
                }
            }
//            } else {
//                String floderPath = thePath.substring(0, thePath.lastIndexOf("/"));
//                floderPath = floderPath.substring(0, floderPath.lastIndexOf("/"));
//                String fileName = thePath.substring(0, thePath.lastIndexOf("/"));
//                fileName = fileName.substring(fileName.lastIndexOf("/")+ 1);
//                if (getPath.equals(floderPath)) {
//                    if (!StringUtils.isEmpty(fileName)) {
//                        fileBeanList.add(fileBean);
//                    }
//                }
//            }


        }
        return fileBeanList;
    }


    /**
     * 根据路径判断该文件夹下面是否有 该名字的文件
     *
     * @param path
     * @param name
     * @return
     */
    public static boolean isHaveFileByFloder(String path, String name) {
        path = path.substring(0, path.lastIndexOf("/"));
        for (FileBean fileBean : getFileListByPath(path)) {
            String nowName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
            if (nowName.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 刷新单个文件夹内容
     *
     * @param path 文件夹路径
     * @param json 新数据
     * @return
     */
    public static List<FileBean> refreshFloderByPath(String path, String json) {
        List<FileBean> floderListBean = new ArrayList<>();
        Box<FileBean> mFileBox = MyApp.getInstance().getBoxStore().boxFor(FileBean.class);

        QueryBuilder<FileBean> queryBuilder = mFileBox.query();
        queryBuilder.equal(FileBean_.GsId, Objects.requireNonNull(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get()))
                .and().equal(FileBean_.diskId, Objects.requireNonNull(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get()));


        try {

            FloderRefreshBean floderRefreshBean = new Gson().fromJson(json, FloderRefreshBean.class);
            if (floderRefreshBean.getFileInfo() == null) {
                return new ArrayList<FileBean>();
            }

            if (StringUtils.isEmpty(path)) {
                mFileBox.removeAll();
            }else{
                List<FileBean> allFileBean = queryBuilder.build().find();
                for (FileBean fileBean : allFileBean) {
                    String thePath = fileBean.getName();
//            String filePath = fileBean.getName();
                    String floderPath = thePath.substring(0, thePath.lastIndexOf("/"));
                    String fileName = thePath.substring(thePath.lastIndexOf("/") + 1);
                    if (path.equals(floderPath)) {
//            if (filePath.contains(path)) {
                        if (!StringUtils.isEmpty(fileName)) {
                            mFileBox.remove(fileBean);
                        }
                    }
                }
            }



            for (FileBean fileBean : floderRefreshBean.getFileInfo()) {
                fileBean.setDiskId(floderRefreshBean.getDiskId());
                fileBean.setGsId(floderRefreshBean.getGsId());
                floderListBean.add(fileBean);
            }

            mFileBox.put(floderListBean);

            List<FileBean> list = new ArrayList<>();
            for (FileBean fileBean : floderListBean) {
                String thePath = fileBean.getName();

//            if (fileBean.getType().equals("file")) {
                String floderPath = thePath.substring(0, thePath.lastIndexOf("/"));
                String fileName = thePath.substring(thePath.lastIndexOf("/") + 1);
                if (path.equals(floderPath)) {
                    if (!StringUtils.isEmpty(fileName)) {
                        list.add(fileBean);
                    }
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }

    }


    /**
     * 根据文件夹路径 获取该文件夹 下面所有子文件夹
     *
     * @param path
     * @return
     */
    public static List<FileBean> getFloderByPath(String path) {
        List<FileBean> floderListBean = new ArrayList<>();
        Box<FileBean> mFileBox = MyApp.getInstance().getBoxStore().boxFor(FileBean.class);
        QueryBuilder<FileBean> queryBuilder = mFileBox.query();
        queryBuilder.equal(FileBean_.GsId, Objects.requireNonNull(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get()))
                .and().equal(FileBean_.diskId, Objects.requireNonNull(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get()));

        Log.i("CreateFloder", "RefreshList input path === " + path);
//        SaveLogsUtils.initData("CreateFloder" + "RefreshList input path === " + path);

        List<FileBean> allFileBean = queryBuilder.build().find();
        for (FileBean fileBean : allFileBean) {
            String thePath = fileBean.getName();
//            Log.i("CreateFloder", "RefreshList thePath  === " + thePath);
//            SaveLogsUtils.initData("CreateFloder" + "RefreshList thePath  === " + thePath);
            if (StringUtils.isEmpty(thePath)) {
                continue;
            }
            try {
                String floderPath = thePath.substring(0, thePath.lastIndexOf("/"));
                String fileName = thePath.substring(thePath.lastIndexOf("/") + 1);
                if (path.equals(floderPath) && !StringUtils.isEmpty(fileName)) {
                    if (!fileBean.getType().equals("file")) {
                        floderListBean.add(fileBean);
                    }
                }
            } catch (Exception e) {
                Log.i("CreateFloder", "Error  === " + e.getMessage() + "\nthePath ===" + thePath);
                SaveLogsUtils.initData("CreateFloder" + "thePath===" + thePath + "\nError  === " + e.getMessage());
            }

        }
        return floderListBean;
    }


    public static void createNewFloder(String path) {
        Box<FileBean> mFileBox = MyApp.getInstance().getBoxStore().boxFor(FileBean.class);
        Long time = System.currentTimeMillis() / 1000;
        FileBean fileBean = new FileBean(
                Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get(),
                Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get(),
                "0",
                path,
                "folder",
                time
        );
        Log.i("CreateFloder", "time===" + time);
        Log.i("CreateFloder", "path===" + path);
        SaveLogsUtils.initData("CreateFloder" + "createPathName === " + path);
        mFileBox.put(fileBean);
    }

    /**
     * 根据文件类型 获取所有该类型的文件
     *
     * @param type 0:照片 1：视频 2：文档 3：音频 4：其他
     * @return
     */
    public static List<FileBean> getFileByType(int type) {
        List<FileBean> fileBeanList = new ArrayList<>();
        Box<FileBean> mFileBox = MyApp.getInstance().getBoxStore().boxFor(FileBean.class);
        QueryBuilder<FileBean> queryBuilder = mFileBox.query();
        queryBuilder.equal(FileBean_.GsId, Objects.requireNonNull(Constants.SYS_CONTANTS_BEAN.SeleectedMiDunId.get()))
                .and().equal(FileBean_.diskId, Objects.requireNonNull(Constants.SYS_CONTANTS_BEAN.SelectedDiskId.get()))
                .and().equal(FileBean_.type, "file");
        List<FileBean> allFileBean = queryBuilder.build().find();
        for (FileBean fileBean : allFileBean) {
            String fileName = fileBean.getName().substring(fileBean.getName().lastIndexOf("/") + 1);
            if (fileName.contains(".")) {
                int length = fileName.split("\\.").length;
                String fileType = length > 0 ? fileName.split("\\.")[length - 1] : "";
                switch (type) {
                    case 0://照片
                        if (isEqual(fileType, Arrays.asList("bmp", "gif", "jpg", "tif",
                                "jpeg", "pic", "png", "BMP", "GIF", "JPG", "TIF",
                                "JPEG", "PIC", "PNG"))) {
                            fileBeanList.add(fileBean);
                        }
                        break;
                    case 1://视频
                        if (isEqual(fileType, Arrays.asList("mp4", "mpg"
                                , "mpe", "dat"
                                , "mpeg", "mov"
                                , "asf", "avi"
                                , "rmvb", "MP4"
                                , "MPG", "MPE"
                                , "DAT", "MPEG"
                                , "MOV", "ASF"
                                , "AVI", "RMVB"
                                , "rm", "RM"
                                , "mpeg1", "MPEG1"
                                , "mpeg2", "MPEG2"
                                , "mpeg3", "MPEG3"
                                , "mpeg4", "MPEG4"
                                , "mtv", "MTV"
                                , "dat", "DAT"
                                , "wmv", "WMV"
                                , "amv", "AMV"
                                , "dmv", "DMV"
                                , "flv", "FLV"
                                , "3gp", "3GP"
                        ))) {
                            fileBeanList.add(fileBean);
                        }
                        break;
                    case 2://文档
                        if (isEqual(fileType, Arrays.asList("psd", "pdf", "html", "txt", "pptx", "ppt"
                                , "xls", "xlsx", "docx", "doc", "PSD", "PDF", "HTML", "TXT", "PPTX", "PPT"
                                , "XLS", "XLSX", "DOCX", "DOC"))) {
                            fileBeanList.add(fileBean);
                        }
                        break;
                    case 3://音频
                        if (isEqual(fileType, Arrays.asList("wav", "m4a", "mp3", "wma", "acc", "WAV", "M4A", "MP3", "WMA", "ACC"))) {
                            fileBeanList.add(fileBean);
                        }
                        break;
                    case 4://其他
                        if (!isEqual(fileType, Arrays.asList("wav", "m4a", "mp3", "wma", "acc"
                                , "psd", "pdf", "html", "txt", "pptx", "ppt", "xls", "xlsx", "docx", "doc",
                                "mp4", "mpg", "mpe", "dat", "mpeg", "mov", "asf", "avi", "rmvb",
                                "bmp", "gif", "jpg", "tif", "jpeg", "pic", "png"
                                , "rm", "RM"
                                , "mpeg1", "MPEG1"
                                , "mpeg2", "MPEG2"
                                , "mpeg3", "MPEG3"
                                , "mpeg4", "MPEG4"
                                , "mtv", "MTV"
                                , "dat", "DAT"
                                , "wmv", "WMV"
                                , "amv", "AMV"
                                , "dmv", "DMV"
                                , "flv", "FLV"
                                , "3gp", "3GP"))) {
                            fileBeanList.add(fileBean);
                        }

                        break;
                    default:
                        break;
                }
            } else {

            }

        }
        return fileBeanList;
    }

    private static boolean isEqual(String str, List<String> list) {
        for (String s : list) {
            if (str.equals(s)) {
                return true;
            }
        }
        return false;
    }

}
