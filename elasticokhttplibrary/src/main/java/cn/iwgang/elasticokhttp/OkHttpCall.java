package cn.iwgang.elasticokhttp;

import cn.iwgang.elastichttp.core.ElasticCall;
import okhttp3.Call;

/**
 * Created by iWgang on 16/1/26.
 */
public class OkHttpCall implements ElasticCall {
    private Call mOkHttpCall;

    public OkHttpCall(Call mOkHttpCall) {
        this.mOkHttpCall = mOkHttpCall;
    }

    @Override
    public void cancel() {
        mOkHttpCall.cancel();
    }

    @Override
    public boolean isCancel() {
        return mOkHttpCall.isCanceled();
    }

}
