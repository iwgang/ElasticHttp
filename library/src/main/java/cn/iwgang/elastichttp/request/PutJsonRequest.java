package cn.iwgang.elastichttp.request;

import cn.iwgang.elastichttp.constant.RequestMediaType;

/**
 * PUT Json字符串请求
 * Created by iWgang on 16/1/20.
 */
public class PutJsonRequest extends PutRequest {

    public PutJsonRequest(String url, String bodyStr) {
        super(url, bodyStr);
        mMediaType = RequestMediaType.MEDIA_TYPE_JSON;
    }

}
