package cn.iwgang.elasticasynchttp;

import com.loopj.android.http.ResponseHandlerInterface;

import java.io.IOException;
import java.net.URI;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.StatusLine;

/**
 * Created by iWgang on 16/1/28.
 */
public abstract class CustomResponseHandlerInterface implements ResponseHandlerInterface {
    private OnRequestListener mOnRequestListener;
    private Header[] mRequestHeaders = null;
    private URI requestURI;

    @Override
    public Header[] getRequestHeaders() {
        return mRequestHeaders;
    }

    @Override
    public void sendResponseMessage(HttpResponse response) throws IOException {
        // do not process if request has been cancelled
        if (!Thread.currentThread().isInterrupted()) {
            StatusLine status = response.getStatusLine();
            // additional cancellation check as getResponseData() can take non-zero time to process
            if (!Thread.currentThread().isInterrupted()) {
                onResponse(status.getStatusCode(), response);
            }
        }
    }

    @Override
    public void sendStartMessage() {

    }

    @Override
    public void sendFinishMessage() {
    }

    @Override
    public void sendProgressMessage(long bytesWritten, long bytesTotal) {
        if (null != mOnRequestListener) {
            mOnRequestListener.onRequestProgress(bytesWritten, bytesTotal);
        }
    }

    @Override
    public void sendCancelMessage() {
    }

    @Override
    public void sendSuccessMessage(int statusCode, Header[] headers, byte[] responseBody) {
    }

    @Override
    public void sendFailureMessage(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (error instanceof IOException) {
            onFailure(statusCode, (IOException) error);
        } else {
            onFailure(statusCode, new IOException(error.getMessage()));
        }
    }

    @Override
    public void sendRetryMessage(int retryNo) {}

    @Override
    public URI getRequestURI() {
        return requestURI;
    }

    @Override
    public void setRequestURI(URI requestURI) {
        this.requestURI = requestURI;
    }

    @Override
    public void setRequestHeaders(Header[] requestHeaders) {
        this.mRequestHeaders = requestHeaders;
    }

    @Override
    public boolean getUseSynchronousMode() {
        return false;
    }

    @Override
    public void setUseSynchronousMode(boolean useSynchronousMode) {
    }

    @Override
    public void onPreProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
    }

    @Override
    public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
    }

    @Override
    public boolean getUsePoolThread() {
        return false;
    }

    @Override
    public void setUsePoolThread(boolean usePoolThread) {
    }

    @Override
    public Object getTag() {
        return null;
    }

    @Override
    public void setTag(Object TAG) {
    }

    public void setOnRequestListener(OnRequestListener onRequestListener) {
        this.mOnRequestListener = onRequestListener;
    }

    public abstract void onFailure(int statusCode, IOException e);

    public abstract void onResponse(int statusCode, HttpResponse response) throws IOException;


    public interface OnRequestListener {
        void onRequestProgress(long bytesWritten, long contentLength);
    }

}
