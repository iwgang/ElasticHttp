package cn.iwgang.elastichttp.interceptor;

import cn.iwgang.elastichttp.core.ElasticRequest;
import cn.iwgang.elastichttp.core.ElasticResponse;

/**
 * Created by iwgang on 16/1/31.
 */
public interface ElasticInterceptor {

    void onRequest(ElasticRequest request);

    void onResponse(boolean isSuccess, ElasticResponse elasticResponse, Exception ex);

}
