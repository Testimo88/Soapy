package com.android.soapy.weight;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.soapy.R;

public class CustomLayout extends LinearLayout {

    private EditText editText;
    private Button button;
    private SharedPreferences sharedPreferences;
    private TextView textView;


    public CustomLayout(Context context) {
        super(context);
        init(context, null);
    }

    public CustomLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // 设置布局方向为水平
        setOrientation(HORIZONTAL);

        // 初始化SharedPreferences
        sharedPreferences = context.getSharedPreferences("share_data_soapy", Context.MODE_PRIVATE);
        // 初始化TextView
        textView = new TextView(context);
        textView.setLayoutParams(new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        textView.setTextColor(Color.BLACK);
        textView.setTextSize(18.f);
        addView(textView);

        // 初始化EditText
        editText = new EditText(context);
        LayoutParams params = new LayoutParams(
                0, LayoutParams.MATCH_PARENT, 1);
        editText.setLayoutParams(params);
        editText.setText(sharedPreferences.getString("text", ""));
        editText.setEnabled(false);
        addView(editText);

        // 初始化Button
        button = new Button(context);
        button.setText("编辑");
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.isEnabled()) {
                    // 保存操作
                    button.setText("编辑");
                    editText.setEnabled(false);
                    // 获取键值对的键
                    String key = editText.getTag().toString();
                    // 保存EditText的内容到SharedPreferences
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(key, editText.getText().toString());
                    editor.apply();
                } else {
                    // 编辑操作
                    button.setText("保存");
                    editText.setEnabled(true);
                }
            }
        });
        addView(button);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomLayout);
            String textViewText = typedArray.getString(R.styleable.CustomLayout_textViewText);
            String sharedKey = typedArray.getString(R.styleable.CustomLayout_sharedKey);
            typedArray.recycle();
            textView.setText(textViewText);
            editText.setTag(sharedKey);
            String etValue = sharedPreferences.getString(sharedKey, "");
            editText.setText(etValue);
        }
    }

    // 设置TextView的文字内容
    public void setTextViewText(String text) {
        textView.setText(text);
    }

    // 设置键值对的键
    public void setKey(String key) {
        editText.setTag(key);
    }

    // 设置EditText的初始文本
    public void setEditTextText(String text) {
        editText.setText(text);
    }

    // 获取EditText的文本
    public String getEditTextText() {
        return editText.getText().toString();
    }
}
