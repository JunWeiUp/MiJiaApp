package com.mijia.app.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;

import com.mijia.app.R;
import com.mijia.app.databinding.DialogPrivacypoplicyTermofuserBinding;

public class PrivacyPolicyAndTermsOfUseDialog extends Dialog {

    private DialogPrivacypoplicyTermofuserBinding binding;

    private int  index = 0;

    public PrivacyPolicyAndTermsOfUseDialog(@NonNull Context context) {
        super(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_privacypoplicy_termofuser,null);
        setContentView(view);
        binding = DataBindingUtil.bind(view);
        String exchange = context.getResources().getString(R.string.privacy_policy_new);
        binding.tvContent.setText(Html.fromHtml(exchange));

        binding.tvAgree.setOnClickListener(view1 -> {
            if (index==0) {
                String exchange1 = context.getResources().getString(R.string.terms_of_user_new);
                binding.tvContent.setText(Html.fromHtml(exchange1));
                index=1;
            }else{
                SharedPreferences sharedPreferences = context.getSharedPreferences("isFirst",Context.MODE_PRIVATE);
                sharedPreferences.edit().putBoolean("isFirst",false).apply();
                dismiss();
            }
        });
        setCanceledOnTouchOutside(false);
    }
}
