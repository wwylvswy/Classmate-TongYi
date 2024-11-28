package com.example.chatgpter.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.chatgpter.data.bean.ChatSession;
import com.example.chatgpter.data.bean.Message;
import com.example.chatgpter.data.repository.ChatRepository;
import com.example.chatgpter.ui.page.SettingActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends AndroidViewModel {

    private Context context;

    private ChatRepository chatRepository;

    private final MutableLiveData<List<ChatSession>> sessions = new MutableLiveData<>();

    public ProfileViewModel (@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();
        chatRepository = new ChatRepository(application);
//        loadRecentSessions(sessionId);
    }

    public LiveData<List<ChatSession>> getSessions() {
        return sessions;
    }

    /**
     * 加载最近的会话
     * @param sessionId
     */
    public void loadRecentSessions(long sessionId) {
        chatRepository.getRecentSessions(sessionId, sessions::postValue);
    }

    /**
     * 加载消息
     * @param view
     */
    public void onSettingClicked(View view) {
        Intent intent = new Intent(context, SettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 删除会话
     * @param session
     */
    public void deleteChatSession(ChatSession session) {
        chatRepository.deleteMessagesBySessionId(session.getId());
        chatRepository.deleteChatSessionById(session.getId());
        List<ChatSession> updatedSessions = new ArrayList<>(sessions.getValue());
        updatedSessions.remove(session);
        // 更新 LiveData
        sessions.setValue(updatedSessions);
    }

}
