package cn.iwgang.elasticasynchttp;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponse;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.util.ByteArrayBuffer;

/**
 * android-async-http HttpLibrary响应实现
 * Created by iWgang on 16/1/28.
 */
public class AsyncHttpResponse implements ElasticResponse {
    private static final int BUFFER_SIZE = 4096;

    private HttpResponse mResponse;
    private ElasticRequestInfo mElasticRequestInfo;
    private int mStatusCode;

    public AsyncHttpResponse(int statusCode, ElasticRequestInfo elasticRequestInfo, HttpResponse response) {
        this.mStatusCode = statusCode;
        this.mElasticRequestInfo = elasticRequestInfo;
        this.mResponse = response;
    }

    @Override
    public InputStream getBodyIs() throws IOException {
        return mResponse.getEntity().getContent();
    }

    @Override
    public String getBodyStr() throws IOException {
        byte[] responseBody = getResponseData(mResponse.getEntity());

        if (null == responseBody || responseBody.length == 0) throw new IOException("response body is empty");

        return TextHttpResponseHandler.getResponseString(responseBody, TextHttpResponseHandler.DEFAULT_CHARSET);
    }

    @Override
    public int getHttpCode() {
        return mStatusCode;
    }

    @Override
    public long getContentLength() {
        return mResponse.getEntity().getContentLength();
    }

    @Override
    public String getEncodedPath() {
        return mElasticRequestInfo.getUrl();
    }

    @Override
    public boolean isSuccessful() {
        return mStatusCode >= 200 && mStatusCode < 300;
    }

    @Override
    public Map<String, String> getResponseHeader() {
        Map<String, String> rspHeaderMap = null;
        Header[] rspHeaders = mResponse.getAllHeaders();
        if (null != rspHeaders && rspHeaders.length > 0) {
            rspHeaderMap = new HashMap<>();
            for (Header h : rspHeaders) {
                rspHeaderMap.put(h.getName(), h.getValue());
            }
        }

        return rspHeaderMap;
    }

    @Override
    public ElasticRequestInfo getRequestInfo() {
        return mElasticRequestInfo;
    }

    private byte[] getResponseData(HttpEntity entity) throws IOException {
        byte[] responseBody = null;
        if (entity != null) {
            InputStream instream = entity.getContent();
            if (instream != null) {
                long contentLength = entity.getContentLength();
                if (contentLength > Integer.MAX_VALUE) {
                    throw new IllegalArgumentException("HTTP entity too large to be buffered in memory");
                }
                int bufferSize = (contentLength <= 0) ? BUFFER_SIZE : (int) contentLength;
                try {
                    ByteArrayBuffer buffer = new ByteArrayBuffer(bufferSize);
                    try {
                        byte[] tmp = new byte[BUFFER_SIZE];
                        int l;
                        // do not send messages if request has been cancelled
                        while ((l = instream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                            buffer.append(tmp, 0, l);
                        }
                    } finally {
                        AsyncHttpClient.silentCloseInputStream(instream);
                        AsyncHttpClient.endEntityViaReflection(entity);
                    }
                    responseBody = buffer.toByteArray();
                } catch (OutOfMemoryError e) {
                    System.gc();
                    throw new IOException("File too large to fit into available memory");
                }
            }
        }
        return responseBody;
    }

}
