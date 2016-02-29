package cn.iwgang.elasticokhttp;

import android.text.TextUtils;

import java.util.List;
import java.util.Map;

import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticRequest;
import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.OnUploadProgressListener;
import cn.iwgang.elastichttp.request.DeleteRequest;
import cn.iwgang.elastichttp.request.GetRequest;
import cn.iwgang.elastichttp.request.HeadRequest;
import cn.iwgang.elastichttp.request.MultiFileUploadRequest;
import cn.iwgang.elastichttp.request.PostRequest;
import cn.iwgang.elastichttp.request.PutRequest;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * android-async-http 请求生成辅助类
 * Created by iWgang on 16/1/26.
 */
public final class OkHttpRequestHelper {

    /**
     * 生成请求
     *
     * @param request ElasticRequest
     * @return OkHttp Request
     */
    public static Request generateRequest(ElasticRequest request, ElasticRequestInfo requestInfo) {
        if (request instanceof GetRequest) {
            return generateGetOrDeleteOrHeadRequest(request, 1);
        } else if (request instanceof PostRequest) {
            return generatePostTextRequest((PostRequest) request);
        } else if (request instanceof PutRequest) {
            return generatePutTextRequest((PutRequest) request);
        } else if (request instanceof MultiFileUploadRequest) {
            return generateMultiFileUploadRequest((MultiFileUploadRequest) request, requestInfo);
        } else if (request instanceof DeleteRequest) {
            return generateGetOrDeleteOrHeadRequest(request, 2);
        } else if (request instanceof HeadRequest) {
            return generateGetOrDeleteOrHeadRequest(request, 3);
        }

        throw new RuntimeException("unknown ElasticRequest");
    }

    /**
     * 生成GET或者DELETE请求
     *
     * @param request GetRequest or DeleteRequest or HeadRequest
     * @param reqType 1: GET, 2: DELETE. 3: HEAD
     * @return OkHttp Request
     */
    private static Request generateGetOrDeleteOrHeadRequest(ElasticRequest request, int reqType) {
        Request.Builder builder = new Request.Builder();
        Map<String, String> requestParamMap;
        if (reqType == 2) {
            // DELETE
            requestParamMap = ((DeleteRequest) request).getRequestParamMap();
            builder.delete();
        } else if (reqType == 3) {
            // HEAD
            requestParamMap = ((HeadRequest) request).getRequestParamMap();
            builder.head();
        } else {
            // GET
            requestParamMap = ((GetRequest) request).getRequestParamMap();
            builder.get();
        }

        String url = request.getUrl();

        if (null != requestParamMap && !requestParamMap.isEmpty()) {
            HttpUrl.Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
            for (Map.Entry<String, String> param : requestParamMap.entrySet()) {
                httpUrlBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
            builder.url(httpUrlBuilder.build());
        } else {
            builder.url(url);
        }
        addHeader(builder, request.getHeaderMap());
        return builder.build();
    }

    /**
     * 生成POST文本请求
     *
     * @param request PostRequest
     * @return OkHttp Request
     */
    private static Request generatePostTextRequest(PostRequest request) {
        String bodyStr = request.getBodyStr();
        Map<String, String> requestParamMap = request.getRequestParamMap();
        boolean isBodyStrEmpty = TextUtils.isEmpty(bodyStr);

        if (isBodyStrEmpty && (null == requestParamMap || requestParamMap.isEmpty())) {
            throw new IllegalArgumentException("bodyStr and requestParam empty");
        }

        if (!isBodyStrEmpty) {
            Request.Builder builder = new Request.Builder().post(RequestBody.create(MediaType.parse(request.getMediaType()), request.getBodyStr())).url(request.getUrl());
            addHeader(builder, request.getHeaderMap());
            return builder.build();
        } else {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();

            for (Map.Entry<String, String> rp : requestParamMap.entrySet()) {
                formBodyBuilder.add(rp.getKey(), rp.getValue());
            }

            Request.Builder builder = new Request.Builder().url(request.getUrl()).post(formBodyBuilder.build());
            addHeader(builder, request.getHeaderMap());
            return builder.build();
        }
    }

    /**
     * 生成PUT文本请求
     *
     * @param request PutRequest
     * @return OkHttp Request
     */
    private static Request generatePutTextRequest(PutRequest request) {
        String bodyStr = request.getBodyStr();
        Map<String, String> requestParamMap = request.getRequestParamMap();
        boolean isBodyStrEmpty = TextUtils.isEmpty(bodyStr);

        if (isBodyStrEmpty && (null == requestParamMap || requestParamMap.isEmpty())) {
            throw new IllegalArgumentException("bodyStr and requestParam empty");
        }

        if (!isBodyStrEmpty) {
            Request.Builder builder = new Request.Builder().put(RequestBody.create(MediaType.parse(request.getMediaType()), request.getBodyStr())).url(request.getUrl());
            addHeader(builder, request.getHeaderMap());
            return builder.build();
        } else {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();

            for (Map.Entry<String, String> rp : requestParamMap.entrySet()) {
                formBodyBuilder.add(rp.getKey(), rp.getValue());
            }

            Request.Builder builder = new Request.Builder().url(request.getUrl()).put(formBodyBuilder.build());
            addHeader(builder, request.getHeaderMap());
            return builder.build();
        }
    }

    /**
     * 生成多文件上传请求
     *
     * @param request DownloadRequest
     * @return OkHttp Request
     */
    private static Request generateMultiFileUploadRequest(MultiFileUploadRequest request, final ElasticRequestInfo requestInfo) {
        List<MultiFileUploadRequest.BasePartInfo> partList = request.getPartList();

        if (null == partList || partList.isEmpty()) {
            throw new IllegalArgumentException("part empty");
        }

        Map<String, String> stringPartMap = request.getStringPartMap();
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        if (null != stringPartMap && !stringPartMap.isEmpty()) {
            for (Map.Entry<String, String> stringPart : stringPartMap.entrySet()) {
                multipartBodyBuilder.addFormDataPart(stringPart.getKey(), stringPart.getValue());
            }
        }

        for (MultiFileUploadRequest.BasePartInfo partInfo : partList) {
            if (partInfo instanceof MultiFileUploadRequest.FilePartInfo) {
                MultiFileUploadRequest.FilePartInfo filePartInfo = (MultiFileUploadRequest.FilePartInfo) partInfo;
                multipartBodyBuilder.addFormDataPart(filePartInfo.getName(), filePartInfo.getFilename(), RequestBody.create(MediaType.parse(filePartInfo.getMediaType()), filePartInfo.getFile()));
            } else if (partInfo instanceof MultiFileUploadRequest.BytePartInfo) {
                MultiFileUploadRequest.BytePartInfo bytePartInfo = (MultiFileUploadRequest.BytePartInfo) partInfo;
                int offset = bytePartInfo.getOffset();
                int byteCount = bytePartInfo.getByteCount();
                if (offset == 0) {
                    multipartBodyBuilder.addFormDataPart(bytePartInfo.getName(), bytePartInfo.getFilename(), RequestBody.create(MediaType.parse(bytePartInfo.getMediaType()), bytePartInfo.getContent()));
                } else {
                    multipartBodyBuilder.addFormDataPart(bytePartInfo.getName(), bytePartInfo.getFilename(), RequestBody.create(MediaType.parse(bytePartInfo.getMediaType()), bytePartInfo.getContent(), offset, byteCount));
                }
            }
        }

        Request.Builder builder;

        final OnUploadProgressListener onUploadProgressListener = request.getOnUploadProgressListener();
        if (null != onUploadProgressListener) {
            RequestBody requestBody = new CountingRequestBody(multipartBodyBuilder.build(), new CountingRequestBody.Listener() {
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
            builder = new Request.Builder().url(request.getUrl()).post(requestBody);
        } else {
            builder = new Request.Builder().url(request.getUrl()).post(multipartBodyBuilder.build());
        }

        addHeader(builder, request.getHeaderMap());
        return builder.build();
    }


    /**
     * 统一添加Header
     *
     * @param builder   Request.Builder
     * @param headerMap header map
     */
    private static void addHeader(Request.Builder builder, Map<String, String> headerMap) {
        if (null != headerMap && !headerMap.isEmpty()) {
            for (Map.Entry<String, String> head : headerMap.entrySet()) {
                builder.addHeader(head.getKey(), head.getValue());
            }
        }
    }

}
