package org.xdty.autoindex.data;

import org.xdty.autoindex.module.IndexFile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Url;

public interface NginxService {
    @GET
    Call<List<IndexFile>> list(@Header("Authorization") String basic, @Url String url);
}
