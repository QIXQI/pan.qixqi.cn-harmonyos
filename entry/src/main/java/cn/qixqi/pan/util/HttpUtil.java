package cn.qixqi.pan.util;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.*;


public class HttpUtil {

    // 提交 JSON 格式请求体
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // OkHttp Get 方式获取数据
    public static void sendOkHttpRequest(final String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    // OkHttp Post 方式提交数据
    public static void sendOkHttpRequest(final String address, RequestBody requestBody, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * OkHttp Get 异步方式请求
     * newBuilder()获取一个builder对象，该builder对象与原来OkHttpClient共享相同的连接池，配置
     * @param url
     * @param setHeaders：setHeader Map，清除旧值
     * @param addHeaders：addHeader Map，旧值不被清楚
     * @param callback
     */
    public static void get(final String url, Map<String, String> setHeaders, Map<String, String> addHeaders, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();
        request = setHeaderMap(request, setHeaders);
        request = addHeaderMap(request, addHeaders);
        client.newCall(request).enqueue(callback);
    }

    /**
     * OkHttp Post 异步方式请求
     * @param url
     * @param requestBody
     * @param setHeaders
     * @param addHeaders
     * @param callback
     */
    public static void post(final String url, RequestBody requestBody, Map<String, String> setHeaders, Map<String, String> addHeaders, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        request = setHeaderMap(request, setHeaders);
        request = addHeaderMap(request, addHeaders);
        client.newCall(request).enqueue(callback);
    }

    /**
     * OkHttp Put 异步方式请求
     * @param url
     * @param requestBody
     * @param setHeaders
     * @param addHeaders
     * @param callback
     */
    public static void put(final String url, RequestBody requestBody, Map<String, String> setHeaders, Map<String, String> addHeaders, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();
        request = setHeaderMap(request, setHeaders);
        request = addHeaderMap(request, addHeaders);
        client.newCall(request).enqueue(callback);
    }

    /**
     * OkHttp Delete 异步方式请求
     * @param url
     * @param requestBody
     * @param setHeaders
     * @param addHeaders
     * @param callback
     */
    public static void delete(final String url, RequestBody requestBody, Map<String, String> setHeaders, Map<String, String> addHeaders, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .delete(requestBody)
                .build();
        request = setHeaderMap(request, setHeaders);
        request = addHeaderMap(request, addHeaders);
        client.newCall(request).enqueue(callback);
    }

    /**
     * 在请求中 set header
     * @param request
     * @param setHeaders
     * @return
     */
    private static Request setHeaderMap(Request request, Map<String, String> setHeaders){
        if (setHeaders == null || setHeaders.isEmpty()){
            return request;
        }
        Iterator<Map.Entry<String, String>> entries = setHeaders.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry<String, String> entry = entries.next();
            request = request.newBuilder().header(entry.getKey(), entry.getValue()).build();
        }
        return request;
    }

    /**
     * 在请求中 add header
     * @param request
     * @param addHeaders
     * @return
     */
    private static Request addHeaderMap(Request request, Map<String, String> addHeaders){
        if (addHeaders == null || addHeaders.isEmpty()){
            return request;
        }
        Iterator<Map.Entry<String, String>> entries = addHeaders.entrySet().iterator();
        while (entries.hasNext()){
            Map.Entry<String, String> entry = entries.next();
            request = request.newBuilder().addHeader(entry.getKey(), entry.getValue()).build();
        }
        return request;
    }


    // OkHttp WebSocket 连接方式
    public static void sendOkHttpRequestWithWebSocket(final String address, WebSocketListener listener){
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .connectTimeout(3, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }



}
