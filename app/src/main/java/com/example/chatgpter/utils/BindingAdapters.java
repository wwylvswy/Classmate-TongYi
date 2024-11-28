package com.example.chatgpter.utils;

import androidx.databinding.BindingAdapter;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatgpter.data.bean.ChatSession;
import com.example.chatgpter.data.bean.Message;

import java.util.List;

public class BindingAdapters {

    @BindingAdapter("messages")
    public static void bindMessages(RecyclerView recyclerView, List<Message> messages) {
        MessageAdapter adapter = (MessageAdapter) recyclerView.getAdapter();
        // 自定义方法更新数据
        adapter.updateMessages(messages);
    }

    @BindingAdapter("sessions")
    public static void bindSessions(RecyclerView recyclerView, LiveData<List<ChatSession>> liveSessions) {
        if (recyclerView.getAdapter() instanceof SessionAdapter && liveSessions != null) {
            liveSessions.observe((LifecycleOwner) recyclerView.getContext(), sessions -> {
                SessionAdapter adapter = (SessionAdapter) recyclerView.getAdapter();
                // 更新 RecyclerView 的数据
                adapter.submitList(sessions);
            });
        }
    }

}

