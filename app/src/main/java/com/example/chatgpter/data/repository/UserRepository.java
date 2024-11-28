package com.example.chatgpter.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.chatgpter.data.bean.Message;
import com.example.chatgpter.data.bean.User;
import com.example.chatgpter.data.local.UserDao;
import com.example.chatgpter.data.local.UserRoomDatabase;
import com.example.chatgpter.utils.OnSuccessCallback;

public class UserRepository {
    private UserDao userDao;
    public UserRepository(Application application) {
        UserRoomDatabase db = UserRoomDatabase.getInstance(application);
        userDao = db.userDao();
    }

    // 登录方法
    public void login(String username, OnSuccessCallback<User> callback) {
        UserRoomDatabase.databaseWriteExecutor.execute(() -> {
            User user = userDao.login(username);
            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(user));
        });
    }

    public LiveData<User> getUser(String account) {
        return userDao.getUserByAccount(account);
    }

    public void insertUser(User user) {
        UserRoomDatabase.databaseWriteExecutor.execute(() -> {
            long id = userDao.insertUser(user);
            if (id != -1) {
                Log.d("Database", "User successfully inserted with ID: " + id);
            } else {
                Log.e("Database", "Failed to insert user");
            }
        });
    }
}
