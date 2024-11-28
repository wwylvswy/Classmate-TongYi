package com.example.chatgpter.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.chatgpter.data.bean.User;

@Dao
public interface UserDao {
    @Insert
    long insertUser(User user);

    @Query("SELECT * FROM user_table WHERE account = :account")
    LiveData<User> getUserByAccount(String account);

    @Update
    void updateUser(User user);

    // 查询匹配的用户
    @Query("SELECT * FROM user_table WHERE account = :account LIMIT 1")
    User login(String account);
}

