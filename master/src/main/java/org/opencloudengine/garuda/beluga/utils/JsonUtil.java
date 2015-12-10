package org.opencloudengine.garuda.beluga.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by swsong on 2015. 8. 16..
 */
public class JsonUtil {
    public static Map<String, Object> json2Object(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<HashMap<String, Object>> typeReference = new TypeReference<HashMap<String, Object>>() { };
        return objectMapper.readValue(json, typeReference);
    }

    public static <T> T json2Object(String json, Class<T> clazz) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Object o = objectMapper.readValue(json, clazz);
        return (T) o;
    }

    static String dateFormat = "yyyy-MM-dd hh:mm:ss";
    public static String object2String(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        return mapper.writer(sdf).withDefaultPrettyPrinter().writeValueAsString(object);
    }

    public static JsonNode toJsonNode(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(json);
    }
}
