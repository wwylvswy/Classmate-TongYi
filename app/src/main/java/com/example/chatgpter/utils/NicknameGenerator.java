package com.example.chatgpter.utils;

import java.util.Random;

public class NicknameGenerator {

    // 随机前缀数组
    private static final String[] PREFIXES = {
            "Cool", "Super", "Mega", "Ultra", "Mighty", "Crazy", "Lucky", "Smart", "Brave", "Shadow"
    };

    // 随机后缀数组
    private static final String[] SUFFIXES = {
            "Hero", "Warrior", "Ninja", "Pirate", "Gamer", "Fox", "Eagle", "Lion", "Dragon", "Phoenix"
    };

    // 随机数字范围
    private static final int MAX_NUMBER = 999;

    public static String generateRandomNickname() {
        Random random = new Random();

        // 从前缀和后缀中随机选择
        String prefix = PREFIXES[random.nextInt(PREFIXES.length)];
        String suffix = SUFFIXES[random.nextInt(SUFFIXES.length)];

        // 随机生成一个0-999之间的数字
        int randomNumber = random.nextInt(MAX_NUMBER + 1);

        // 拼接昵称
        return prefix + suffix + randomNumber;
    }
}

