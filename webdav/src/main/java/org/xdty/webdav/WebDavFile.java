package org.xdty.webdav;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.xdty.webdav.model.MultiStatus;
import org.xdty.webdav.model.Prop;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebDavFile {

    public final static String TAG = WebDavFile.class.getSimpleName();

    private final static String DIR = "<?xml version=\"1.0\"?>\n" +
            "<a:propfind xmlns:a=\"DAV:\">\n" +
            "<a:prop><a:resourcetype/></a:prop>\n" +
            "</a:propfind>";

    private final static String DAV_SCHEME = "dav://";
    private final static String DAVS_SCHEME = "davs://";

    private final static String HTTP_SCHEME = "http://";
    private final static String HTTPS_SCHEME = "https://";

    //private final static String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
    private final static String ALLOWED_URI_CHARS = "*-_.,:!()/~'";
    private URI mUri;
    private String httpUrl = "";
    private String canon;
    private long createTime;
    private long lastModified;
    private long size;
    private boolean isDirectory = true;
    private String parent = "";
    private String urlName = "";

    private OkHttpClient okHttpClient;

    public WebDavFile(String uri) throws URISyntaxException {
        this.mUri = new URI(uri);
        okHttpClient = OkHttp.getInstance().client();
    }

    public String getUrl() {

        if (httpUrl.isEmpty()) {
            httpUrl = mUri.toString()
                    .replace(DAV_SCHEME, HTTP_SCHEME)
                    .replace(DAVS_SCHEME, HTTPS_SCHEME);
        }
        return httpUrl;
    }

    public String getPath() {
        return mUri.toString();
    }

    public WebDavFile[] listFiles() throws MalformedURLException {

        Request.Builder request = new Request.Builder()
                .url(getUrl())
                .method("PROPFIND", RequestBody.create(MediaType.parse("text/plain"), DIR));

        WebDavAuth.Auth auth = WebDavAuth.getAuth(mUri.toString());
        if (auth != null) {
            request.header("Authorization", Credentials.basic(auth.getUser(), auth.getPass()));
        }

        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            String s = response.body().string();
            return parseDir(s);
        } catch (IOException | XmlPullParserException | IllegalArgumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    public InputStream getInputStream() {
        Request.Builder request = new Request.Builder()
                .url(getUrl());

        WebDavAuth.Auth auth = WebDavAuth.getAuth(mUri.toString());

        if (auth != null) {
            request.header("Authorization", Credentials.basic(auth.getUser(), auth.getPass()));
        }

        try {
            Response response = okHttpClient.newCall(request.build()).execute();
            return response.body().byteStream();
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WebDavFile[] parseDir(String s) throws XmlPullParserException, IOException {

        List<WebDavFile> list = new ArrayList<>();

        Serializer serializer = new Persister();
        try {
            MultiStatus multiStatus = serializer.read(MultiStatus.class, s);
            String parent = URLDecoder.decode(mUri.toString().replace("+", "%2B"), "utf-8");
            for (org.xdty.webdav.model.Response response : multiStatus.getResponse()) {
                String path = mUri.getScheme() + "://" + getHost() +
                        (mUri.getPort() == -1 ? "" : ":" + mUri.getPort()) +
                        URLDecoder.decode(response.getHref().replace("+", "%2B"), "utf-8");

                if (path.equalsIgnoreCase(parent)) {
                    continue;
                }

                WebDavFile webDavFile = new WebDavFile(path);
                Prop prop = response.getPropstat().getProp();
                webDavFile.setCanon(prop.getDisplayname());
                webDavFile.setCreateTime(0);
                webDavFile.setLastModified(0);
                webDavFile.setSize(prop.getGetcontentlength());
                webDavFile.setIsDirectory(prop.getResourcetype().getCollection() != null);
                webDavFile.setParent(parent);
                list.add(webDavFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list.toArray(new WebDavFile[list.size()]);
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
            urlName = (parent.isEmpty() ? mUri.getPath() : mUri.toString().replace(parent, "")).
                    replace("/", "");
        }
        return urlName;
    }

    public String getHost() {
        return mUri.getHost();
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
}