package com.example.chatgpter.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatgpter.MainActivity;
import com.example.chatgpter.data.bean.User;
import com.example.chatgpter.data.repository.UserRepository;
import com.example.chatgpter.ui.page.RegisterActivity;
import com.example.chatgpter.utils.PasswordUtils;
import com.example.chatgpter.utils.ToastUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginViewModel extends AndroidViewModel {
    private final UserRepository userRepository;

    public MutableLiveData<String> account = new MutableLiveData<>();
    public MutableLiveData<String> password = new MutableLiveData<>();

    // 私有的 MutableLiveData 只在 ViewModel 内部修改
    private final MutableLiveData<Boolean> loginResultPrivate = new MutableLiveData<>();

    // 暴露只读 LiveData 给外部观察
    public LiveData<Boolean> loginResultPublic = loginResultPrivate;

    private Context context;


    public LoginViewModel (@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        context = application.getApplicationContext();
    }

    /**
     * 登录
     * @param view
     */
    public void login(View view) {
        String accountValue = this.account.getValue();
        String passwordValue = this.password.getValue();

        userRepository.login(accountValue, user -> {
            if (user == null) {
                // 用户不存在
                Toast.makeText(context, ToastUtil.INVALID_ACCOUNT, Toast.LENGTH_SHORT).show();
            } else {
                String storedPassword = user.getPassword();
                String storedSalt = user.getSalt();
                try {
                    String hashedInputPassword = PasswordUtils.hashPassword(passwordValue, storedSalt);
                    if (storedPassword.equals(hashedInputPassword)) {
                        // 验证成功
                        loginResultPrivate.postValue(user != null);
                        Toast.makeText(context, ToastUtil.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
                    } else {
                        // 验证失败
                        Toast.makeText(context, ToastUtil.ERROR_PASSWORD, Toast.LENGTH_SHORT).show();
                    }
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * 注册
     * @param context
     */
    public void onRegisterClicked(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

}
