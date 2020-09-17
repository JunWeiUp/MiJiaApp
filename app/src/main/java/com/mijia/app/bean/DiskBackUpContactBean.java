package com.mijia.app.bean;

import java.util.List;

public class DiskBackUpContactBean {


    /**
     * userName : test_user
     * userId : aaaaaaaaaa
     * gsId : bbbbbbbbb
     * gsName : test_gs
     * diskName : test_disk
     * diskId : ccccccccc
     * addBook : [{"bookName":"test_book1","bookTime":"2019-05-10 20:03:35","bookPath":"/local/addbook/"}]
     */



    private String userName;
    private String userId;
    private String gsId;
    private String gsName;
    private String diskName;
    private String diskId;
    private List<AddBookBean> addBook;

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

    public String getDiskName() {
        return diskName;
    }

    public void setDiskName(String diskName) {
        this.diskName = diskName;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public List<AddBookBean> getAddBook() {
        return addBook;
    }

    public void setAddBook(List<AddBookBean> addBook) {
        this.addBook = addBook;
    }

    public static class AddBookBean {
        /**
         * bookName : test_book1
         * bookTime : 2019-05-10 20:03:35
         * bookPath : /local/addbook/
         */

        private String bookName;
        private String bookTime;
        private String bookPath;
        private String bookCount;
        private String bookSize;


        public String getBookCount() {
            return bookCount;
        }

        public void setBookCount(String bookCount) {
            this.bookCount = bookCount;
        }

        public String getBookSize() {
            return bookSize;
        }

        public void setBookSize(String bookSize) {
            this.bookSize = bookSize;
        }

        public String getBookName() {
            return bookName;
        }

        public void setBookName(String bookName) {
            this.bookName = bookName;
        }

        public String getBookTime() {
            return bookTime;
        }

        public void setBookTime(String bookTime) {
            this.bookTime = bookTime;
        }

        public String getBookPath() {
            return bookPath;
        }

        public void setBookPath(String bookPath) {
            this.bookPath = bookPath;
        }
    }
}
