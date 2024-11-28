package com.example.chatgpter.ui.page;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatgpter.R;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        // 设置标题栏
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("设置");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // 添加返回按钮
        }
    }
}

