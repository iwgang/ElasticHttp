package cn.iwgang.elastichttp.core;

import java.util.Map;

import cn.iwgang.elastichttp.constant.RequestMethod;

/**
 * Created by iWgang on 16/1/21.
 */
public class ElasticRequestInfo {
    private String url; // 请求url
    private String body; // 请求body
    private int method; // RequestMethod.REQUEST_METHOD_*
    private Map<String, String> headerMap;
    private Object tag; // tag object

    public ElasticRequestInfo(String url, int method) {
        this(url, method, null);
    }

    public ElasticRequestInfo(String url, int method, Map<String, String> headerMap) {
        this.url = url;
        this.method = method;
        this.headerMap = headerMap;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getHeaderMap() {
        return headerMap;
    }

    public void setHeaderMap(Map<String, String> headerMap) {
        this.headerMap = headerMap;
    }

    public int getMethod() {
        return method;
    }

    public String getMethodStr() {
        return RequestMethod.toMethodStr(method);
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "ElasticRequestInfo{" +
                "body='" + body + '\'' +
                ", url='" + url + '\'' +
                ", method='" + method + '\'' +
                ", headerMap=" + headerMap +
                ", tag=" + tag +
                '}';
    }

}
