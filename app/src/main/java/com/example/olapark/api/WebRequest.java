package com.example.olapark.api;

import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

public class WebRequest {
    public static AsyncHttpClient client;

    static {
        client = new AsyncHttpClient(true, 80, 443);
    }

    public static void post(Context context, String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        client.post(context, url,params, responseHandler);
    }
}
