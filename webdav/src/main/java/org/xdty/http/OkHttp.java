package org.xdty.http;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttp {

    private OkHttpClient okHttpClient;

    private OkHttp() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    public static OkHttp getInstance() {
        return SingletonHelper.INSTANCE;
    }

    public OkHttpClient client() {
        return okHttpClient;
    }

    private static class SingletonHelper {
        private final static OkHttp INSTANCE = new OkHttp();
    }

}
