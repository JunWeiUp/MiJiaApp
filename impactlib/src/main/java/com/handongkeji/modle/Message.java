package com.handongkeji.modle;

import java.util.List;

/**
 * 动态消息 实体类
 *
 * @author chengs
 * @PackageName: com.handongkeji.shangxuelian.modle
 * @Create 2016/2/1   10:10
 * @Site http://www.handongkeji.com
 * @Copyrights 2016/1/28 handongkeji All rights reserved.
 */
public class Message {

    private int id;
    private String imgHeadUrl;
    private int vipFlag;
    private String name;
    private String post;
    private String company;
    private int likeFlag;
    private int likeNum;
    private int discussNum;
    private String type;
    private String desc;
    private List<String> imgIcons;
    private String imgCourseUrl;//课程头像
    private String courseTitle;//课程标题
    private String expertName;//专家名称
    private String signupNum;
    private String content;
    private long time;
    private int sociatype;
    private String actood;//活动点赞
    private String actcomment;//活动评论
    private String toppicgood;//话题点赞
    private String toppiccomment;//话题评论
    private String coursegood;//课程点赞
    private String coursecomment;//课程评论
    private String coursejion;//课程报名人数
    private String acttitle;//践行活动标题

    public String getActtitle() {
        return acttitle;
    }

    public void setActtitle(String acttitle) {
        this.acttitle = acttitle;
    }

    public String getActpic() {
        return actpic;
    }

    public void setActpic(String actpic) {
        this.actpic = actpic;
    }

    private String actpic;//践行活动封面

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCoursejion() {
        return coursejion;
    }

    public void setCoursejion(String coursejion) {
        this.coursejion = coursejion;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public int getSociatype() {
        return sociatype;
    }

    public void setSociatype(int sociatype) {
        this.sociatype = sociatype;
    }

    public String getActood() {
        return actood;
    }

    public void setActood(String actood) {
        this.actood = actood;
    }

    public String getActcomment() {
        return actcomment;
    }

    public void setActcomment(String actcomment) {
        this.actcomment = actcomment;
    }

    public String getToppicgood() {
        return toppicgood;
    }

    public void setToppicgood(String toppicgood) {
        this.toppicgood = toppicgood;
    }

    public String getToppiccomment() {
        return toppiccomment;
    }

    public void setToppiccomment(String toppiccomment) {
        this.toppiccomment = toppiccomment;
    }

    public String getCoursegood() {
        return coursegood;
    }

    public void setCoursegood(String coursegood) {
        this.coursegood = coursegood;
    }

    public String getCoursecomment() {
        return coursecomment;
    }

    public void setCoursecomment(String coursecomment) {
        this.coursecomment = coursecomment;
    }

    public String getImgHeadUrl() {
        return imgHeadUrl;
    }

    public void setImgHeadUrl(String imgHeadUrl) {
        this.imgHeadUrl = imgHeadUrl;
    }

    public int getVipFlag() {
        return vipFlag;
    }

    public void setVipFlag(int vipFlag) {
        this.vipFlag = vipFlag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public int getLikeFlag() {
        return likeFlag;
    }

    public void setLikeFlag(int likeFlag) {
        this.likeFlag = likeFlag;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getDiscussNum() {
        return discussNum;
    }

    public void setDiscussNum(int discussNum) {
        this.discussNum = discussNum;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImgIcons() {
        return imgIcons;
    }

    public void setImgIcons(List<String> imgIcons) {
        this.imgIcons = imgIcons;
    }

    public String getImgCourseUrl() {
        return imgCourseUrl;
    }

    public void setImgCourseUrl(String imgCourseUrl) {
        this.imgCourseUrl = imgCourseUrl;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getSignupNum() {
        return signupNum;
    }

    public void setSignupNum(String signupNum) {
        this.signupNum = signupNum;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
