package com.example.chatgpter.data.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_table")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "account")
    private String account;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "nickname")
    private String nickname;

    @ColumnInfo(name = "salt")
    private String salt;

    @ColumnInfo(name = "lastLoginTime")
    private long lastLoginTime;

    public User() {}

    public User(String account, String password, String nickname, long currentTimeMillis) {
        this.account = account;
        this.password = password;
        this.nickname = nickname;
        this.lastLoginTime = currentTimeMillis;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getNickname() {
        return nickname;
    }

    public String getSalt() {
        return salt;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

}

