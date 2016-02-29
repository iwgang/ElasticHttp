package cn.iwgang.elastichttp.request;

import java.util.HashMap;
import java.util.Map;

import cn.iwgang.elastichttp.constant.RequestMediaType;
import cn.iwgang.elastichttp.constant.RequestMethod;
import cn.iwgang.elastichttp.core.ElasticRequest;

/**
 * POST 字符串请求
 * Created by iWgang on 16/1/20.
 */
public class PostRequest extends ElasticRequest {
    protected String mMediaType = RequestMediaType.MEDIA_TYPE_PLAIN;
    private String mBodyStr;
    private Map<String, String> mRequestParamMap;

    public PostRequest(String url) {
        super(url, RequestMethod.REQUEST_METHOD_POST);
    }

    public PostRequest(String url, String bodyStr) {
        this(url);
        this.mBodyStr = bodyStr;
    }

    public PostRequest(String url, Map<String, String> requestParamMap) {
        this(url);
        this.mRequestParamMap = requestParamMap;
    }

    public void addRequestParam(String key, String value) {
        if (null == mRequestParamMap) {
            mRequestParamMap = new HashMap<>();
        }
        mRequestParamMap.put(key, value);
    }

    public void setMediaType(String mediaType) {
        this.mMediaType = mediaType;
    }

    public String getBodyStr() {
        return mBodyStr;
    }

    public Map<String, String> getRequestParamMap() {
        return mRequestParamMap;
    }

    public String getMediaType() {
        return mMediaType;
    }

}
