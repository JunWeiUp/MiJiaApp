package com.nevermore.oceans.uits.content_check;

import java.util.List;

/**
 *
 * @author XuNeverMore
 * @QQ 1045530120
 * @create on 2018/2/2 0002
 * @github https://github.com/XuNeverMore
 */

public interface IContentChecker {


    /**
     * 检查条件
     */
    public interface Condition{
        /**
         * 是否符合条件
         * @param body
         * @return
         */
        boolean match(Body body);

        void showTips(Body body);

    }


    /**
     * 被检查的内容
     */
    public interface Body{

        /**
         * 被检查内容的名字
         * @return
         */
        CharSequence getName();

        /**
         * 被检查的具体内容
         * @return
         */
        CharSequence getContent();

    }

    boolean check(Body body, List<Condition> conditions);

}
