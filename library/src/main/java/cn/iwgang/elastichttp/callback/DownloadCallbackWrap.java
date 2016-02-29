package cn.iwgang.elastichttp.callback;

import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponseInfo;

/**
 * Created by iWgang on 16/1/20.
 */
public class DownloadCallbackWrap extends DownloadCallback {
    private InternalCallback mInternalCallback;

    public DownloadCallbackWrap(InternalCallback internalCallback) {
        this(null, null, internalCallback);
    }

    public DownloadCallbackWrap(String saveFileDir, String fileName, InternalCallback internalCallback) {
        super(saveFileDir, fileName);
        this.mInternalCallback = internalCallback;
    }

    @Override
    public void onFailure(ElasticRequestInfo requestInfo, Exception e) {
        mInternalCallback.onDownloadFailure(requestInfo, e);
    }

    @Override
    public void onSuccess(ElasticResponseInfo responseInfo, DownloadFileInfo downloadFileInfo) {
        mInternalCallback.onDownloadSuccess(responseInfo, downloadFileInfo);
    }

    @Override
    public boolean onUpdateProgressListener(ElasticResponseInfo responseInfo, DownloadFileInfo downloadFileInfo, float progress) {
        return mInternalCallback.onDownloadProgressListener(responseInfo, downloadFileInfo, progress);
    }

    public interface InternalCallback {
        void onDownloadFailure(ElasticRequestInfo requestInfo, Exception e);

        void onDownloadSuccess(ElasticResponseInfo responseInfo, DownloadFileInfo downloadFileInfo);

        boolean onDownloadProgressListener(ElasticResponseInfo responseInfo, DownloadFileInfo downloadFileInfo, float progress);
    }

}
