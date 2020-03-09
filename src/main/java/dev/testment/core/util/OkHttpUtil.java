package dev.testment.core.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public class OkHttpUtil {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public OkHttpUtil() { }

    public static RequestBody createJsonBody(ObjectMapper mapper, Object object) {
        try {
            return RequestBody.create(mapper.writeValueAsString(object), JSON);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static RequestBody createEmptyJsonBody() {
        return RequestBody.create("{}", JSON);
    }

    public static Response executeRequest(OkHttpClient client, Request request) {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseResponseAsString(Response response) {
        try {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T parseResponse(ObjectMapper mapper, Response response, Class<T> clazz) {
        try {
            String body = response.body().string();
            return mapper.readValue(body, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
