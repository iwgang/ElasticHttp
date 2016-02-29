package cn.iwgang.elastichttp.core;

import java.util.Map;

/**
 * Created by iwgang on 16/1/30.
 */
public class ElasticResponseInfo {
    private ElasticRequestInfo requestInfo;
    private int httpCode;
    private boolean isCache;
    private Map<String, String> responseHeader;

    public ElasticResponseInfo(ElasticRequestInfo requestInfo, int httpCode, Map<String, String> responseHeader, boolean isCache) {
        this.requestInfo = requestInfo;
        this.httpCode = httpCode;
        this.responseHeader = responseHeader;
        this.isCache = isCache;
    }

    public ElasticRequestInfo getRequestInfo() {
        return requestInfo;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public Map<String, String> getResponseHeader() {
        return responseHeader;
    }

    public String getResponseHeader(String key) {
        if (null != responseHeader && responseHeader.containsKey(key)) {
            return responseHeader.get(key);
        }

        return null;
    }

    public boolean isCache() {
        return isCache;
    }

}
