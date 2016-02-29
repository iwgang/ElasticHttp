package cn.iwgang.elasticasynchttp;

import com.loopj.android.http.RequestHandle;

import cn.iwgang.elastichttp.core.ElasticCall;

/**
 * Created by iWgang on 16/1/27.
 */
public class AsyncHttpCall implements ElasticCall {
    private RequestHandle mRequestHandle;

    public AsyncHttpCall(RequestHandle requestHandle) {
        this.mRequestHandle = requestHandle;
    }

    @Override
    public void cancel() {
        mRequestHandle.cancel(true);
    }

    @Override
    public boolean isCancel() {
        return mRequestHandle.isCancelled();
    }

}
