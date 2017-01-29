package com.scrat.app.bus.net;

import android.text.TextUtils;

import com.scrat.app.bus.utils.L;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yixuanxuan on 16/1/15.
 */
public class OkHttpHelper {
    private static class SingletonHolder {
        private static OkHttpHelper instance = new OkHttpHelper();
    }

    public static OkHttpHelper getInstance() {
        return SingletonHolder.instance;
    }

    private OkHttpClient client;

    private OkHttpHelper() {
        client = new OkHttpClient();
    }

    private Request buildPostFormRequest(String url, Map<String, String> headers, Map<String, String> params) throws UnsupportedEncodingException {
        FormBody.Builder builder = new FormBody.Builder();
        if (!isEmpty(params)) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addEncoded(entry.getKey(), URLEncoder.encode(entry.getValue(), "UTF-8"));
            }
        }

        RequestBody requestBody = builder.build();
        Request.Builder reqBuilder = new Request.Builder();
        if (!isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                reqBuilder.header(entry.getKey(), entry.getValue());
            }
        }
        reqBuilder.url(url).post(requestBody);

        return reqBuilder.build();
    }

    private Request buildGetFormRequest(String url, Map<String, String> headers, Map<String, String> params) throws UnsupportedEncodingException {
        if (!isEmpty(params)) {
            StringBuilder sb = new StringBuilder(url);
            sb.append('?');
            for (Map.Entry<String, String> entry : params.entrySet()) {
                sb.append(entry.getKey()).append('=').append(entry.getValue()).append('&');
            }
            sb.deleteCharAt(sb.lastIndexOf("&"));
            url = sb.toString();
        }
        Request.Builder builder = new Request.Builder().url(url);
        if (!isEmpty(headers)) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        return builder.build();
    }

    private String getResponseBody(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Unexpected code " + response.code() + ", " + response.message());
        }

        String encoding = "UTF-8";
        String contextType = response.headers().get("Content-Type");
        if (!TextUtils.isEmpty(contextType)) {
            if (contextType.toUpperCase().contains("UTF")) {
                encoding = "UTF-8";
            } else if (contextType.toUpperCase().contains("GBK")) {
                encoding = "GBK";
            }
        }
        return new String(response.body().bytes(), encoding);
    }

    private Response getResponse(Request request) throws IOException {
        int maxRetryTimes = 3;
        for (int i = 0; i < maxRetryTimes; i++) {
            try {
                return client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
                int totalRequestTimes = i + 1;
                L.d("retry %s", totalRequestTimes);
                if (totalRequestTimes == maxRetryTimes) {
                    throw e;
                }
            }
        }

        throw new IOException("retry fail");
    }

    public Call post(String url, Map<String, String> params, Map<String, String> headers, Callback responseCallback) throws UnsupportedEncodingException {
        L.d("url %s", url);
        L.d("post %s", params);
        Request request = buildPostFormRequest(url, headers, params);
        Call call = client.newCall(request);
        call.enqueue(responseCallback);
        return call;
    }

    public String get(String url, Map<String, String> params) throws IOException {
        L.d("[get] %s", url);
        L.d("[params] %s", params);
        Request request = buildGetFormRequest(url, null, params);
        Response response = getResponse(request);
        L.d("%s", response.code());
        String body = getResponseBody(response);
        L.d("[response] %s", body);
        return body;
    }

    public String get(String url) throws IOException {
        return get(url, Collections.<String, String>emptyMap());
    }

    public void get(String url, Map<String, String> params, Map<String, String> headers, Callback responseCallback) throws UnsupportedEncodingException {
        L.d("[get] %s", url);
        L.d("[params] %s", params);
        Request request = buildGetFormRequest(url, headers, params);
        client.newCall(request).enqueue(responseCallback);
    }

    public void get(String url, Callback responseCallback) throws UnsupportedEncodingException {
        get(url, null, null, responseCallback);
    }

    private boolean isEmpty(Map<?, ?> map) {
        return map == null || map.size() == 0;
    }

    public static class ParamsBuilder {
        Map<String, String> params;

        public ParamsBuilder() {
            this.params = new HashMap<>();
        }

        public ParamsBuilder put(String key, String value) {
            params.put(key, value);
            return this;
        }

        public ParamsBuilder put(String key, long value) {
            params.put(key, String.valueOf(value));
            return this;
        }

        public ParamsBuilder put(String key, int value) {
            params.put(key, String.valueOf(value));
            return this;
        }

        public ParamsBuilder put(String key, boolean value) {
            params.put(key, String.valueOf(value));
            return this;
        }

        public Map<String, String> build() {
            return params;
        }
    }

    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("application/xml");

    public void put(String url, String path) {
        final File file = new File(path);

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(MEDIA_TYPE_MARKDOWN, file))
                .build();

        try {
            Response response = getResponse(request);
            L.e("code=%s", response.code());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
