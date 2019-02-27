package com.ylzn.scanlibrary.zxing.scan.view;

import android.app.Notification;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

/**
 * @author Richard_zhou
 * @date 2019/2/26
 * Describe:
 */
public abstract class BaseColorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(getLayoutRsId());

//        ViewGroup viewGroup = findViewById(Window.ID_ANDROID_CONTENT);
//        View view = viewGroup.getChildAt(0);
//        if(view != null && Build.VERSION.SDK_INT > 21){
//            view.setFitsSystemWindows(true);
//        }

        setUI();
        initData();
    }

    abstract protected int getLayoutRsId();
    abstract protected void setUI();
    abstract protected void initData();

}
