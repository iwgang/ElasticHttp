package cn.iwgang.elasticokhttp;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponse;
import okhttp3.Headers;
import okhttp3.Response;

/**
 * OkHttp HttpLibrary响应实现
 * Created by iWgang on 16/1/27.
 */
public class OkHttpResponse implements ElasticResponse {
    private Response mResponse;
    private ElasticRequestInfo mRequestInfo;

    public OkHttpResponse(Response response, ElasticRequestInfo requestInfo) {
        this.mResponse = response;
        this.mRequestInfo = requestInfo;
    }

    @Override
    public String getBodyStr() throws IOException {
        return mResponse.body().string();
    }

    @Override
    public InputStream getBodyIs() throws IOException {
        return mResponse.body().byteStream();
    }

    @Override
    public int getHttpCode() {
        return mResponse.code();
    }

    @Override
    public long getContentLength() {
        return mResponse.body().contentLength();
    }

    @Override
    public String getEncodedPath() {
        return mResponse.request().url().encodedPath();
    }

    @Override
    public boolean isSuccessful() {
        return mResponse.isSuccessful();
    }

    @Override
    public Map<String, String> getResponseHeader() {
        Map<String, String> rspHeaderMap = null;
        Headers headers = mResponse.headers();
        if (null != headers && headers.size() > 0) {
            rspHeaderMap = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                rspHeaderMap.put(headers.name(i), headers.value(i));
            }
        }

        return rspHeaderMap;
    }

    @Override
    public ElasticRequestInfo getRequestInfo() {
        return mRequestInfo;
    }

}
