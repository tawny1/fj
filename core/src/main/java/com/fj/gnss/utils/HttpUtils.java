package com.fj.gnss.utils;

import android.text.TextUtils;

import com.fj.gnss.GnssManager;
import com.fjdynamics.xlog.FJXLog;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import okio.BufferedSource;

public class HttpUtils {

    private static final String TAG = HttpUtils.class.getSimpleName();

    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(@NotNull HttpUrl url, @NotNull List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @NotNull
                @Override
                public List<Cookie> loadForRequest(@NotNull HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
//            //添加一个缓存
//            .cache(new Cache(App.getContext().getExternalCacheDir() == null ? App.getContext().getCacheDir() :
//                    App.getContext().getExternalCacheDir(), 5 * 1024 * 1024))
            //添加一个日志拦截器
            .addInterceptor(chain -> {
                Request request = chain.request();
                RequestBody requestBody = request.body();
                String body = null;
                if (requestBody != null) {
                    Buffer buffer = new Buffer();
                    requestBody.writeTo(buffer);

                    Charset charset = StandardCharsets.UTF_8;
                    MediaType contentType = requestBody.contentType();
                    if (contentType != null) {
                        charset = contentType.charset(StandardCharsets.UTF_8);
                    }
                    body = buffer.readString(charset);
                }
                FJXLog.INSTANCE.d(TAG, "request = " + request);
                FJXLog.INSTANCE.d(TAG, "requestBody = " + body);

                Response response = chain.proceed(request);
                if (response.body() != null) {
                    BufferedSource source = response.body().source();
                    source.request(Long.MAX_VALUE);
                    String bodyStr = source.buffer().clone().readString(StandardCharsets.UTF_8);
                    FJXLog.INSTANCE.d(TAG, "response.body.bodyStr= " + bodyStr);
                }
                return response;
            })
            .build();

    /**
     * 获取千寻服务状态
     *
     * @param callback
     */
    public static void getQxStatus(Callback callback) {
        //请填写申请的sik
        String sik = "S000000KUAM";
        //请填写您申请的sik对应的sis
        String sis = "37d271c0de75e4cedce2dadf4f23eea0eae799b2b063cf79524b6ea9b1519173";
        // 访问的API接口名称
        String apiName = "findcm.dsk.queryByDevice";
        // API路径，注意没有问号
        String apiPath = String.format(Locale.US, "/rest/%s/sik/%s", apiName, sik);

        // 参数列表(加签的时候需要进行字典序升序排序)
        final Map<String, String> paramMap = new HashMap<>();
        if (GnssManager.getInstance().getRTKCommunicationCallBack()!=null){
            if (!TextUtils.isEmpty(GnssManager.getInstance().getRTKCommunicationCallBack().getDeviceSn())){
                paramMap.put("deviceId", GnssManager.getInstance().getRTKCommunicationCallBack().getDeviceSn());
            }
        }
        paramMap.put("deviceType", "fjdaidrive");
        // 毫秒级时间戳，一定是毫秒级！重要！！！
        final String timestamp = String.valueOf(System.currentTimeMillis());
        String signatureStr = DataUtil.doHmacSHA2(apiPath, paramMap, sis, timestamp);
        final String url = String.format(Locale.US, "http://openapi.qxwz.com%s?_sign=%s", apiPath, signatureStr);
        FJXLog.INSTANCE.e(TAG, "getQxStatus:url=" + url);
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            builder.add(entry.getKey(), entry.getValue());
        }
        final Request request = new Request.Builder()
                .addHeader("wz-acs-timestamp", timestamp)
                .post(builder.build())
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(callback);
    }
    public static void getQxStatus(Callback callback,Map<String,String> paramMap,String url) {
        //请填写申请的sik
//        String sik = "S000000KUAM";
//        //请填写您申请的sik对应的sis
//        String sis = "37d271c0de75e4cedce2dadf4f23eea0eae799b2b063cf79524b6ea9b1519173";
//        // 访问的API接口名称
//        String apiName = "findcm.dsk.queryByDevice";
//        // API路径，注意没有问号
//        String apiPath = String.format(Locale.US, "/rest/%s/sik/%s", apiName, sik);
//
//        // 参数列表(加签的时候需要进行字典序升序排序)
//        final Map<String, String> paramMap = new HashMap<>();
//        if (GnssManager.getInstance().getRTKCommunicationCallBack()!=null){
//            if (!TextUtils.isEmpty(GnssManager.getInstance().getRTKCommunicationCallBack().getDeviceSn())){
//                paramMap.put("deviceId", GnssManager.getInstance().getRTKCommunicationCallBack().getDeviceSn());
//            }
//        }
//        paramMap.put("deviceType", "fjdaidrive");
//        // 毫秒级时间戳，一定是毫秒级！重要！！！
//
//        String signatureStr = DataUtil.doHmacSHA2(apiPath, paramMap, sis, timestamp);
//        final String url = String.format(Locale.US, "http://openapi.qxwz.com%s?_sign=%s", apiPath, signatureStr);
        final String timestamp = String.valueOf(System.currentTimeMillis());
        FJXLog.INSTANCE.e(TAG, "getQxStatus:url=" + url);
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : paramMap.entrySet()) {
            if (entry.getValue() == null) {
                continue;
            }
            builder.add(entry.getKey(), entry.getValue());
        }
        final Request request = new Request.Builder()
                .addHeader("wz-acs-timestamp", timestamp)
                .post(builder.build())
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(callback);

    }
}
