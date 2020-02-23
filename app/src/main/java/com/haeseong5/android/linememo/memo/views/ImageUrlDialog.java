package com.haeseong5.android.linememo.memo.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.haeseong5.android.linememo.R;

/**
 * Custom Dialog for input image url to user
 */
public class ImageUrlDialog extends Dialog implements View.OnClickListener{
    private Context mContext;
    private EditText mEtInputUrl;
    private CustomDialogListener customDialogListener;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dialog_positive_button:
                String url = mEtInputUrl.getText().toString();
                customDialogListener.onPositiveClicked(url);
                dismiss();
                break;
            case R.id.dialog_negative_button:
                cancel();
                break;
        }
    }
    interface CustomDialogListener{
        void onPositiveClicked(String url);
        void onNegativeClicked();
    }
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }
    public ImageUrlDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_input_url);

        Button mBtNegative = findViewById(R.id.dialog_negative_button);
        Button mBtPositive = findViewById(R.id.dialog_positive_button);
        mEtInputUrl = findViewById(R.id.dialog_et_input_url);

        mBtNegative.setOnClickListener(this);
        mBtPositive.setOnClickListener(this);
    }

    @Override
    public void show() {
        super.show();
    }
    @Override
    public void dismiss() {
        super.dismiss();
        mEtInputUrl.getText().clear();
    }
}
