package com.yisingle.driver.app.http.interceptor;

import android.text.TextUtils;


import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * 日志打印拦截器
 */
public class LoggerInterceptor implements Interceptor {

    private static final String TAG = "http_";
    private boolean showResponse;

    public LoggerInterceptor(String tag, boolean showResponse) {

        this.showResponse = showResponse;

    }

    public LoggerInterceptor(String tag) {
        this(tag, false);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        logForRequest(request);
        Response response = chain.proceed(request);
        return logForResponse(response);
    }

    private Response logForResponse(Response response) {
        try {
            Logger.d("<<<<< response start=======================");
            Response.Builder builder = response.newBuilder();
            Response clone = builder.build();

            Logger.d(clone.code() + "  " + clone.message() + "  " + clone.request().url());

            if (showResponse) {
                ResponseBody body = clone.body();
                if (body != null) {
                    MediaType mediaType = body.contentType();
                    if (mediaType != null) {
                        Logger.d("contentType: " + mediaType.toString());
                        if (isText(mediaType)) {
                            String resp = body.string();
//                            Log.e(tag, "content: " + resp);

                            Logger.json(resp);
                            body = ResponseBody.create(mediaType, resp);
                            Logger.d("<<<<< response end=========================");
                            return response.newBuilder().body(body).build();
                        } else {
                            Logger.d("responseBody's content : maybe [file part] , too large too print , ignored!");
                        }
                    }
                }
            }

            Logger.d("<<<<< response end=========================");
        } catch (Exception e) {
//            e.printStackTrace();
        }

        return response;
    }

    private void logForRequest(Request request) {
        try {
            String url = request.url().toString();
            Headers headers = request.headers();

            Logger.d(">>>>> request start_________________");
            Logger.d(request.method() + ' ' + url);
            if (headers != null && headers.size() > 0) {
                Logger.d("headers : " + headers.toString());
            }
            RequestBody requestBody = request.body();
            if (requestBody != null) {
                MediaType mediaType = requestBody.contentType();
                if (mediaType != null) {
                    Logger.d("contentType : " + mediaType.toString());
                    if (isText(mediaType)) {
                        Logger.json(bodyToString(request));
                    } else {
                        Logger.d("content : maybe [file part] , too large too print , ignored!");
                    }
                }
            }
            Logger.d(">>>>> end request_________________");
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    private boolean isText(MediaType mediaType) {
        if (mediaType.type() != null && "text".equals(mediaType.type())) {
            return true;
        }
        if (mediaType.subtype() != null) {
            if ("json".equals(mediaType.subtype()) ||
                    "xml".equals(mediaType.subtype()) ||
                    "html".equals(mediaType.subtype()) ||
                    "webviewhtml".equals(mediaType.subtype())
                    )
                return true;
        }
        return false;
    }

    private String bodyToString(final Request request) {
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "something error when show requestBody.";
        }
    }
}
