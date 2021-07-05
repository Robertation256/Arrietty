package com.arrietty.utils.session;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * @Author: Yuechuan Zhang
 * @Date: 2021/7/5 17:24
 */
public class SessionIdGenerator {
    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public static String generate(){
        byte[] buffer = new byte[20];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }
}
