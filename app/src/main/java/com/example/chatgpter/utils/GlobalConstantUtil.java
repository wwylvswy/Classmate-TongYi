package com.example.chatgpter.utils;

public class GlobalConstantUtil {

    /**
     * 全局使用的一些常量
     */


    // 选择的图片大小不能超过5MB
    public static final long MAX_IMAGE_SIZE_MB = 5 * 1024 * 1024;

    // 用户视图
    public static final int VIEW_TYPE_USER = 0;

    // GPT视图
    public static final int VIEW_TYPE_GPT = 1;

    // 文本消息
    public static final int MESSAGE_TYPE_TXT = 0;

    // 图片消息
    public static final int MESSAGE_TYPE_IMG = 1;

}
