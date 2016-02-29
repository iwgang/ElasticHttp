package cn.iwgang.elastichttp.request;

import java.util.HashMap;
import java.util.Map;

import cn.iwgang.elastichttp.constant.RequestMethod;
import cn.iwgang.elastichttp.core.ElasticRequest;

/**
 * GET 请求
 * Created by iWgang on 16/1/20.
 */
public class GetRequest extends ElasticRequest {
    private Map<String, String> mRequestParamMap;

    public GetRequest(String url) {
        super(url, RequestMethod.REQUEST_METHOD_GET);
    }

    public GetRequest(String url, Map<String, String> requestParamMap) {
        super(url, RequestMethod.REQUEST_METHOD_GET);
        this.mRequestParamMap = requestParamMap;
    }

    public GetRequest(String url, String requestParamKey, String requestParamValue) {
        super(url, RequestMethod.REQUEST_METHOD_GET);
        mRequestParamMap = new HashMap<>();
        mRequestParamMap.put(requestParamKey, requestParamValue);
    }

    public Map<String, String> getRequestParamMap() {
        return mRequestParamMap;
    }
}
