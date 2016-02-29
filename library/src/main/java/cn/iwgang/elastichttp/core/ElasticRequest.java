package cn.iwgang.elastichttp.core;

import java.util.HashMap;
import java.util.Map;

import cn.iwgang.elastichttp.constant.RequestDataMode;
import cn.iwgang.elastichttp.constant.RequestMethod;

/**
 * 请求基类
 * Created by iWgang on 16/1/20.
 */
public abstract class ElasticRequest {
    private int mHttpLibraryKey = ElasticHttpConfiguration.DEF_HTTP_LIBRARY_KEY;
    private String mUrl;
    private int mMethod; // 使用 RequestMethod.REQUEST_METHOD_*
    private Map<String, String> mHeaderMap;
    private Object mTag;
    private RequestTimeOut mRequestTimeOut;
    private int mRequestDataMode = RequestDataMode.ONLY_NET; // 使用 RequestDataMode.*

    protected ElasticRequest(String url, int method) {
        this.mUrl = url;
        this.mMethod = method;
    }

    public void addHeader(String key, String value) {
        if (null == mHeaderMap) {
            mHeaderMap = new HashMap<>();
        }

        mHeaderMap.put(key, value);
    }

    public void setHeaders(Map<String, String> headerMap) {
        this.mHeaderMap = headerMap;
    }

    public void setTag(Object mTag) {
        this.mTag = mTag;
    }

    public void setRequestTimeOut(RequestTimeOut requestTimeOut) {
        this.mRequestTimeOut = requestTimeOut;
    }

    public void setRequestDataMode(int requestDataMode) {
        this.mRequestDataMode = requestDataMode;
    }

    public void setHttpLibraryKey(int httpLibraryKey) {
        this.mHttpLibraryKey = httpLibraryKey;
    }

    public int getMethod() {
        return mMethod;
    }

    public String getMethodStr() {
        return RequestMethod.toMethodStr(mMethod);
    }

    public String getUrl() {
        return mUrl;
    }

    public Map<String, String> getHeaderMap() {
        return mHeaderMap;
    }

    public Object getTag() {
        return mTag;
    }

    public RequestTimeOut getRequestTimeOut() {
        return mRequestTimeOut;
    }

    public int getRequestDataMode() {
        return mRequestDataMode;
    }

    public int getHttpLibraryKey() {
        return mHttpLibraryKey;
    }

    protected void setUploadProgressListener(OnUploadProgressListener onUploadProgressListener) {}


}
