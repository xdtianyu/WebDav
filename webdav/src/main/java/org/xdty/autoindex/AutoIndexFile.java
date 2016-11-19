package org.xdty.autoindex;

import org.xdty.autoindex.data.NginxService;
import org.xdty.autoindex.module.IndexFile;
import org.xdty.http.Handler;
import org.xdty.http.HttpAuth;
import org.xdty.http.OkHttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AutoIndexFile {

    private URL url;
    private String httpUrl;

    private String canon;
    private long createTime;
    private long lastModified;
    private long size;
    private boolean isDirectory = true;
    private String parent = "";
    private String urlName = "";

    private String mAuth;

    private OkHttpClient mOkHttpClient;
    private NginxService mNginxService;

    public AutoIndexFile() {
    }

    public AutoIndexFile(String url) throws MalformedURLException {
        this.url = new URL(null, url, Handler.HANDLER);
        mOkHttpClient = OkHttp.getInstance().client();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://nginx.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(mOkHttpClient)
                .build();
        mNginxService = retrofit.create(NginxService.class);

        mAuth = HttpAuth.Auth.basic(url);
    }

    private AutoIndexFile(String url, NginxService service) throws MalformedURLException {
        this.url = new URL(null, url, Handler.HANDLER);
        mOkHttpClient = OkHttp.getInstance().client();
        mNginxService = service;
        mAuth = HttpAuth.Auth.basic(url);
    }

    public String getUrl() {
        if (httpUrl == null) {
            String raw = url.toString()
                    .replace("indexs://", "https://")
                    .replace("index://", "http://");
            try {
                httpUrl = URLEncoder.encode(raw, "UTF-8")
                        .replaceAll("\\+", "%20")
                        .replaceAll("%3A", ":")
                        .replaceAll("%2F", "/");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return httpUrl;
    }

    public String getPath() {
        return url.toString();
    }

    public List<AutoIndexFile> listFiles() throws MalformedURLException {

        List<AutoIndexFile> autoIndexFiles = new ArrayList<>();
        try {
            List<IndexFile> files = mNginxService.list(mAuth, getUrl())
                    .execute()
                    .body();

            String path = getPath();
            if (!path.endsWith("/")) {
                path += "/";
            }

            if (files != null) {
                for (IndexFile file : files) {

                    AutoIndexFile autoIndexFile = new AutoIndexFile(path + file.getName(),
                            mNginxService);
                    autoIndexFile.setNginxService(mNginxService);
                    autoIndexFile.setParent(getPath());
                    autoIndexFile.setIsDirectory(file.getType() == IndexFile.Type.DIRECTORY);
                    autoIndexFile.setSize(file.getSize());
                    autoIndexFiles.add(autoIndexFile);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return autoIndexFiles;
    }

    public InputStream getInputStream() {
        Request.Builder request = new Request.Builder()
                .url(getUrl());

        HttpAuth.Auth auth = HttpAuth.getAuth(url.toString());

        if (auth != null) {
            request.header("Authorization", Credentials.basic(auth.getUser(), auth.getPass()));
        }

        try {
            Response response = mOkHttpClient.newCall(request.build()).execute();
            return response.body().byteStream();
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getCanon() {
        return canon;
    }

    public void setCanon(String canon) {
        this.canon = canon;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public String getName() {
        return getURLName();
    }

    public String getURLName() {
        if (urlName.isEmpty()) {
            urlName = (parent.isEmpty() ? url.getFile() : url.toString().replace(parent, "")).
                    replace("/", "");
        }
        return urlName;
    }

    public String getHost() {
        return url.getHost();
    }

    public boolean canRead() {
        return true;
    }

    public boolean canWrite() {
        return false;
    }

    public boolean exists() {
        return true;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String path) {
        parent = path;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    private void setNginxService(NginxService service) {
        mNginxService = service;
    }

}
