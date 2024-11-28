package com.example.chatgpter.data.repository;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.util.Consumer;
import androidx.lifecycle.LiveData;

import com.example.chatgpter.data.bean.ChatSession;
import com.example.chatgpter.data.bean.Message;
import com.example.chatgpter.data.local.ChatDao;
import com.example.chatgpter.data.local.ChatRoomDatabase;
import com.example.chatgpter.utils.OnResponseCallback;
import com.example.chatgpter.utils.OnSuccessCallback;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ChatRepository {

    private ChatDao chatDao;

    public ChatRepository(Application application) {
        ChatRoomDatabase db = ChatRoomDatabase.getInstance(application);
        chatDao = db.chatDao();
    }

    public long insertChatSession(ChatSession chatSession) {
        AtomicLong res = new AtomicLong(-1);
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            res.set(chatDao.insertSession(chatSession));
            if (res.get() != -1) {
                Log.d("Database", "ChatSession successfully inserted with ID: " + res);
            } else {
                Log.e("Database", "Failed to insert chatSession");
            }
        });
        return res.get();
    }

    public void insertMessage(Message message) {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            long res = chatDao.insertMessage(message);
            if (res != -1) {
                Log.d("Database", "Message successfully inserted with ID: " + res);
            } else {
                Log.e("Database", "Failed to insert message");
            }
        });
    }

    public LiveData<ChatSession> getLastSession() {
        return chatDao.getLastSession();
    }
    public void createNewSession(long timestamp, Consumer<Long> callback) {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            ChatSession chatSession = new ChatSession("null", timestamp);
            long sessionId = chatDao.insertSession(chatSession);
            callback.accept(sessionId);
        });
    }

//    public LiveData<Message> getMessageById(long messageId) {
//        return chatDao.getMessageById(messageId);
//    }

    public void getMessageByIdAsync(long messageId, OnSuccessCallback<Message> callback) {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            // 在后台线程中执行查询
            Message message = chatDao.getMessageById(messageId); // Dao 查询同步执行
            // 回到主线程返回结果
            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(message));
        });
    }


    public long insertMessageAsync(Message lastMessage) {
        AtomicLong res = new AtomicLong(-1);
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            // 在后台线程中执行插入
            long messageId = chatDao.insertMessage(lastMessage); // Dao 插入同步执行
            if (messageId != -1) {
                res.set(messageId);
            }
        });
        return res.get();
    }

    public void updateSessionTitle(long sessionId, String title) {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            chatDao.updateSessionTitle(sessionId, title);
        });
    }

    public void getRecentSessions(long sessionId, Consumer<List<ChatSession>> callback) {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<ChatSession> result = chatDao.getRecentSessions(sessionId);
            callback.accept(result);
        });
    }

    public void getMessagesBySessionId(long sessionId, OnSuccessCallback<List<Message>> callback) {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            List<Message> message = chatDao.getMessagesBySessionId(sessionId);
            new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(message));
        });
    }

    public void deleteChatSessionById(long id) {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            chatDao.deleteChatSessionById(id);
        });
    }

    public void deleteMessagesBySessionId(long id) {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            chatDao.deleteMessagesBySessionId(id);
        });
    }

    public void deleteSessionNoMessage() {
        ChatRoomDatabase.databaseWriteExecutor.execute(() -> {
            chatDao.deleteSessionNoMessage();
        });
    }
}
