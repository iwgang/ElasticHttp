package cn.iwgang.elasticokhttp;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cn.iwgang.elastichttp.callback.ElasticCallback;
import cn.iwgang.elastichttp.core.ElasticCall;
import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticRequest;
import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponse;
import cn.iwgang.elastichttp.core.ElasticResponseInfo;
import cn.iwgang.elastichttp.core.HttpLibrary;
import cn.iwgang.elastichttp.core.RequestTimeOut;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * HttpLibrary OkHttp
 * Created by iWgang on 16/1/26.
 */
public class OkHttpLibrary implements HttpLibrary {
    private OkHttpClient mOkHttpClient;
    private int defConnectTimeout;
    private int defWriteTimeout;
    private int defReadTimeout;

    @Override
    public ElasticCall asyncExecute(ElasticRequest request, ElasticRequestInfo requestInfo, ElasticHttp.ElasticCallbackWrap responseCallbackWrap) {
        Call call = generateCall(request, requestInfo);
        call.enqueue(new CustomCallback(requestInfo, responseCallbackWrap));

        return new OkHttpCall(call);
    }

    @Override
    public ElasticResponse syncExecute(ElasticRequest request, ElasticRequestInfo requestInfo) throws IOException {
        return new OkHttpResponse(generateCall(request, requestInfo).execute(), requestInfo);
    }

    private Call generateCall(ElasticRequest request, ElasticRequestInfo requestInfo) {
        if (null == mOkHttpClient) throw new RuntimeException("mOkHttpClient not initialize");

        Request okHttpRequest = OkHttpRequestHelper.generateRequest(request, requestInfo);

        // 执行请求
        Call call;
        RequestTimeOut customRequestTimeOut = request.getRequestTimeOut();
        // 处理自定义超时的场景
        if (null != customRequestTimeOut) {
            OkHttpClient.Builder newBuilder = mOkHttpClient.newBuilder();

            long connectTimeout = customRequestTimeOut.getConnectTimeout();
            newBuilder.connectTimeout(connectTimeout > 0 ? connectTimeout : defConnectTimeout, TimeUnit.SECONDS);

            long writeTimeout = customRequestTimeOut.getWriteTimeout();
            newBuilder.writeTimeout(writeTimeout > 0 ? writeTimeout : defWriteTimeout, TimeUnit.SECONDS);

            long readTimeout = customRequestTimeOut.getReadTimeout();
            newBuilder.readTimeout(readTimeout > 0 ? readTimeout : defReadTimeout, TimeUnit.SECONDS);

            call = newBuilder.build().newCall(okHttpRequest);
        } else {
            call = mOkHttpClient.newCall(okHttpRequest);
        }

        return call;
    }

    @Override
    public void initialize(RequestTimeOut defRequestTimeOut, boolean isHaveUseSyncRequest, String cacheDir, long cachedMaxSize) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

//        if (!TextUtils.isEmpty(cacheDir)) {
//            File cacheFile = new File(cacheDir);
//            if (!cacheFile.exists()) cacheFile.mkdir();
//
//            okHttpClientBuilder.cache(new Cache(cacheFile, cachedMaxSize));
//        }

        defConnectTimeout = defRequestTimeOut.getConnectTimeout();
        defWriteTimeout = defRequestTimeOut.getWriteTimeout();
        defReadTimeout = defRequestTimeOut.getReadTimeout();

        mOkHttpClient = okHttpClientBuilder
                .connectTimeout(defConnectTimeout, TimeUnit.SECONDS)
                .writeTimeout(defConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(defReadTimeout, TimeUnit.SECONDS)
                .build();
    }


    static class CustomCallback implements Callback {
        private ElasticRequestInfo requestInfo;
        private ElasticCallback responseCallback;

        public CustomCallback(ElasticRequestInfo requestInfo, ElasticCallback responseCallback) {
            this.requestInfo = requestInfo;
            this.responseCallback = responseCallback;
        }

        @Override
        public void onFailure(Call call, IOException e) {
            responseCallback.handlerFailure(requestInfo, e);
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            OkHttpResponse okHttpResponse = new OkHttpResponse(response, requestInfo);
            responseCallback.handlerSuccess(new ElasticResponseInfo(requestInfo, response.code(), okHttpResponse.getResponseHeader(), false), okHttpResponse);
        }
    }

}
