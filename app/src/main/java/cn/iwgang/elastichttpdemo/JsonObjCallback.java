package cn.iwgang.elastichttpdemo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;

import cn.iwgang.elastichttp.callback.ElasticCallback;
import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponse;
import cn.iwgang.elastichttp.core.ElasticResponseInfo;

/**
 * Created by iWgang on 16/1/20.
 */
public abstract class JsonObjCallback<T> extends ElasticCallback {

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
        T obj;
        try {
            // 回调成功
            obj = new Gson().fromJson(elasticResponse.getBodyStr(), new TypeToken<T>(){}.getType());
        } catch (IOException e) {
            e.printStackTrace();
            // 回调失败
            handlerFailure(responseInfo.getRequestInfo(), e);
            return ;
        }

        final T finalObj = obj;
        ElasticHttp.getInstance().runMain(new Runnable() {
            @Override
            public void run() {
                // 回调成功
                onSuccess(responseInfo, finalObj);
            }
        });
    }


    public abstract void onSuccess(ElasticResponseInfo responseInfo, T t);

    public abstract void onFailure(ElasticRequestInfo requestInfo, Exception e);

}
