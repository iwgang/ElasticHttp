package cn.iwgang.elastichttp.callback;

import java.io.IOException;

import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponse;
import cn.iwgang.elastichttp.core.ElasticResponseInfo;

/**
 * Created by iWgang on 16/1/20.
 */
public abstract class TextCallback extends ElasticCallback {

    @Override
    public void handlerFailure(final ElasticRequestInfo requestInfo, final Exception e) {
        ElasticHttp.getInstance().runMain(new Runnable() {
            @Override
            public void run() {
                onFailure(requestInfo, e);
            }
        });
    }

    @Override
    public void handlerSuccess(final ElasticResponseInfo responseInfo, final ElasticResponse elasticResponse) {
        String retStr;
        try {
            // 解析
            retStr = elasticResponse.getBodyStr();
        } catch (IOException e) {
            // 回调成功
            handlerFailure(responseInfo.getRequestInfo(), e);
            return ;
        }

        final String finalRetStr = retStr;
        ElasticHttp.getInstance().runMain(new Runnable() {
            @Override
            public void run() {
                // 回调成功
                onSuccess(responseInfo, finalRetStr);
            }
        });
    }

    public abstract void onSuccess(ElasticResponseInfo responseInfo, String retStr);


    public abstract void onFailure(ElasticRequestInfo requestInfo, Exception e);

}
