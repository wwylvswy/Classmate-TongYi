package com.example.chatgpter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.chatgpter.databinding.ActivityMainBinding;
import com.example.chatgpter.ui.page.LoginActivity;
import com.example.chatgpter.utils.MessageAdapter;
import com.example.chatgpter.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private MainViewModel mainViewModel;

    MessageAdapter adapter;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检查登录状态
        if (!isLogggedIn()) {
            // 未登录，跳转到登录页面
            Intent intentLogin = new Intent(this, LoginActivity.class);
            startActivity(intentLogin);
            finish();
            return;
        }

        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // 从用户主页的历史记录跳转到聊天界面，获取传入的 session_id
        Intent intent = getIntent();
        long sessionId = -1;
        if (intent != null && intent.hasExtra("sessionID")) {
            sessionId = Long.parseLong(intent.getStringExtra("sessionID"));
        } else {
            Log.d("MainActivity", "No session_id passed with the Intent");
        }

        if (savedInstanceState == null && sessionId == -1) {
            // App冷启动， 自动创建新的会话
            mainViewModel.startNewSession();
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setMainvm(mainViewModel);
        binding.setLifecycleOwner(this);

        // 聊天相关
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new MessageAdapter(this, mainViewModel.messages.getValue());
        binding.recyclerView.setAdapter(adapter);

        // 观察数据变化
        mainViewModel.getMessages().observe(this, messages -> {
            adapter.updateMessages(messages);
        });

        // 加载历史消息
        if (sessionId != -1) {
            mainViewModel.setSessionId(sessionId);
            mainViewModel.loadMessagesBySessionId(sessionId);
        }

        Glide.with(this).load(R.drawable.ic_user_avatar).circleCrop().into(binding.circularImageButton);

        // 图片选择器
        // 注册图片选择器
        ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            if (mainViewModel.isImageSizeValid(imageUri)) {

                                Log.d("MainActivity", "Image URI: " + imageUri);

                                mainViewModel.insertImageIntoEditText(imageUri, binding.inputMessage, this);

                                // 更新图片路径到 ViewModel
//                                mainViewModel.updateImagePath(imageUri);
                            } else {
                                Toast.makeText(this, "Image size exceeds 5MB. Please select a smaller image.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
        );

        // 将 Launcher 注册到 ViewModel
        mainViewModel.registerImagePickerLauncher(imagePickerLauncher);

    }

    private boolean isLogggedIn() {
        // 检查登录状态
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        return sp.getBoolean("isLogin", false);
    }



}