package com.example.chatgpter.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatgpter.R;
import com.example.chatgpter.data.bean.ChatSession;
import com.example.chatgpter.databinding.ItemSessionBinding;

import java.util.List;

public class SessionAdapter extends ListAdapter<ChatSession, SessionAdapter.SessionViewHolder> {

    private final SessionClickListener clickListener;

    private OnSessionLongClickListener longClickListener;

    private List<ChatSession> sessionList;

    public SessionAdapter(List<ChatSession> sessionList,
                          SessionClickListener clickListener,
                          OnSessionLongClickListener longClickListener) {
        super(new DiffUtil.ItemCallback<ChatSession>() {
            @Override
            public boolean areItemsTheSame(@NonNull ChatSession oldItem, @NonNull ChatSession newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @SuppressLint("DiffUtilEquals")
            @Override
            public boolean areContentsTheSame(@NonNull ChatSession oldItem, @NonNull ChatSession newItem) {
                return oldItem.equals(newItem);
            }
        });
        this.clickListener = clickListener;
        this.sessionList = sessionList;
        this.longClickListener = longClickListener;
    }

    public void setOnSessionLongClickListener(OnSessionLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSessionBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_session,
                parent,
                false
        );
        return new SessionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        ChatSession session = getItem(position);
        holder.bind(session, clickListener);

        // 单击事件监听
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onClick(session);
            }
        });

        // 长按事件监听
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onSessionLongClick(session, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return sessionList == null ? 0 : sessionList.size();
    }

    public void updateSessions(List<ChatSession> newSessions) {
        this.sessionList = newSessions;
        notifyDataSetChanged();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        private final ItemSessionBinding binding;

        public SessionViewHolder(@NonNull ItemSessionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(ChatSession session, SessionClickListener listener) {
            binding.setSession(session);
            binding.setClickListener(listener);
            binding.executePendingBindings();
        }

    }

    public interface OnSessionLongClickListener {
        void onSessionLongClick(ChatSession session, int position);
    }
}
