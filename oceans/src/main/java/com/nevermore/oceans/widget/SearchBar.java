package com.nevermore.oceans.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nevermore.oceans.R;

/**
 * Created by Administrator on 2017/10/14 0014.
 */

public class SearchBar extends FrameLayout {
    public EditText edtSearch;
    public TextView tvRight;
    public ImageView ivRight;
    public RelativeLayout rightView;
    public ImageView leftImageView;


    public SearchBar(Context context) {
        this(context, null);
    }

    public SearchBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.search_bar, this);

//        leftImageView.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (getContext() instanceof Activity) {
//                    ((Activity) getContext()).finish();
//                }
//            }
//        });

        leftImageView = (ImageView) findViewById(R.id.iv_finish);
        rightView=(RelativeLayout) findViewById(R.id.right_view);
        tvRight = (TextView) findViewById(R.id.tv_right);
        edtSearch = (EditText) findViewById(R.id.edt_search);
        ivRight = (ImageView) findViewById(R.id.iv_right);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SearchBar);

        Drawable leftdrawable=typedArray.getDrawable(R.styleable.SearchBar_ivLeftIcon);
        if (leftdrawable!=null) {
            leftImageView.setImageDrawable(leftdrawable);
        }


        String rightText = typedArray.getString(R.styleable.SearchBar_textRight);
        if (!TextUtils.isEmpty(rightText)) {
            tvRight.setText(rightText);
        }

        Drawable rightdrawable=typedArray.getDrawable(R.styleable.SearchBar_ivRightIcon);
        if (rightdrawable!=null) {
            ivRight.setImageDrawable(rightdrawable);
        }

        boolean righttexthint = typedArray.getBoolean(R.styleable.SearchBar_textRightHint, false);
        tvRight.setVisibility(righttexthint ? GONE : VISIBLE);

        boolean rightimageviewhint=typedArray.getBoolean(R.styleable.SearchBar_ivRightHint,true);
        ivRight.setVisibility(rightimageviewhint ? GONE : VISIBLE);

        String hint = typedArray.getString(R.styleable.SearchBar_searchHint);
        if (!TextUtils.isEmpty(hint)) {
            edtSearch.setHint(hint);
        }
        typedArray.recycle();
    }

    public interface OnSearchListener {
        void onSearchContent(String content);
    }

    private void hideSoftInputFromWindow() {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(activity.getWindow().peekDecorView().getWindowToken(), 0);
        }

    }


    public void setSearchListener(final OnSearchListener listener) {

        if (listener != null) {
            edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                        String content = edtSearch.getText().toString().trim();

                        if (!TextUtils.isEmpty(content)) {
                            listener.onSearchContent(content);
                            hideSoftInputFromWindow();
                        } else {
                            Toast.makeText(getContext(), "请输入搜索内容", Toast.LENGTH_SHORT).show();
                        }
                    }

                    return false;
                }
            });


        }

    }


    public void setClickRightSearch(final OnSearchListener listener) {
        tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = edtSearch.getText().toString().trim();

                if (!TextUtils.isEmpty(content)) {
                    listener.onSearchContent(content);
                    hideSoftInputFromWindow();
                } else {
                    Toast.makeText(getContext(), "请输入搜索内容", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void setCancel() {
        tvRight.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = SearchBar.this.getContext();
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }
        });
    }


    public EditText getEdtSearch() {
        return edtSearch;
    }

    public TextView getTvRight() {
        return tvRight;
    }

    public ImageView getIvRight() {
        return ivRight;
    }

    public RelativeLayout getRightView() {
        return rightView;
    }

    public ImageView getLeftImageView() {
        return leftImageView;
    }
}
