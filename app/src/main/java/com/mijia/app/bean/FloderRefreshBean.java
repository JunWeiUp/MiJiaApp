package com.mijia.app.bean;

import java.util.List;

public class FloderRefreshBean {


    /**
     * cmdType : speclist
     * diskId :
     * diskName :
     * fileInfo : [{"name":"/20190814-内测反馈-安卓.docx","size":"2195461","time":"1567072020","type":"file"},{"name":"/404Error.txt","size":"18","time":"1567072014","type":"file"},{"name":"/normal video.mp4","size":"1466091","time":"1567072018","type":"file"},{"name":"/QQ截图20190820190024.png","size":"14545","time":"1567071998","type":"file"},{"name":"/新建 PPT 演示文稿.ppt","size":"20992","time":"1567072009","type":"file"},{"name":"/测试文件夹","size":"0","time":"1567403857","type":"folder"},{"name":"/测试文件夹/假文本.txt","size":"0","time":"1567403867","type":"file"}]
     * gsId :
     * gsName :
     * path :
     * userId : 18dd6ab35ed740c17a50d66250bbc089
     * userName : 在路上
     */

    private String cmdType;
    private String diskId;
    private String diskName;
    private String gsId;
    private String gsName;
    private String path;
    private String userId;
    private String userName;
    private List<FileBean> fileInfo;

    public String getCmdType() {
        return cmdType;
    }

    public void setCmdType(String cmdType) {
        this.cmdType = cmdType;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getGsId() {
        return gsId;
    }

    public void setGsId(String gsId) {
        this.gsId = gsId;
    }

    public String getGsName() {
        return gsName;
    }

    public void setGsName(String gsName) {
        this.gsName = gsName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<FileBean> getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(List<FileBean> fileInfo) {
        this.fileInfo = fileInfo;
    }
//
//    public static class FileInfoBean {
//        /**
//         * name : /20190814-内测反馈-安卓.docx
//         * size : 2195461
//         * time : 1567072020
//         * type : file
//         */
//
//        private String name;
//        private String size;
//        private String time;
//        private String type;
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public String getSize() {
//            return size;
//        }
//
//        public void setSize(String size) {
//            this.size = size;
//        }
//
//        public String getTime() {
//            return time;
//        }
//
//        public void setTime(String time) {
//            this.time = time;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//    }
}
