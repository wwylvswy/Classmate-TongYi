package com.example.chatgpter.ui.page;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContentProviderCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.chatgpter.MainActivity;
import com.example.chatgpter.R;
import com.example.chatgpter.databinding.ActivityUserProfileBinding;
import com.example.chatgpter.utils.SessionAdapter;
import com.example.chatgpter.viewmodel.ProfileViewModel;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private ProfileViewModel profileViewModel;

    ActivityUserProfileBinding binding;

    private SessionAdapter sessionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);
        binding.setUservm(profileViewModel);
        binding.setLifecycleOwner(this);

        // 头像、用户名加载
        Glide.with(this).load(R.drawable.ic_user_avatar).circleCrop().into(binding.avatarImageView);

        // 会话列表

        // 从用户主页的历史记录跳转到聊天界面，获取传入的 session_id
        Intent intent = getIntent();
        long sessionId = -1;
        if (intent != null && intent.hasExtra("sessionID")) {
            sessionId = Long.parseLong(intent.getStringExtra("sessionID"));
        } else {
            Log.d("ProfileActivity", "No session_id passed with the Intent");
        }



        sessionAdapter = new SessionAdapter(
                new ArrayList<>(),
                session -> {
                    // 单击事件处理，跳转到聊天界面
                    Intent intentActivity = new Intent(this, MainActivity.class);
                    intentActivity.putExtra("sessionID", String.valueOf(session.getId()));
                    intentActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intentActivity);
                },
                (session, position) -> {
                    // 长按事件处理，删除会话
                    new AlertDialog.Builder(this)
                            .setTitle("删除会话")
                            .setMessage("确定要删除这个会话吗？")
                            .setPositiveButton("删除", (dialog, which) -> {
                                profileViewModel.deleteChatSession(session);
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
        );

        binding.chatHistoryRecyclerView.setAdapter(sessionAdapter);
        binding.chatHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        profileViewModel.loadRecentSessions(sessionId);

        // 观察 LiveData 数据
        profileViewModel.getSessions().observe(this, sessions -> sessionAdapter.updateSessions(sessions));

    }
}
