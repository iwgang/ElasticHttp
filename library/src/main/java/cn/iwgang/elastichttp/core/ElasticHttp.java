package cn.iwgang.elastichttp.core;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.iwgang.elastichttp.callback.ElasticCallback;
import cn.iwgang.elastichttp.constant.RequestMethod;
import cn.iwgang.elastichttp.interceptor.ElasticInterceptor;
import cn.iwgang.elastichttp.request.PostRequest;
import cn.iwgang.elastichttp.util.ElasticUtil;

/**
 * Created by iWgang on 16/1/15.
 */
public final class ElasticHttp {
    private static ElasticHttp mInstance;
    private ElasticHttpConfiguration mConfiguration;
    private Handler mMainThreadHandler;
    private boolean isInitialize = false;
    private List<ElasticInterceptor> mElasticInterceptor;

    public ElasticHttp() {
        mMainThreadHandler = new Handler(Looper.getMainLooper());
    }

    public static ElasticHttp getInstance() {
        if (mInstance == null) {
            synchronized (ElasticHttp.class) {
                if (mInstance == null) {
                    mInstance = new ElasticHttp();
                }
            }
        }
        return mInstance;
    }

    public void initialize(ElasticHttpConfiguration configuration) {
        synchronized (this) {
            if (null == configuration) throw new IllegalArgumentException("configure is null");

            if (isInitialize) throw new IllegalArgumentException("repeat initialize");

            mConfiguration = configuration;
            mElasticInterceptor = mConfiguration.elasticInterceptor;
            isInitialize = true;
        }
    }

    /**
     * 执行异步请求
     * @param request            Request
     * @param responseCallback   Response Callback
     * @return RequestAnchor
     */
    public ElasticCall asyncExecute(ElasticRequest request, ElasticCallback responseCallback) {
        if (!isInitialize) throw new RuntimeException("not call initialize()");

        if (null != mElasticInterceptor) {
            for (ElasticInterceptor interceptor : mElasticInterceptor) {
                interceptor.onRequest(request);
            }
        }

        String reqUrl = request.getUrl();
        int reqMethod = request.getMethod();
        int reqHttpLibraryKey = request.getHttpLibraryKey();

        // 保存请求信息
        ElasticRequestInfo elasticRequestInfo = new ElasticRequestInfo(reqUrl, reqMethod, request.getHeaderMap());
        elasticRequestInfo.setTag(request.getTag());
        switch (reqMethod) {
            case RequestMethod.REQUEST_METHOD_POST:
                if (request instanceof PostRequest) {
                    PostRequest postRequest = (PostRequest) request;

                    String bodyStr = postRequest.getBodyStr();
                    Map<String, String> requestParamMap = postRequest.getRequestParamMap();

                    if (TextUtils.isEmpty(bodyStr)) {
                        elasticRequestInfo.setBody(bodyStr);
                    } else if (null != requestParamMap && !requestParamMap.isEmpty()) {
                        elasticRequestInfo.setBody(ElasticUtil.mapToStr(requestParamMap));
                    }
                }
                break;
        }

        // 执行请求
        return mConfiguration.getHttpLibrary(reqHttpLibraryKey).asyncExecute(request, elasticRequestInfo, new ElasticCallbackWrap(responseCallback, mElasticInterceptor));
    }

    /**
     * 执行同步请求 (非UI线程调用)
     * @param request            Request
     * @return ElasticResponse
     */
    public ElasticResponse syncExecute(ElasticRequest request) throws IOException {
        if (!isInitialize) throw new RuntimeException("not call initialize()");

        if (null != mElasticInterceptor) {
            for (ElasticInterceptor interceptor : mElasticInterceptor) {
                interceptor.onRequest(request);
            }
        }

        String reqUrl = request.getUrl();
        int reqMethod = request.getMethod();
        int reqHttpLibraryKey = request.getHttpLibraryKey();

        // 保存请求信息
        ElasticRequestInfo elasticRequestInfo = new ElasticRequestInfo(reqUrl, reqMethod, request.getHeaderMap());
        elasticRequestInfo.setTag(request.getTag());
        switch (reqMethod) {
            case RequestMethod.REQUEST_METHOD_POST:
                if (request instanceof PostRequest) {
                    PostRequest postRequest = (PostRequest) request;

                    String bodyStr = postRequest.getBodyStr();
                    Map<String, String> requestParamMap = postRequest.getRequestParamMap();

                    if (TextUtils.isEmpty(bodyStr)) {
                        elasticRequestInfo.setBody(bodyStr);
                    } else if (null != requestParamMap && !requestParamMap.isEmpty()) {
                        elasticRequestInfo.setBody(ElasticUtil.mapToStr(requestParamMap));
                    }
                }
                break;
        }

        // 执行请求
        return mConfiguration.getHttpLibrary(reqHttpLibraryKey).syncExecute(request, elasticRequestInfo);
    }

    public void runMain(Runnable run) {
        mMainThreadHandler.post(run);
    }


    public static class ElasticCallbackWrap extends ElasticCallback {
        private ElasticCallback responseCallback;
        private List<ElasticInterceptor> elasticInterceptor;

        public ElasticCallbackWrap(ElasticCallback responseCallback, List<ElasticInterceptor> elasticInterceptor) {
            this.responseCallback = responseCallback;
            this.elasticInterceptor = elasticInterceptor;
        }

        @Override
        public void handlerSuccess(ElasticResponseInfo responseInfo, ElasticResponse elasticResponse) {
            if (null != elasticInterceptor) {
                for (ElasticInterceptor interceptor : elasticInterceptor) {
                    interceptor.onResponse(true, elasticResponse, null);
                }
            }

            responseCallback.handlerSuccess(responseInfo, elasticResponse);
        }

        @Override
        public void handlerFailure(ElasticRequestInfo requestInfo, Exception e) {
            if (null != elasticInterceptor) {
                for (ElasticInterceptor interceptor : elasticInterceptor) {
                    interceptor.onResponse(false, null, e);
                }
            }

            responseCallback.handlerFailure(requestInfo, e);
        }
    }

}
