package com.seulmae.seulmae.global.util;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
public class UUIDUtil {

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String generateShortUUID() {
        UUID uuid = UUID.randomUUID();
        byte[] bytes = asBytes(uuid);

        // Base64 URL-safe 인코딩하여 문자열 반환
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
//        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, 22); // 22자리를 사용할 경우
    }

    private static byte[] asBytes(UUID uuid) {
        byte[] bytes = new byte[16];  // 16바이트 배열 생성
        long msb = uuid.getMostSignificantBits();  // UUID의 Most Significant Bits 가져오기
        long lsb = uuid.getLeastSignificantBits();  // UUID의 Least Significant Bits 가져오기

        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (msb >>> 8 * (7 - i)); // Most Significant Bits를 바이트 배열의 앞 8바이트에 저장
            bytes[8 + i] = (byte) (lsb >>> 8 * (7 - i)); // Least Significant Bits를 바이트 배열의 뒤 8바이트에 저장
        }

        return bytes;  // 바이트 배열 반환
    }
}
