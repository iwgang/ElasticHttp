package cn.iwgang.elastichttp.core;

import java.io.IOException;

/**
 * Http请求框架方法定义
 * Created by iWgang on 16/1/26.
 */
public interface HttpLibrary {

    /**
     * 初始化
     *
     * @param defRequestTimeOut    默认请求超时信息Bean
     * @param isHaveUseSyncRequest 是否有使用到同步请求 (避免浪费资源进行初始化工作, 目前仅针对android-async-http设置)
     * @param cacheDir             缓存目录
     * @param cachedMaxSize        最大缓存大小
     */
    void initialize(RequestTimeOut defRequestTimeOut, boolean isHaveUseSyncRequest, String cacheDir, long cachedMaxSize);

    /**
     * 异步请求
     *
     * @param request             ElasticRequest
     * @param requestInfo         ElasticRequestInfo
     * @param ElasticCallbackWrap ElasticHttp.ElasticCallbackWrap
     * @return ElasticCall
     */
    ElasticCall asyncExecute(ElasticRequest request, ElasticRequestInfo requestInfo, ElasticHttp.ElasticCallbackWrap ElasticCallbackWrap);

    /**
     * 同步请求
     *
     * @param request     ElasticRequest
     * @param requestInfo ElasticRequestInfo
     * @return ElasticResponse
     */
    ElasticResponse syncExecute(ElasticRequest request, ElasticRequestInfo requestInfo) throws IOException;

}
