package cn.iwgang.elastichttp.callback;

import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponse;
import cn.iwgang.elastichttp.core.ElasticResponseInfo;

/**
 * Created by iWgang on 16/1/20.
 */
public abstract class ElasticCallback {

    public abstract void handlerSuccess(ElasticResponseInfo responseInfo, ElasticResponse elasticResponse);

    public abstract void handlerFailure(ElasticRequestInfo requestInfo, Exception e);

}
