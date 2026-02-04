package com.xxl.job.executor.biz.sellfox.config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONObject;

@Component
public class SellfoxParamConfig {
    /** 内存缓存：key -> 配置模板 */
    private final Map<String, JSONObject> templateMap = new HashMap<>();

    public SellfoxParamConfig() {
        loadConfig();
    }

    private void loadConfig() {
        try {
            ClassPathResource resource = new ClassPathResource("getDataParams.json");
            try (InputStream is = resource.getInputStream()) {
                String json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                JSONObject root = JSONObject.parseObject(json);
                for (String key : root.keySet()) {
                    templateMap.put(key, root.getJSONObject(key));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("加载 getDataParams.json 失败", e);
        }
    }

    /**
     * 根据 key 获取一份“拷贝”，避免修改模板本身
     */
    public JSONObject getParams(String key) {
        JSONObject template = templateMap.get(key);
        if (template == null) {
            throw new IllegalArgumentException("未找到 Sellfox 参数配置，key=" + key);
        }
        // 返回深拷贝，避免后续 put 改动影响缓存中的模板
        return JSONObject.parseObject(template.toJSONString());
    }
}
