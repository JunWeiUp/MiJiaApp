package com.mijia.app.bean;

import com.handong.framework.base.BaseBean;

import java.util.List;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;

public class HomeDataBean extends BaseBean {


    /**
     * jsonInfo : {"gsInfo":[{"gsStatu":"online","gsId":"bbbbbbbbbbbbbb","gsName":"test_gs",
     * "diskInfo":[{"diskName":"test_disk","diskSize":"100","diskStatu":"open",
     * "diskInuse":"50","fileInfo":[{"size":"15","name":"/test.txt","type":"file"},{"size":"0","name":"/","type":"folder"}],"diskId":"cccccccccccc"}]}],"userName":"test_user","userId":"aaaaaaaaaa"}
     */

    private JsonInfoBean jsonInfo;

    public JsonInfoBean getJsonInfo() {
        return jsonInfo;
    }

    public void setJsonInfo(JsonInfoBean jsonInfo) {
        this.jsonInfo = jsonInfo;
    }


    public static class JsonInfoBean {
        /**
         * gsInfo : [{"gsStatu":"online","gsId":"bbbbbbbbbbbbbb","gsName":"test_gs","diskInfo":[{"diskName":"test_disk","diskSize":"100","diskStatu":"open","diskInuse":"50","fileInfo":[{"size":"15","name":"/test.txt","type":"file"},{"size":"0","name":"/","type":"folder"}],"diskId":"cccccccccccc"}]}]
         * userName : test_user
         * userId : aaaaaaaaaa
         */

        private String userName;
        private String userId;
        private List<GsInfoBean> gsInfo;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public List<GsInfoBean> getGsInfo() {
            return gsInfo;
        }

        public void setGsInfo(List<GsInfoBean> gsInfo) {
            this.gsInfo = gsInfo;
        }


    }
}
