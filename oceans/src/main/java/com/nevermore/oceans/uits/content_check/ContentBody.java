package com.nevermore.oceans.uits.content_check;

/**
 * Created by Administrator on 2018/2/2 0002.
 */

public class ContentBody implements IContentChecker.Body {

    private String name;
    private String content;

    public ContentBody(String name, String content) {
        this.name = name;
        this.content = content;
    }

    @Override
    public CharSequence getName() {
        return name;
    }

    @Override
    public CharSequence getContent() {
        return content;
    }
}
