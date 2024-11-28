package com.example.chatgpter.viewmodel;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.alibaba.dashscope.exception.ApiException;
import com.example.chatgpter.data.bean.ChatSession;
import com.example.chatgpter.data.bean.Message;
import com.example.chatgpter.data.repository.ChatRepository;
import com.example.chatgpter.ui.page.ProfileActivity;
import com.example.chatgpter.utils.ApiClient;
import com.example.chatgpter.utils.GlobalConstantUtil;
import com.example.chatgpter.utils.OnResponseCallback;
import com.example.chatgpter.utils.ToastUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.content.ClipData;
import android.content.ClipboardManager;

public class MainViewModel extends AndroidViewModel {

    private Context context;

    private ActivityResultLauncher<Intent> selectImageLauncher;

    // 只读暴露给 UI 层
    private final MutableLiveData<String> imagePathPrivate = new MutableLiveData<>();


    // 用于存储用户输入的聊天内容
    public final MutableLiveData<String> chatContent = new MutableLiveData<>();

    private List<Message> chatMessages = new ArrayList<>();

    // 用于存储聊天消息
    public MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());

    /**
     * sessionid的管理方式，每次退出应用在打开应用，都会开启一个新的对话，
     * 这意味着每次打开应用都会生成一个新的sessionid
     * 通过检测 onCreate 的调用情况来判断应用是否冷启动（即完全退出后重新打开）。
     */
    private final MutableLiveData<Long> sessionID = new MutableLiveData<>();

    private final Executor executor = Executors.newSingleThreadExecutor();


    private ChatRepository chatRepository;

    // 用于存储选中的图片
    private Uri selectedImageUri = null;


    // 用于addGPTMessage方法中判断是否是第一次回复
    private int replyCount = 0;

    // 用于辅助将会话的title存储到本地数据库
    private boolean isFirstMessage = true;


    public MainViewModel (@NonNull Application application) {
        super(application);
        context = application.getApplicationContext();

        chatRepository = new ChatRepository(application);

        chatRepository.getLastSession().observeForever(session -> {
            if (session != null) {
                sessionID.setValue(session.getId());
            } else {
                startNewSession();
            }
        });
    }

    /**
     * 获取当前会话的ID
     *
     * @return 当前会话的ID
     */
    public LiveData<Long> getCurrentSessionId() {
        return sessionID;
    }


    /**
     * 开启一个新的会话
     */
    public void startNewSession() {
        long timestamp = System.currentTimeMillis();
        chatRepository.createNewSession(timestamp, sessionId -> {
            sessionID.postValue(sessionId);
            isFirstMessage = true;
        });
    }


    /**
     * 添加用户的消息到聊天界面
     *
     * @param message  用户的消息
     */
    public void addUserMessage(Message message) {
        List<Message> currentMessages = messages.getValue();
        currentMessages.add(message);
        messages.setValue(currentMessages);
    }


    /**
     * 添加GPT的回复到聊天界面
     *
     * @param message  GPT的回复消息
     */
    public void addGPTMessage(Message message) {
        List<Message> currentMessages = messages.getValue();
        replyCount += 1;
        if (replyCount == 1) {
            currentMessages.add(message);
            messages.setValue(currentMessages);
        } else {
            Message lastMessage = currentMessages.get(currentMessages.size() - 1);
            lastMessage.setContent(lastMessage.getContent() + message.getContent());
            currentMessages.set(currentMessages.size() - 1, lastMessage);
            messages.setValue(currentMessages);
        }
    }


    /**
     * 通过Uri获取文件路径
     *
     * @param context  上下文
     * @param uri  文件的Uri
     * @return 文件的路径
     */
    private String getFilePathFromUri(Context context, Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    filePath = cursor.getString(columnIndex);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        return filePath;
    }

    /**
     * 点击发送按钮
     */
    public void onSendClicked() {

        String userMessage = chatContent.getValue();
        if (userMessage != null) {
           userMessage = userMessage.replaceAll("[\\r\\n]+$", "");
           userMessage = userMessage
                   // 去掉换行符
                   .replace("\n", "")
                   // 去掉首尾空格
                   .trim();
        }

        if (isFirstMessage) {
            isFirstMessage = false;
            // 保存会话的title到数据库，把用户发送的第一条消息作为title
            chatRepository.updateSessionTitle(sessionID.getValue(), userMessage);
        }

        // 将图片的URL转变成filePath
        String filePath = null;

        Message userMsgImg = null;
        if (selectedImageUri != null) {
            filePath = getFilePathFromUri(context, selectedImageUri);
            String imagePath = selectedImageUri.toString();
            // 在界面上显示用户发送的图片
            userMsgImg = new Message(sessionID.getValue(), 0, 1, imagePath, System.currentTimeMillis());
            // 重置选中的图片
            selectedImageUri = null;
        }

        if (userMessage != null && !userMessage.isEmpty()) {
            // 在界面上显示用户发送的消息
            Message userMsg = new Message(sessionID.getValue(), 0, 0, userMessage, System.currentTimeMillis());
            addUserMessage(userMsg);
            if (userMsgImg != null) {
                addUserMessage(userMsgImg);
            }

            // 用户消息保存到数据库
            chatRepository.insertMessageAsync(userMsg);

            // 图片消息保存到数据库
            if (userMsgImg != null) {
                chatRepository.insertMessageAsync(userMsgImg);
            }

            Map<String, String> userMessageMap = new HashMap<>();
            userMessageMap.put("userTextMessage", userMessage);
            userMessageMap.put("userImgMessage", filePath);

            try {
                ApiClient.simpleMultiModalConversationCall(userMessageMap, new OnResponseCallback() {
                    @Override
                    public void onSuccess(Object response) {

                        Map<String, String> getResponse = (Map<String, String>) response;

                        Message gptMsg = new Message(sessionID.getValue(), 1, 0, getResponse.get("content"), System.currentTimeMillis());

                        if (("stop").equals(getResponse.get("finishReason"))) {
                            // GPT的回复保存到数据库
                            chatRepository.insertMessageAsync(gptMsg);
                            // 更新startReply状态
                            replyCount = 0;
                        } else {
                            // 更新数据到聊天界面
                            addGPTMessage(gptMsg);
                        }

                    }

                    @Override
                    public void onError(Exception e) {
                        // 处理错误，例如显示错误提示
                        Log.e("Error", "Network error occurred", e);
                        Toast.makeText(context, ToastUtil.NETWORK_CONNECTION_FAILURE, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (ApiException e) {
                System.out.println(e.getMessage());
            }

            chatContent.setValue("");
        } else {
            Toast.makeText(context, ToastUtil.SEND_INPUT_EMPTY, Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 注册图片选择器
     *
     * @param launcher  图片选择器的 ActivityResultLauncher。
     */
    public void registerImagePickerLauncher(ActivityResultLauncher<Intent> launcher) {
        this.selectImageLauncher = launcher;
    }


    /**
     * 判断图片的大小是否超过限制
     *
     * @param imageUri  要判断的图片的 URI。
     */
    public boolean isImageSizeValid(Uri imageUri) {
        try {
            InputStream inputStream = getApplication().getContentResolver().openInputStream(imageUri);
            if (inputStream != null) {
                int size = inputStream.available();
                inputStream.close();
                return size <= GlobalConstantUtil.MAX_IMAGE_SIZE_MB;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 在指定的 EditText 中的光标位置插入图片。
     * 图片默认插入到光标的下一行。
     * 插入图片后，光标会自动移动到文本末尾。
     *
     * @param imageUri  要插入的图片的 URI。
     * @param editText  目标 EditText 控件。
     * @param context   当前上下文，用于访问内容解析器和资源。
     */
    public void insertImageIntoEditText(Uri imageUri, EditText editText, Context context) {
        selectedImageUri = imageUri;
        try {
            // 通过 ContentResolver 获取图片
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false);

            // 创建 ImageSpan
            ImageSpan imageSpan = new ImageSpan(context, scaledBitmap);

            // 获取 EditText 中现有内容
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(editText.getText());

            // 在光标处插入图片
            int cursorPosition = editText.getSelectionStart();

            // 确保光标位置合法
            if (cursorPosition < 0 || cursorPosition > spannableStringBuilder.length()) {
                cursorPosition = spannableStringBuilder.length();
            }

            // 在光标处插入换行符
            spannableStringBuilder.insert(cursorPosition, "\n");

            // 重新计算光标位置并插入图片， 换行符占用一个位置
            cursorPosition += 1;
            // 占位符用于插入图片
            spannableStringBuilder.insert(cursorPosition, " ");
            spannableStringBuilder.setSpan(
                    imageSpan,
                    // 图片开始位置
                    cursorPosition,
                    // 图片结束位置
                    cursorPosition + 1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            );

            // 更新 EditText 的内容
            editText.setText(spannableStringBuilder);

            // 将光标移动到文本后
            editText.setSelection(cursorPosition);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击添加文件按钮
     */
    public void onSelectImageClicked() {
        // 打开文件选择器
        if (selectImageLauncher != null) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            selectImageLauncher.launch(intent);
        }
    }

    /**
     * 点击新建对话按钮
     */
    public void onNewChatClicked() {

        // 清空没有任何聊天记录的会话
        chatRepository.deleteSessionNoMessage();

        // 检查并保存最后一条消息
        if (!chatMessages.isEmpty()) {
            Message lastMessage = chatMessages.get(chatMessages.size() - 1);
            chatRepository.getMessageByIdAsync(lastMessage.getId(), message -> {
                if (message == null) {
                    // 如果最后一条消息未保存，则插入数据库
                    long res = chatRepository.insertMessageAsync(lastMessage);
                    if (res != -1) {
                        Log.d("Database", "Message successfully inserted with ID: " + res);
                    } else {
                        Log.e("Database", "Failed to insert message");
                    }
                }
            });
        }

        // 向数据库插入一条新的ChatSession记录
        ChatSession newChatSession = new ChatSession("null", System.currentTimeMillis());
        long newSessionId = chatRepository.insertChatSession(newChatSession);
        sessionID.setValue(newSessionId);

        isFirstMessage = true;

        // 清空界面上的对话记录
        chatMessages.clear();

        //通知观察者更新
        messages.setValue(new ArrayList<>());
    }

    /**
     *  打开个人主页
     */
    public void onProfileClicked() {
        // 跳转到个人主页
        Intent intent = new Intent(context, ProfileActivity.class);
        intent.putExtra("sessionID", String.valueOf(sessionID.getValue()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 一键复制GPT回复
     */
    public void onCopyGptReplyClicked() {
        String messageToCopy = "copy this message";
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Message", messageToCopy);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, ToastUtil.COPY_SUCCESS, Toast.LENGTH_SHORT).show();
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public void loadMessagesBySessionId(long sessionId) {
        chatRepository.getMessagesBySessionId(sessionId, messagesBySessionId -> {
            Log.d("MainViewModel", "Loaded messages by session ID: " + sessionId);
            messages.setValue(messagesBySessionId);
        });
    }

    public void setSessionId(long newSessionId) {
        sessionID.setValue(newSessionId);
        isFirstMessage = false;
    }
}
