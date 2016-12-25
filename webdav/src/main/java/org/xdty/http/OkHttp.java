package org.xdty.http;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttp {

    private OkHttpClient okHttpClient;

    private OkHttp() {
    }

    public static OkHttp getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public void setClient(OkHttpClient client) {
        okHttpClient = client;
    }

    public OkHttpClient client() {

        if (okHttpClient == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        }
        return okHttpClient;
    }

    private static class SingletonHelper {
        private final static OkHttp INSTANCE = new OkHttp();
    }

}
