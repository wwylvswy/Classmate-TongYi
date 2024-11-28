package com.example.chatgpter.ui.page;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.example.chatgpter.R;
import com.example.chatgpter.databinding.ActivityRegisterBinding;
import com.example.chatgpter.viewmodel.RegisterViewModel;

public class RegisterActivity extends AppCompatActivity {
    RegisterViewModel viewModel;
    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register);

        // 创建ViewModel并绑定
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        binding.setRegistervm(viewModel);
        // 保证LiveData更新时UI同步
        binding.setLifecycleOwner(this);

    }

}
