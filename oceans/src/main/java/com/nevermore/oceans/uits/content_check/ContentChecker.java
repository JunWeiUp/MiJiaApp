package com.nevermore.oceans.uits.content_check;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 内容检查工具类
 *
 * @author XuNeverMore
 * @QQ 1045530120
 * @create on 2018/2/2 0002
 * @github https://github.com/XuNeverMore
 *
 * 多项内容检查{@link Machine}
 *
 */

public class ContentChecker implements IContentChecker {

    private ContentChecker() {

    }

    private Body body;
    private List<Condition> conditionList;

    private ContentChecker(Body body) {
        this.body = body;
    }

    public static ContentChecker getChecker(Body body){

        return new ContentChecker(body);
    }

    /**
     * 添加条件
     * @param condition
     * @return
     */
    public ContentChecker addCondition(Condition condition){
        if(conditionList == null){
            conditionList = new ArrayList<>();
        }
        conditionList.add(condition);

        return this;
    }

    /**
     * 检查条件并返回检查结果
     * @return
     */
    public boolean getCheckResult(){

        if(this.body == null||this.conditionList==null){
            throw new NullPointerException("检查内容和条件不能为空");
        }

        return check(this.body,this.conditionList);
    }

    @Override
    public boolean check(@NonNull Body body, @NonNull List<Condition> conditions) {

        for (Condition c : conditions) {
            if (!c.match(body)) {
                c.showTips(body);
                return false;
            }
        }
        return true;
    }


    public static Machine getCheckMachine(){
        return new Machine();
    }

    public static class Machine{

        private List<ContentChecker> checkerList;

        private Machine() {
            checkerList = new ArrayList<>();
        }

        public Machine putChecker(ContentChecker checker){
            checkerList.add(checker);
            return this;
        }

        /**
         * 检查所有内容
         * @return 结果
         */
        public boolean checkAll(){

            for(ContentChecker c:checkerList){
                if(!c.getCheckResult()){
                    return false;
                }
            }
            return true;
        }
    }

}
