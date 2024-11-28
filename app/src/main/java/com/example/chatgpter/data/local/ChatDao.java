package com.example.chatgpter.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.chatgpter.data.bean.ChatSession;
import com.example.chatgpter.data.bean.Message;

import java.util.List;

@Dao
public interface ChatDao {
    @Insert
    long insertSession(ChatSession session);

    @Insert
    long insertMessage(Message message);

    @Query("SELECT * FROM chat_session")
    List<ChatSession> getAllSessions();

    @Query("SELECT * FROM message WHERE session_id = :sessionId ORDER BY timestamp")
    List<Message> getMessagesForSession(int sessionId);

    @Query("SELECT * FROM chat_session ORDER BY id DESC LIMIT 1")
    LiveData<ChatSession> getLastSession();

    @Query("SELECT * FROM message WHERE id = :messageId")
    Message getMessageById(long messageId);


    @Query("UPDATE chat_session SET summary = :summary WHERE id = :id")
    void updateSessionTitle(long id, String summary);

    @Query("SELECT * FROM chat_session WHERE summary IS NOT NULL AND summary != 'null' and id != :sessionId ORDER BY created_time DESC LIMIT 10")
    List<ChatSession> getRecentSessions(long sessionId);
    @Query("SELECT * FROM message WHERE session_id = :sessionId ORDER BY timestamp")
    List<Message> getMessagesBySessionId(long sessionId);

    @Query("DELETE FROM chat_session WHERE id = :id")
    void deleteChatSessionById(long id);

    @Query("DELETE FROM message WHERE session_id = :id")
    void deleteMessagesBySessionId(long id);

    @Query("DELETE FROM chat_session WHERE id NOT IN (SELECT DISTINCT session_id FROM message)")
    void deleteSessionNoMessage();
}
