package cn.iwgang.elastichttp.request;

import cn.iwgang.elastichttp.constant.RequestMediaType;

/**
 * POST Json字符串请求
 * Created by iWgang on 16/1/20.
 */
public class PostJsonRequest extends PostRequest {

    public PostJsonRequest(String url, String bodyStr) {
        super(url, bodyStr);
        mMediaType = RequestMediaType.MEDIA_TYPE_JSON;
    }

}
