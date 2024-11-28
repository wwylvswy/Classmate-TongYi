package com.example.chatgpter.ui.page;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.chatgpter.MainActivity;
import com.example.chatgpter.R;
import com.example.chatgpter.databinding.ActivityLoginBinding;
import com.example.chatgpter.viewmodel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel loginViewModel;
    @NonNull ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        binding.setLoginvm(loginViewModel);
        binding.setLifecycleOwner(this);

        binding.btnRegister.setOnClickListener(v -> loginViewModel.onRegisterClicked(this));

        loginViewModel.loginResultPublic.observe(this, success -> {
            if (success) {
                saveLoginState();  // 保存登录状态
                navigateToMain();  // 跳转到主页面
            } else {
                Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveLoginState() {
        // 保存登录状态
        SharedPreferences sp = getSharedPreferences("login", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", true);
        editor.apply();
    }

    private void navigateToMain() {
        // 跳转到主页面
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
