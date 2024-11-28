package com.example.chatgpter.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.example.chatgpter.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;

public class ApiClient {

    // 缓存的流式数据
    private static final List<String> cache = new ArrayList<>();

    private static final Handler uiHandler = new Handler(Looper.getMainLooper());


    /**
     * 发送用户消息到 通义千问 模型，并通过回调返回响应结果。
     *
     * @param userMessageMap 用户输入的文本和图片消息，作为聊天内容发送到 GPT 模型。
     * @param callback 回调接口，用于在异步请求完成后返回结果或处理错误。
     * 处理逻辑：
     * 1. 创建一个新的线程以避免在主线程上执行网络请求。
     * 2. 构建 MultiModalMessage 和 MultiModalConversationParam，用于配置用户消息、模型信息和 API 密钥。
     * 3. 调用 通义千问 的聊天 API（通过 conv.call(param)），获取模型的响应。
     * 4. 将成功的响应或异常切换到主线程，通过 OnResponseCallback 回调返回给调用方。
     * 注意：
     * - 确保在 `BuildConfig.API_KEY` 中正确配置了 API 密钥。
     */
    public static void simpleMultiModalConversationCall(Map<String, String> userMessageMap, OnResponseCallback callback) {
        // 启动一个新线程
        new Thread(() -> {
            try {
                // 初始化对话参数
                MultiModalConversation conv = new MultiModalConversation();
                List<Map<String, Object>> listContent = null;
                if (userMessageMap.get("userImgMessage") != null) {

                    listContent = Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("image", "file://" + userMessageMap.get("userImgMessage"));
                            }},
                            new HashMap<String, Object>() {{
                                put("text", userMessageMap.get("userTextMessage"));
                            }});
                } else {
                    listContent = Arrays.asList(
                            new HashMap<String, Object>() {{
                                put("text", userMessageMap.get("userTextMessage"));
                            }});
                }

                MultiModalMessage userMessage = MultiModalMessage.builder()
                        .role(Role.USER.getValue())
                        .content(listContent)
                        .build();

                // 获取 API 密钥
                String apiKey = BuildConfig.API_KEY;

                // 构造请求参数
                MultiModalConversationParam param = MultiModalConversationParam.builder()
                        .model("qwen-vl-max-latest")
                        .message(userMessage)
                        .incrementalOutput(true)
                        .apiKey(apiKey)
                        .build();

                Flowable<MultiModalConversationResult> result = conv.streamCall(param);

                result.blockingForEach(item -> {
                    try {
                        Map<String, String> response = new HashMap<>();
                        String responseContent = (String) item.getOutput()
                                .getChoices()
                                .get(0)
                                .getMessage()
                                .getContent()
                                .get(0)
                                .get("text");
                        String responseFinishReason = (String) item.getOutput()
                                .getChoices()
                                .get(0)
                                .getFinishReason();
                        response.put("content", responseContent);
                        response.put("finishReason", responseFinishReason);

                        if ("stop".equals(responseFinishReason)) {
                            StringBuilder builder = new StringBuilder();
                            for (String str : cache) {
                                builder.append(str); // 不添加分隔符
                            }
                            response.put("content", builder.toString());
                            cache.clear();
                        } else {
                            cache.add(responseContent);
                            response.put("content", responseContent);
                        }
                        uiHandler.post(() -> {
                            callback.onSuccess(response);
                        });

                    } catch (Exception e){
                        System.exit(0);
                    }
                });
            } catch (ApiException | NoApiKeyException | UploadFileException e) {
                // 异常处理：返回错误信息到主线程
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError(e);
                });
            }
        }).start();
    }
}

