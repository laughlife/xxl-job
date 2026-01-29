package com.xxl.job.executor.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONException;

/**
 * Python -> Java (fastjson2)
 */
@SuppressWarnings("unchecked")
public class Md5Utils {

    // 等价 Python: _SIGN_SALT
    private static final String SIGN_SALT = "I1j0l9n4DF3Aac|spP03DGqd4msbb";

    // JS 的 Base64 允许字符（用于 decode 前过滤）
    private static final String BASE64_ALLOWED =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";

    // =========================
    // 1) Base64 编码/解码（等价 JS 的 i/o/a/s）
    // =========================

    /** 等价 Python: js_base64_encode */
    public static String jsBase64Encode(String text) {
        if (text == null) return "";
        byte[] raw = text.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(raw);
    }

    /** 等价 Python: js_base64_decode（含"过滤非 Base64 字符"行为） */
    public static String jsBase64Decode(String b64Text) {
        if (b64Text == null || b64Text.isEmpty()) return "";

        // 贴近 JS：过滤非 Base64 字符
        StringBuilder cleaned = new StringBuilder(b64Text.length());
        for (int i = 0; i < b64Text.length(); i++) {
            char ch = b64Text.charAt(i);
            if (BASE64_ALLOWED.indexOf(ch) >= 0) {
                cleaned.append(ch);
            }
        }

        byte[] decoded = Base64.getDecoder().decode(cleaned.toString());
        return new String(decoded, StandardCharsets.UTF_8);
    }


    // =========================
    // 2) 混淆字符编码/解码（等价 JS 的 encodeMixChar / decodeMixChar）
    // =========================

    /** 等价 Python: encode_mix_char */
    public static String encodeMixChar(String plainText) {
        if (plainText == null || plainText.isEmpty()) return null;

        String b64 = jsBase64Encode(plainText);
        // a -> '-', c -> '#', x -> '^', M -> '$'
        return b64.replace("a", "-")
                .replace("c", "#")
                .replace("x", "^")
                .replace("M", "$");
    }

    /** 等价 Python: decode_mix_char */
    public static String decodeMixChar(String mixedText) {
        if (mixedText == null || mixedText.isEmpty()) return null;

        // '-' -> 'a', '#' -> 'c', '^' -> 'x', '$' -> 'M'
        String restoredB64 = mixedText.replace("-", "a")
                .replace("#", "c")
                .replace("^", "x")
                .replace("$", "M");
        return jsBase64Decode(restoredB64);
    }

    // =========================
    // 3) Token 生成/解密（等价 JS 的 getToken + decodeMixChar）
    // =========================

    /** 等价 Python: get_token */
    public static String getToken(Map<String, Object> payload) {
        if (payload == null) return "";

        Map<String, Object> obj = new LinkedHashMap<>();
        obj.put("timestamp", payload.get("timestamp"));
        obj.put("nonce", payload.get("nonce"));
        obj.put("token", payload.get("token"));

        String jsonText = JSON.toJSONString(obj); // fastjson2 紧凑 JSON
        String mixed = encodeMixChar(jsonText);
        return mixed == null ? "" : mixed;
    }

    /** 等价 Python: decrypt_token_to_obj */
    public static Map<String, Object> decryptTokenToObj(String mixedToken) {
        String plain = decodeMixChar(mixedToken);
        if (plain == null || plain.isEmpty()) {
            return new LinkedHashMap<>();
        }
        try {
            // fastjson2 parse 成 Map
            return (Map<String, Object>) JSON.parseObject(plain, Map.class);
        } catch (JSONException e) {
            // 如果不是 JSON，就返回原文包装一下，便于排查
            Map<String, Object> fallback = new LinkedHashMap<>();
            fallback.put("_raw", plain);
            return fallback;
        }
    }


    // =========================
    // 4) md5 + sign（等价 JS 的 md5 / getSign）
    // =========================

    /** 等价 Python: md5_hex（小写 hex） */
    public static String md5Hex(String text) {
        if (text == null || text.isEmpty()) return null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(text.getBytes(StandardCharsets.UTF_8));
            return toHexLower(digest);
        } catch (NoSuchAlgorithmException e) {
            // Java 标准库一定有 MD5，这里仅兜底
            throw new RuntimeException(e);
        }
    }

    /** 等价 Python: get_sign */
    public static String getSign(Map<String, Object> params) {
        if (params == null || params.isEmpty()) return "";

        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);

        List<String> parts = new ArrayList<>();
        for (String k : keys) {
            Object v = params.get(k);

            // JS: (v || v===0) 才参与
            // Java 等价：v != null 且 (不是空字符串)；或者 v 是数字 0
            if (shouldIncludeValue(v)) {
                parts.add(k + "=" + v);
            }
        }

        if (parts.isEmpty()) return "";

        String joined = String.join("&", parts);
        String b64 = jsBase64Encode(joined);

        String md5 = md5Hex(b64 + SIGN_SALT);
        return md5 == null ? "" : md5.toUpperCase(Locale.ROOT);
    }

    private static boolean shouldIncludeValue(Object v) {
        if (v == null) return false;

        if (v instanceof Number) {
            // 保留 0
            return true;
        }

        if (v instanceof Boolean b) {
            // JS 中 true/false：true 会参与，false 也会参与吗？
            // Python 逻辑是 (v or v==0)，对 false 会跳过。
            // 这里按"JS truthy"更贴近：false 跳过。
            return b;
        }

        if (v instanceof CharSequence cs) {
            return cs.length() > 0;
        }

        // 其它对象：有值就参与（类似 JS truthy 但更宽松）
        return true;
    }

    private static String toHexLower(byte[] bytes) {
        char[] hex = "0123456789abcdef".toCharArray();
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i] & 0xFF;
            out[i * 2] = hex[b >>> 4];
            out[i * 2 + 1] = hex[b & 0x0F];
        }
        return new String(out);
    }

    // =========================
    // 5) 使用示例
    // =========================
    public static void main(String[] args) {
        // 示例：解密 token
        Map<String, Object> obj = decryptTokenToObj("V0VVUDA1");
        System.out.println("decrypted_obj: " + obj);
    }
}
