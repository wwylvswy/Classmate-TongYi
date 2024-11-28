package com.example.chatgpter.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.chatgpter.data.bean.ChatSession;
import com.example.chatgpter.data.bean.Message;
import com.example.chatgpter.data.bean.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ChatSession.class, Message.class}, version = 3, exportSchema = false)
public abstract class ChatRoomDatabase extends RoomDatabase {
    public abstract ChatDao chatDao();

    private static volatile ChatRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ChatRoomDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (ChatRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    ChatRoomDatabase.class, "chat_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
