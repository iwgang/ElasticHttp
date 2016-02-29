package cn.iwgang.elasticasynchttp;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.SyncHttpClient;

import java.io.IOException;

import cn.iwgang.elastichttp.core.ElasticCall;
import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticRequest;
import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponse;
import cn.iwgang.elastichttp.core.HttpLibrary;
import cn.iwgang.elastichttp.core.RequestTimeOut;
import cz.msebera.android.httpclient.HttpResponse;

/**
 * HttpLibrary android-async-http
 * Created by iWgang on 16/1/26.
 */
public class AsyncHttpLibrary implements HttpLibrary {
    private AsyncHttpClient mAsyncHttpClient; // 异步请求
    private SyncHttpClient mSyncHttpClient; // 同步请求
    private ThreadLocal<SyncElasticResponseWrap> mSyncHttpResponseTL = new ThreadLocal<>();
    private int mDefConnTimeout;
    private int mDefRspTimeout;

    @Override
    public ElasticCall asyncExecute(ElasticRequest request, final ElasticRequestInfo requestInfo, ElasticHttp.ElasticCallbackWrap responseCallbackWrap) {
        if (null == mAsyncHttpClient) throw new RuntimeException("mAsyncHttpClient not initialize");

        RequestTimeOut customRequestTimeOut = request.getRequestTimeOut();
        int connTimeout = 0;
        int rspTimeout = 0;
        if (null != customRequestTimeOut) {
            connTimeout = customRequestTimeOut.getConnectTimeout();
            rspTimeout = customRequestTimeOut.getReadTimeout();
        }

        int finalConnTimeout = connTimeout > 0 ? connTimeout * 1000 : mDefConnTimeout;
        int finalRspTimeout = rspTimeout > 0 ? rspTimeout * 1000 : mDefRspTimeout;

        if (finalConnTimeout != mAsyncHttpClient.getConnectTimeout()) {
            mAsyncHttpClient.setConnectTimeout(finalConnTimeout);
        }

        if (finalRspTimeout != mAsyncHttpClient.getResponseTimeout()) {
            mAsyncHttpClient.setResponseTimeout(finalConnTimeout);
        }

        RequestHandle requestHandle = AsyncHttpRequestHelper.processAsyncExecute(request, requestInfo, mAsyncHttpClient, responseCallbackWrap);
        if (null == requestHandle) {
            responseCallbackWrap.handlerFailure(requestInfo, new RuntimeException("RequestHandle is empty"));
            return null;
        }

        return new AsyncHttpCall(requestHandle);
    }

    @Override
    public ElasticResponse syncExecute(ElasticRequest request, final ElasticRequestInfo requestInfo) throws IOException {
        if (null == mSyncHttpClient) throw new RuntimeException("mSyncHttpClient not initialize");

        RequestTimeOut customRequestTimeOut = request.getRequestTimeOut();
        int connTimeout = 0;
        int rspTimeout = 0;
        if (null != customRequestTimeOut) {
            connTimeout = customRequestTimeOut.getConnectTimeout();
            rspTimeout = customRequestTimeOut.getReadTimeout();
        }

        int finalConnTimeout = connTimeout > 0 ? connTimeout * 1000 : mDefConnTimeout;
        int finalRspTimeout = rspTimeout > 0 ? rspTimeout * 1000 : mDefRspTimeout;

        if (finalConnTimeout != mAsyncHttpClient.getConnectTimeout()) {
            mAsyncHttpClient.setConnectTimeout(finalConnTimeout);
        }

        if (finalRspTimeout != mAsyncHttpClient.getResponseTimeout()) {
            mAsyncHttpClient.setResponseTimeout(finalConnTimeout);
        }

        AsyncHttpRequestHelper.processSyncExecute(request, requestInfo, mSyncHttpClient, new CustomResponseHandlerInterface() {
            @Override
            public void onFailure(int statusCode, IOException e) {
                SyncElasticResponseWrap syncElasticResponseWrap = new SyncElasticResponseWrap();
                syncElasticResponseWrap.retCode = -1;
                syncElasticResponseWrap.statusCode = statusCode;
                syncElasticResponseWrap.e = e;
                mSyncHttpResponseTL.set(syncElasticResponseWrap);
            }

            @Override
            public void onResponse(int statusCode, HttpResponse response) throws IOException {
                SyncElasticResponseWrap syncElasticResponseWrap = new SyncElasticResponseWrap();
                syncElasticResponseWrap.retCode = 0;
                syncElasticResponseWrap.statusCode = statusCode;
                syncElasticResponseWrap.response = response;
                mSyncHttpResponseTL.set(syncElasticResponseWrap);
            }
        });

        SyncElasticResponseWrap retSyncElasticResponseWrap = mSyncHttpResponseTL.get();

        if (null == retSyncElasticResponseWrap) throw new IOException("sync call error");

        if (-1 == retSyncElasticResponseWrap.retCode) throw retSyncElasticResponseWrap.e;

        return new AsyncHttpResponse(retSyncElasticResponseWrap.statusCode, requestInfo, retSyncElasticResponseWrap.response);
    }

    @Override
    public void initialize(RequestTimeOut defRequestTimeOut, boolean isHaveUseSyncRequest, String cacheDir, long cachedMaxSize) {
        mAsyncHttpClient = new AsyncHttpClient();
        mAsyncHttpClient.setMaxRetriesAndTimeout(0, 0);

        if (isHaveUseSyncRequest) {
            mSyncHttpClient = new SyncHttpClient();
            mSyncHttpClient.setMaxRetriesAndTimeout(0, 0);
        }

        mDefConnTimeout = defRequestTimeOut.getConnectTimeout() * 1000;
        mDefRspTimeout = defRequestTimeOut.getReadTimeout() * 1000;
    }

    private static class SyncElasticResponseWrap {
        int retCode; // 0成功, -1失败
        int statusCode;
        IOException e;
        HttpResponse response;
    }

}
