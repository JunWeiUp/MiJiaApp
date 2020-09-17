package com.nevermore.oceans.uits;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.widget.TextView;

/**
 * SpannableString,SpannableStringBuilder,ulity class
 *
 * @ClassName:SpanUtil
 * @PackageName:com.bluemobi.wenwanstyle.utils
 * @Create On 2017/8/16 0016   13:23
 * @Site:http://www.handongkeji.com
 * @author:xuchuanting
 * @Copyrights 2017/8/16 0016 handongkeji All rights reserved.
 */

public class SpanUtil {


    public static SpannableStringBuilder getSpannableStringBuilder(String content, Object... o) {
        SpannableStringBuilder builder = new SpannableStringBuilder(content);
        for (int i = 0; i < o.length; i++) {
            builder.setSpan(o[i], 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    public static SpannableString getSpanString(String content, Object o) {
        SpannableString string = new SpannableString(content);
        string.setSpan(o, 0, content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);//INCLUSIVE
        return string;
    }


    public static void append(TextView tv, CharSequence... charSequences) {
        for (int i = 0; i < charSequences.length; i++) {
            tv.append(charSequences[i]);
        }
    }
}
