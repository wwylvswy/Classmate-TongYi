package com.example.chatgpter.data.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "chat_session")
public class ChatSession {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "summary")
    public String summary;

    @ColumnInfo(name = "created_time")
    public long createdTime;

    public ChatSession() {}

    public ChatSession(String summary, long createdTime) {
        this.summary = summary;
        this.createdTime = createdTime;
    }

    public long getId() {
        return id;
    }

    public String getSummary() {
        return summary;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void setCreatedTime(long createdTime) {
        this.createdTime = createdTime;
    }
}
