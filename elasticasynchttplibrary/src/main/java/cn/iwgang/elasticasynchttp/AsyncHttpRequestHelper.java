package cn.iwgang.elasticasynchttp;

import android.text.TextUtils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import cn.iwgang.elastichttp.callback.ElasticCallback;
import cn.iwgang.elastichttp.constant.RequestMediaType;
import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticRequest;
import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponseInfo;
import cn.iwgang.elastichttp.core.OnUploadProgressListener;
import cn.iwgang.elastichttp.request.DeleteRequest;
import cn.iwgang.elastichttp.request.GetRequest;
import cn.iwgang.elastichttp.request.MultiFileUploadRequest;
import cn.iwgang.elastichttp.request.PostRequest;
import cn.iwgang.elastichttp.request.PutRequest;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.message.BasicHeader;

/**
 * android-async-http请求辅助类
 * Created by iWgang on 16/1/26.
 */
public final class AsyncHttpRequestHelper {

    /**
     * 处理异步请求
     *
     * @param request              ElasticRequest
     * @param requestInfo          ElasticRequestInfo
     * @param asyncHttpClient      AsyncHttpClient
     * @param responseCallbackWrap ElasticHttp.ElasticCallbackWrap
     * @return RequestHandle
     */
    public static RequestHandle processAsyncExecute(ElasticRequest request, ElasticRequestInfo requestInfo, AsyncHttpClient asyncHttpClient, ElasticHttp.ElasticCallbackWrap responseCallbackWrap) {
        return processExecute(request, requestInfo, asyncHttpClient, new CustomAsyncHttpResponseHandler(requestInfo, responseCallbackWrap));
    }

    /**
     * 处理同步请求
     *
     * @param request                        ElasticRequest
     * @param requestInfo                    ElasticRequestInfo
     * @param syncHttpClient                 SyncHttpClient
     * @param customResponseHandlerInterface CustomResponseHandlerInterface
     * @return RequestHandle
     */
    public static RequestHandle processSyncExecute(ElasticRequest request, ElasticRequestInfo requestInfo, SyncHttpClient syncHttpClient, CustomResponseHandlerInterface customResponseHandlerInterface) {
        return processExecute(request, requestInfo, syncHttpClient, customResponseHandlerInterface);
    }

    private static RequestHandle processExecute(ElasticRequest request, ElasticRequestInfo requestInfo, AsyncHttpClient asyncHttpClient, CustomResponseHandlerInterface customResponseHandlerInterface) {
        Header[] headers = convertHeaders(request.getHeaderMap());

        if (request instanceof GetRequest) {
            return executeGetOrDelete(asyncHttpClient, headers, customResponseHandlerInterface, request, true);
        } else if (request instanceof PostRequest) {
            return executePost(asyncHttpClient, headers, customResponseHandlerInterface, (PostRequest) request);
        } else if (request instanceof PutRequest) {
            return executePut(asyncHttpClient, headers, customResponseHandlerInterface, (PutRequest) request);
        } else if (request instanceof MultiFileUploadRequest) {
            return executeMultiFileUpload(asyncHttpClient, headers, customResponseHandlerInterface, (MultiFileUploadRequest) request, requestInfo);
        } else if (request instanceof DeleteRequest) {
            return executeGetOrDelete(asyncHttpClient, headers, customResponseHandlerInterface, request, false);
        }

        throw new RuntimeException("unknown ElasticRequest");
    }

    /**
     * 执行GET或DELETE请求
     *
     * @param asyncHttpClient                AsyncHttpClient
     * @param headers                        Header[]
     * @param customResponseHandlerInterface CustomResponseHandlerInterface
     * @param request                        GetRequest or DeleteRequest
     * @param isGetRequest                   true:GET false:DELETE
     * @return RequestHandle
     */
    private static RequestHandle executeGetOrDelete(AsyncHttpClient asyncHttpClient, Header[] headers, CustomResponseHandlerInterface customResponseHandlerInterface, ElasticRequest request, boolean isGetRequest) {
        RequestHandle retRequestHandle;
        if (isGetRequest) {
            // GET
            Map<String, String> requestParamMap = ((GetRequest) request).getRequestParamMap();
            retRequestHandle = asyncHttpClient.get(null, request.getUrl(), headers, convertRequestParams(requestParamMap), customResponseHandlerInterface);
        } else {
            // DELETE
            Map<String, String> requestParamMap = ((DeleteRequest) request).getRequestParamMap();
            retRequestHandle = asyncHttpClient.delete(null, request.getUrl(), headers, convertRequestParams(requestParamMap), customResponseHandlerInterface);
        }

        return retRequestHandle;
    }

    /**
     * 执行POST请求
     *
     * @param asyncHttpClient                AsyncHttpClient
     * @param headers                        Header[]
     * @param customResponseHandlerInterface CustomResponseHandlerInterface
     * @param request                        PostRequest
     * @return RequestHandle
     */
    private static RequestHandle executePost(AsyncHttpClient asyncHttpClient, Header[] headers, CustomResponseHandlerInterface customResponseHandlerInterface, PostRequest request) {
        String bodyStr = request.getBodyStr();
        Map<String, String> requestParamMap = request.getRequestParamMap();
        boolean isBodyStrEmpty = TextUtils.isEmpty(bodyStr);

        if (isBodyStrEmpty && (null == requestParamMap || requestParamMap.isEmpty())) {
            throw new IllegalArgumentException("bodyStr and requestParam empty");
        }

        if (!isBodyStrEmpty) {
            StringEntity bodyEntity = new StringEntity(request.getBodyStr(), "utf-8");
            return asyncHttpClient.post(null, request.getUrl(), headers, bodyEntity, request.getMediaType(), customResponseHandlerInterface);
        } else {
            return asyncHttpClient.post(null, request.getUrl(), headers, new RequestParams(requestParamMap), RequestMediaType.MEDIA_TYPE_FORM, customResponseHandlerInterface);
        }
    }

    /**
     * 执行PUT请求
     *
     * @param asyncHttpClient                AsyncHttpClient
     * @param headers                        Header[]
     * @param customResponseHandlerInterface CustomResponseHandlerInterface
     * @param request                        PutRequest
     * @return RequestHandle
     */
    private static RequestHandle executePut(AsyncHttpClient asyncHttpClient, Header[] headers, CustomResponseHandlerInterface customResponseHandlerInterface, PutRequest request) {
        String bodyStr = request.getBodyStr();
        Map<String, String> requestParamMap = request.getRequestParamMap();
        boolean isBodyStrEmpty = TextUtils.isEmpty(bodyStr);

        if (isBodyStrEmpty && (null == requestParamMap || requestParamMap.isEmpty())) throw new IllegalArgumentException("bodyStr and requestParam empty");

        if (!isBodyStrEmpty) {
            StringEntity bodyEntity = new StringEntity(request.getBodyStr(), "utf-8");
            return asyncHttpClient.put(null, request.getUrl(), headers, bodyEntity, request.getMediaType(), customResponseHandlerInterface);
        } else {
            RequestParams requestParams = new RequestParams(requestParamMap);
            HttpEntity entity = null;
            try {
                entity = requestParams.getEntity(customResponseHandlerInterface);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != entity) {
                return asyncHttpClient.put(null, request.getUrl(), headers, entity, RequestMediaType.MEDIA_TYPE_FORM, customResponseHandlerInterface);
            } else {
                return asyncHttpClient.put(null, request.getUrl(), requestParams, customResponseHandlerInterface);
            }
        }
    }

    /**
     * 执行多文件上传请求
     *
     * @param asyncHttpClient                AsyncHttpClient
     * @param headers                        Header[]
     * @param customResponseHandlerInterface CustomResponseHandlerInterface
     * @param request                        DownloadRequest
     * @param requestInfo                    ElasticRequestInfo
     * @return RequestHandle
     */
    private static RequestHandle executeMultiFileUpload(AsyncHttpClient asyncHttpClient, Header[] headers, CustomResponseHandlerInterface customResponseHandlerInterface, MultiFileUploadRequest request, final ElasticRequestInfo requestInfo) {
        List<MultiFileUploadRequest.BasePartInfo> partInfoList = request.getPartList();

        if (null == partInfoList || partInfoList.isEmpty())
            throw new IllegalArgumentException("part is empty");

        RequestParams params = new RequestParams();
        params.setAutoCloseInputStreams(true);

        try {
            for (MultiFileUploadRequest.BasePartInfo partInfo : partInfoList) {
                if (partInfo instanceof MultiFileUploadRequest.FilePartInfo) {
                    MultiFileUploadRequest.FilePartInfo filePartInfo = (MultiFileUploadRequest.FilePartInfo) partInfo;
                    params.put(filePartInfo.getName(), filePartInfo.getFile(), filePartInfo.getMediaType(), filePartInfo.getFilename());
                } else if (partInfo instanceof MultiFileUploadRequest.BytePartInfo) {
                    MultiFileUploadRequest.BytePartInfo bytePartInfo = (MultiFileUploadRequest.BytePartInfo) partInfo;
                    params.put(bytePartInfo.getName(), new ByteArrayInputStream(bytePartInfo.getContent()), bytePartInfo.getFilename());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        final OnUploadProgressListener onUploadProgressListener = request.getOnUploadProgressListener();
        if (null != onUploadProgressListener) {
            customResponseHandlerInterface.setOnRequestListener(new CustomResponseHandlerInterface.OnRequestListener() {
                @Override
                public void onRequestProgress(final long bytesWritten, final long contentLength) {
                    ElasticHttp.getInstance().runMain(new Runnable() {
                        @Override
                        public void run() {
                            onUploadProgressListener.onProgressUpdate(requestInfo, bytesWritten * 100f / contentLength);
                        }
                    });
                }
            });
        }

        return asyncHttpClient.post(null, request.getUrl(), headers, params, null, customResponseHandlerInterface);
    }

    private static RequestParams convertRequestParams(Map<String, String> requestParamMap) {
        if (null == requestParamMap || requestParamMap.isEmpty()) return null;
        return new RequestParams(requestParamMap);
    }

    private static Header[] convertHeaders(Map<String, String> headerMap) {
        if (null == headerMap || headerMap.isEmpty()) return null;

        Header[] ahHeader = new Header[headerMap.size()];
        int idx = 0;
        for (Map.Entry<String, String> h : headerMap.entrySet()) {
            ahHeader[idx] = new BasicHeader(h.getKey(), h.getValue());
            idx++;
        }
        return ahHeader;
    }


    static class CustomAsyncHttpResponseHandler extends CustomResponseHandlerInterface {
        private ElasticRequestInfo requestInfo;
        private ElasticCallback responseCallback;

        public CustomAsyncHttpResponseHandler(ElasticRequestInfo requestInfo, ElasticCallback responseCallback) {
            this.requestInfo = requestInfo;
            this.responseCallback = responseCallback;
        }

        @Override
        public void onFailure(int statusCode, IOException e) {
            responseCallback.handlerFailure(requestInfo, e);
        }

        @Override
        public void onResponse(int statusCode, HttpResponse response) throws IOException {
            AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse(statusCode, requestInfo, response);
            responseCallback.handlerSuccess(new ElasticResponseInfo(requestInfo, statusCode, asyncHttpResponse.getResponseHeader(), false), asyncHttpResponse);
        }

    }

}
