package cn.iwgang.elastichttp.core;

/**
 * Created by iWgang on 16/1/21.
 */
public interface OnUploadProgressListener {

    void onProgressUpdate(ElasticRequestInfo elasticRequestInfo, float progress);

}
