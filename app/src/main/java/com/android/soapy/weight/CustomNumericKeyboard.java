package com.android.soapy.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.android.soapy.R;
import com.android.soapy.databinding.CustomNumericKeyboardBinding;


public class CustomNumericKeyboard extends LinearLayout implements View.OnClickListener {

    private TextView[] mButtons;
    private OnKeyboardClickListener mListener;

    StringBuilder sb = new StringBuilder();
    private CustomNumericKeyboardBinding binding;


    public interface OnKeyboardClickListener {
        void onConfirmClicked(String digit);

        void onDigitClicked(String digit);

    }

    public CustomNumericKeyboard(Context context) {
        super(context);
        init(context);
    }

    public CustomNumericKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomNumericKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = CustomNumericKeyboardBinding.inflate(inflater, this, true);
        mButtons = new TextView[12];

        mButtons[0] = binding.btn1;
        mButtons[1] = binding.btn2;
        mButtons[2] = binding.btn3;
        mButtons[3] = binding.btn4;
        mButtons[4] = binding.btn5;
        mButtons[5] = binding.btn6;
        mButtons[6] = binding.btn7;
        mButtons[7] = binding.btn8;
        mButtons[8] = binding.btn9;
        mButtons[9] = binding.btnDelete;
        mButtons[10] = binding.btn0;
        mButtons[11] = binding.btnConfirm;

        for (TextView button : mButtons) {
            button.setOnClickListener(this);
        }
    }

    public void setOnKeyboardClickListener(OnKeyboardClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onClick(View v) {
        if (mListener == null) return;

        if (v.getId() == R.id.btn_delete) {
            if (!TextUtils.isEmpty(sb) && sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
            }
            mListener.onDigitClicked(sb.toString());
        } else if (v.getId() == R.id.btn_confirm) {
            mListener.onConfirmClicked(sb.toString());
        } else {
            TextView clickedButton = (TextView) v;
            String digit = clickedButton.getText().toString();
            if (!(sb.length() == 0 && "0".equals(digit))&&sb.length()<6) {
                sb.append(digit);
            }
            mListener.onDigitClicked(sb.toString());
        }
    }
}
