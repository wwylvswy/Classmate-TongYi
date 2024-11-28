package com.example.chatgpter.utils;

public interface OnResponseCallback<T> {
    // 成功回调
    void onSuccess(T response);

    // 错误回调
    void onError(Exception e);
}
