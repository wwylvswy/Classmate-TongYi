package com.example.chatgpter.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.chatgpter.MainActivity;
import com.example.chatgpter.data.bean.User;
import com.example.chatgpter.data.local.UserDao;
import com.example.chatgpter.data.local.UserRoomDatabase;
import com.example.chatgpter.data.repository.UserRepository;
import com.example.chatgpter.utils.NicknameGenerator;
import com.example.chatgpter.utils.PasswordUtils;
import com.example.chatgpter.utils.ToastUtil;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class RegisterViewModel extends AndroidViewModel {
    public MutableLiveData<String> account = new MutableLiveData<>("");
    public MutableLiveData<String> password = new MutableLiveData<>("");
    public MutableLiveData<String> nickname = new MutableLiveData<>("");

    private Context context;

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
    }

    public void onRegisterClicked(View view) {
        String account = this.account.getValue();
        String password = this.password.getValue();
        String nickname = this.nickname.getValue();
        if (validateInput(account, password, nickname)) {
            try {
                saveUser();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            } catch (InvalidKeySpecException e) {
                throw new RuntimeException(e);
            }
            navigateToMain();
        }
    }

    private boolean validateInput(String act, String pwd, String name) {

        if (act == null || act.isEmpty() || pwd == null || pwd.isEmpty()) {
            showToast(ToastUtil.ACCOUNT_OR_PASSWORD_EMPTY);
            return false;
        }

        if (!isValidPassword(pwd)) {
            showToast(ToastUtil.PASSWORD_FORMAT_ERROR);
            return false;
        }

        // 昵称可以为空，如果为空则随机生成一个
        if (name == null || name.isEmpty()) {
            String randomName = NicknameGenerator.generateRandomNickname();
            nickname.setValue(randomName);
        }
        return true;
    }

    private boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$");
    }

    /**
     * 保存用户到数据库
     */
    private void saveUser() throws NoSuchAlgorithmException, InvalidKeySpecException {
        // 设置 lastLoginTime 为当前时间
        long currentTimeMillis = System.currentTimeMillis();
        String pwd = password.getValue();
        // 生成盐值
        String salt = PasswordUtils.generateSalt();
        // 哈希密码
        String hashedPassword = PasswordUtils.hashPassword(pwd, salt);
        // 创建用户对象
        User newUser = new User(account.getValue(),hashedPassword , nickname.getValue(), currentTimeMillis);
        newUser.setSalt(salt);

        // 保存用户到数据库
        UserRepository userRepository = new UserRepository(getApplication());
        userRepository.insertUser(newUser);

        showToast(ToastUtil.REGISTER_SUCCESS);
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void navigateToMain() {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}

