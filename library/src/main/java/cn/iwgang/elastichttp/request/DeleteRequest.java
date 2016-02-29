package cn.iwgang.elastichttp.request;

import java.util.HashMap;
import java.util.Map;

import cn.iwgang.elastichttp.constant.RequestMethod;
import cn.iwgang.elastichttp.core.ElasticRequest;

/**
 * DELETE 请求
 * Created by iWgang on 16/1/20.
 */
public class DeleteRequest extends ElasticRequest {
    private Map<String, String> mRequestParamMap;

    public DeleteRequest(String url) {
        super(url, RequestMethod.REQUEST_METHOD_DELETE);
    }

    public DeleteRequest(String url, Map<String, String> requestParamMap) {
        super(url, RequestMethod.REQUEST_METHOD_DELETE);
        this.mRequestParamMap = requestParamMap;
    }

    public DeleteRequest(String url, String requestParamKey, String requestParamValue) {
        super(url, RequestMethod.REQUEST_METHOD_DELETE);
        mRequestParamMap = new HashMap<>();
        mRequestParamMap.put(requestParamKey, requestParamValue);
    }

    public Map<String, String> getRequestParamMap() {
        return mRequestParamMap;
    }
}
