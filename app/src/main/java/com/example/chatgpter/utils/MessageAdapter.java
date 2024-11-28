package com.example.chatgpter.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.chatgpter.R;
import com.example.chatgpter.data.bean.Message;
import io.noties.markwon.Markwon;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Message> messageList;

    public final Context context;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        return message.sender == 0 ? GlobalConstantUtil.VIEW_TYPE_USER : GlobalConstantUtil.VIEW_TYPE_GPT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == GlobalConstantUtil.VIEW_TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_message, parent, false);
            return new UserMessageViewHolder(view, context);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gpt_message, parent, false);
            return new GptMessageViewHolder(view, context);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof GptMessageViewHolder) {
            ((GptMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateMessages(List<Message> newMessages) {
        this.messageList = newMessages;
        notifyDataSetChanged();
    }

    // ViewHolder for user messages
    static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;
        private ImageView imageViewMessage;

        private Context userContext;

        public UserMessageViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.user_txt_msg);
            imageViewMessage = itemView.findViewById(R.id.user_img_msg);
            userContext = context;
        }

        public void bind(Message message) {
            textViewMessage.setText(message.getContent());

            if (message.getMessageType() == GlobalConstantUtil.MESSAGE_TYPE_TXT) {
                textViewMessage.setVisibility(View.VISIBLE);
                imageViewMessage.setVisibility(View.GONE);
                textViewMessage.setText(message.getContent());
            } else if (message.getMessageType() == GlobalConstantUtil.MESSAGE_TYPE_IMG) {
                textViewMessage.setVisibility(View.GONE);
                imageViewMessage.setVisibility(View.VISIBLE);
                Glide.with(userContext)
                        .load(message.getContent()) // 图片 URL
                        .placeholder(R.drawable.chatwithgpt_placeholder) // 占位图
                        .error(R.drawable.chatwithgpt_error_select_img) // 错误图
                        .into(imageViewMessage);
            }

        }


    }

    // ViewHolder for GPT messages
    static class GptMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewMessage;
        private ImageButton btnCopyMessage;
        private ImageView imageViewAvatar;

        private Markwon markwon;

        public GptMessageViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.gpt_message);
            imageViewAvatar = itemView.findViewById(R.id.gpt_avatar);
            btnCopyMessage = itemView.findViewById(R.id.btn_copy);
            markwon = Markwon.create(context);
        }

        public void bind(Message message) {
            markwon.setMarkdown(textViewMessage, message.getContent());
            imageViewAvatar.setImageResource(R.drawable.ic_gpt_avatar);
            btnCopyMessage.setOnClickListener(v -> {
                // 复制文本到剪贴板
                ClipboardManager clipboard =
                        (ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", message.getContent());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(), "复制成功!", Toast.LENGTH_SHORT).show();
            });

        }
    }


}

