package com.example.chatgpter.data.bean;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "message")
public class Message {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "session_id")
    // 对应 ChatSessions 的 ID
    public long sessionId;

    @ColumnInfo(name = "sender")
    // 消息发送者：0-用户 1-GPT
    public int sender;

    @ColumnInfo(name = "message_type")
    // 消息的类型：0-"text" or 1-"image"
    public int messageType;

    @ColumnInfo(name = "content")
    // 文本消息或图片的路径
    public String content;

    @ColumnInfo(name = "timestamp")
    // 消息发送时间
    public long timestamp;

    public Message() {}

    public Message(long sessionId, int sender, int messageType, String content, long timestamp) {
        this.sessionId = sessionId;
        this.sender = sender;
        this.messageType = messageType;
        this.content = content;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public long getSessionId() {
        return sessionId;
    }

    public int getSender() {
        return sender;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getContent() {
        return content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public void setSender(int sender) {
        this.sender = sender;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
