package com.mijia.app.bean;

/**
 * 轮播
 * @ClassName:CarouselBean
 * @PackageName:com.anxinxiaoyuan.app.bean
 * @Create On 2018/8/20 0020.
 * @Site:http://www.handongkeji.com
 * @author:陈志广
 * @Copyrights 2018/1/31 0031 handongkeji All rights reserved.
 */

public class CarouselBean {

    /**
     * pic : /public/uploads/product/2018-08-10/5b6d17727eb5f.jpg
     * link_address : https://www.baidu.com/
     */

    private int pic;
    private String link_address;

    public CarouselBean(int pic, String link_address) {
        this.pic = pic;
        this.link_address = link_address;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public String getLink_address() {
        return link_address;
    }

    public void setLink_address(String link_address) {
        this.link_address = link_address;
    }
}
