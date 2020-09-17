package com.mijia.app.bean;

import com.handong.framework.base.BaseBean;

import java.util.List;

public class EmpowerListBean extends BaseBean {


    private List<WaitListBean> agreedList;
    private List<WaitListBean> refuseList;
    private List<WaitListBean> waitList;

    public List<WaitListBean> getAgreedList() {
        return agreedList;
    }

    public void setAgreedList(List<WaitListBean> agreedList) {
        this.agreedList = agreedList;
    }

    public List<WaitListBean> getRefuseList() {
        return refuseList;
    }

    public void setRefuseList(List<WaitListBean> refuseList) {
        this.refuseList = refuseList;
    }

    public List<WaitListBean> getWaitList() {
        return waitList;
    }

    public void setWaitList(List<WaitListBean> waitList) {
        this.waitList = waitList;
    }

    public static class WaitListBean {
        /**
         * type : 2
         * createDate : 2019-06-25 10:46:51
         * nickName : 456nickname
         * fileName : filename
         */
        private boolean isSelected = false;

        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        private String type;
        private String createDate;
        private String nickName;
        private String fileName;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }
}
