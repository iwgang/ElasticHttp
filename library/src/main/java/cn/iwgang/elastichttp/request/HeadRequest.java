package cn.iwgang.elastichttp.request;

import java.util.HashMap;
import java.util.Map;

import cn.iwgang.elastichttp.constant.RequestMethod;
import cn.iwgang.elastichttp.core.ElasticRequest;

/**
 * HEAD 请求
 * Created by iWgang on 16/1/20.
 */
public class HeadRequest extends ElasticRequest {
    private Map<String, String> mRequestParamMap;

    public HeadRequest(String url) {
        super(url, RequestMethod.REQUEST_METHOD_GET);
    }

    public HeadRequest(String url, Map<String, String> requestParamMap) {
        super(url, RequestMethod.REQUEST_METHOD_HEAD);
        this.mRequestParamMap = requestParamMap;
    }

    public HeadRequest(String url, String requestParamKey, String requestParamValue) {
        super(url, RequestMethod.REQUEST_METHOD_HEAD);
        mRequestParamMap = new HashMap<>();
        mRequestParamMap.put(requestParamKey, requestParamValue);
    }

    public Map<String, String> getRequestParamMap() {
        return mRequestParamMap;
    }
}
