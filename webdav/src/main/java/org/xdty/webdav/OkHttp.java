package org.xdty.webdav;

import okhttp3.OkHttpClient;

class OkHttp {

    private OkHttpClient okHttpClient;

    private OkHttp() {
        okHttpClient = new OkHttpClient();
    }

    static OkHttp getInstance() {
        return SingletonHelper.INSTANCE;
    }

    OkHttpClient client() {
        return okHttpClient;
    }

    private static class SingletonHelper {
        private final static OkHttp INSTANCE = new OkHttp();
    }

}
