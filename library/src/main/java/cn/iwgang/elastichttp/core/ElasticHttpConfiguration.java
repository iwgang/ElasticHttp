package cn.iwgang.elastichttp.core;

import android.text.TextUtils;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import cn.iwgang.elastichttp.interceptor.ElasticInterceptor;

public class ElasticHttpConfiguration {
    public static final int DEF_HTTP_LIBRARY_KEY = 120001;

    private boolean isMultipleHttpLibrary;
    private HttpLibrary singleHttpLibrary;
    private SparseArray<HttpLibrary> httpLibraryList;
    final List<ElasticInterceptor> elasticInterceptor;
    final RequestTimeOut requestTimeOut;

    private ElasticHttpConfiguration(final Builder builder) {
        if (builder.httpLibraryList.size() == 1) {
            // 单个HttpLibrary
            singleHttpLibrary = builder.httpLibraryList.valueAt(0);
            httpLibraryList = null;
            isMultipleHttpLibrary = false;
        } else {
            // 多个HttpLibrary
            singleHttpLibrary = null;
            httpLibraryList = builder.httpLibraryList;
            isMultipleHttpLibrary = true;
        }

        elasticInterceptor = builder.elasticInterceptor;
        requestTimeOut = new RequestTimeOut(builder.timeoutConn, builder.timeoutRead, builder.timeoutWrite);

        initializeHttpLibrary(builder.cacheDir, builder.cachedMaxSize, builder.isHaveUseSyncRequest);
    }

    public HttpLibrary getHttpLibrary(int httpLibraryKey) {
        if (!isMultipleHttpLibrary) {
            return singleHttpLibrary;
        } else {
            HttpLibrary retHttpLibrary = httpLibraryList.get(httpLibraryKey);

            if (null == retHttpLibrary)
                throw new IllegalArgumentException("httpLibraryKey invalid");

            return retHttpLibrary;
        }
    }

    public List<ElasticInterceptor> getInterceptor() {
        return elasticInterceptor;
    }

    private void initializeHttpLibrary(String cacheDir, long cachedMaxSize, boolean isHaveUseSyncRequest) {
        if (!isMultipleHttpLibrary) {
            singleHttpLibrary.initialize(requestTimeOut, isHaveUseSyncRequest, cacheDir, cachedMaxSize);
        } else {
            for (int i = 0; i < httpLibraryList.size(); i++) {
                httpLibraryList.valueAt(i).initialize(requestTimeOut, isHaveUseSyncRequest, cacheDir, cachedMaxSize);
            }
        }
    }


    public static class Builder {
        /** 默认连接超时时间(单位秒) */
        private static final int DEF_TIMEOUT_CONN = 10;
        /** 默认读取超时时间(单位秒) */
        private static final int DEF_TIMEOUT_WRITE = 10;
        /** 默认读取超时时间(单位秒) */
        private static final int DEF_TIMEOUT_READ = 10;
        /** 默认缓存最大容量 */
        private static final int DEF_CACHE_MAX_SIZE = 5 * 1024 *1024;

        private SparseArray<HttpLibrary> httpLibraryList;
        private List<ElasticInterceptor> elasticInterceptor;
        private boolean isHaveUseSyncRequest = false;
        private String cacheDir = null;
        private long cachedMaxSize;
        private int timeoutConn; // 连接超时时间(单位秒)
        private int timeoutWrite; // 读取超时时间(单位秒)
        private int timeoutRead; // 读取超时时间(单位秒)

        public Builder setHttpLibrary(HttpLibrary httpLibrary) {
            if (null == httpLibraryList) {
                httpLibraryList = new SparseArray<>();
            }

            this.httpLibraryList.append(DEF_HTTP_LIBRARY_KEY, httpLibrary);
            return this;
        }

        public Builder addHttpLibrary(int httpLibraryKey, HttpLibrary httpLibrary) {
            if (null == httpLibraryList) {
                httpLibraryList = new SparseArray<>();
            }

            this.httpLibraryList.append(httpLibraryKey, httpLibrary);
            return this;
        }

        public Builder setTimeoutConn(int timeoutConn) {
            this.timeoutConn = timeoutConn;
            return this;
        }

        public Builder setTimeoutRead(int timeoutRead) {
            this.timeoutRead = timeoutRead;
            return this;
        }

        public Builder setTimeoutWrite(int timeoutWrite) {
            this.timeoutWrite = timeoutWrite;
            return this;
        }

        public Builder addInterceptor(ElasticInterceptor interceptor) {
            if (null == elasticInterceptor) {
                elasticInterceptor = new ArrayList<>();
            }
            elasticInterceptor.add(interceptor);
            return this;
        }

        public Builder setCacheDir(String cacheDir) {
            this.cacheDir = cacheDir;
            return this;
        }

        public Builder setCachedMaxSize(long cachedMaxSize) {
            this.cachedMaxSize = cachedMaxSize;
            return this;
        }

        /**
         * 有使用到同步请求
         * (如果没有使用同步请求, 请别设置此方法, 避免浪费资源进行初始化工作, 目前仅针对android-async-http设置)
         *
         * @return
         */
        public Builder haveUseSyncRequest() {
            this.isHaveUseSyncRequest = true;
            return this;
        }

        private void checkData() {
            if (null == httpLibraryList) throw new IllegalArgumentException("httpLibrary is null");

            if (timeoutConn <= 0) timeoutConn = DEF_TIMEOUT_CONN;

            if (timeoutWrite <= 0) timeoutConn = DEF_TIMEOUT_WRITE;

            if (timeoutRead <= 0) timeoutConn = DEF_TIMEOUT_READ;

            if (!TextUtils.isEmpty(cacheDir) && cachedMaxSize <= 0) cachedMaxSize = DEF_CACHE_MAX_SIZE;
        }

        public ElasticHttpConfiguration builder() {
            checkData();
            return new ElasticHttpConfiguration(this);
        }

    }

}