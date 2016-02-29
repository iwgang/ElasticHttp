package cn.iwgang.elastichttp.callback;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponse;
import cn.iwgang.elastichttp.core.ElasticResponseInfo;

/**
 * Created by iWgang on 16/1/20.
 */
public abstract class DownloadCallback extends ElasticCallback {
    private String mSaveFileDir;
    private String mFileName;
    private boolean isCallbackDownloadProgress = true;

    public DownloadCallback() {
        this(null, null);
    }

    public DownloadCallback(String saveFileDir, String fileName) {
        this.mSaveFileDir = saveFileDir;
        this.mFileName = fileName;

        if (TextUtils.isEmpty(mSaveFileDir)) {
            this.mSaveFileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }

    @Override
    public void handlerFailure(final ElasticRequestInfo requestInfo, final Exception e) {
        ElasticHttp.getInstance().runMain(new Runnable() {
            @Override
            public void run() {
                onFailure(requestInfo, e);
            }
        });
    }

    @Override
    public void handlerSuccess(final ElasticResponseInfo responseInfo, final ElasticResponse elasticResponse) {
        String finalFileName;
        if (TextUtils.isEmpty(mFileName)) {
            String encodedPath = elasticResponse.getEncodedPath();
            if (!TextUtils.isEmpty(encodedPath) && encodedPath.contains("/")) {
                finalFileName = encodedPath.substring(encodedPath.lastIndexOf("/") + 1, encodedPath.length());
            } else {
                finalFileName = String.valueOf(System.currentTimeMillis());
            }
        } else {
            finalFileName = mFileName;
        }

        // 写入本地
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len;
        long sum = 0;
        FileOutputStream fos = null;
        try {
            is = elasticResponse.getBodyIs();
            final long total = elasticResponse.getContentLength();
            final DownloadFileInfo downloadFileInfo = new DownloadFileInfo(finalFileName, mSaveFileDir, total);

            fos = new FileOutputStream(new File(mSaveFileDir, finalFileName));
            while ((len = is.read(buf)) != -1) {
                sum += len;
                fos.write(buf, 0, len);
                // 获取百分比
                final float curProgress = sum * 1.0f / total * 100;

                if (isCallbackDownloadProgress) {
                    ElasticHttp.getInstance().runMain(new Runnable() {
                        @Override
                        public void run() {
                            // 回调成功
                            isCallbackDownloadProgress = onUpdateProgressListener(responseInfo, downloadFileInfo, curProgress);
                        }
                    });
                }
            }
            fos.flush();

            ElasticHttp.getInstance().runMain(new Runnable() {
                @Override
                public void run() {
                    // 回调成功
                    onSuccess(responseInfo, downloadFileInfo);
                }
            });
        } catch (Exception e) {
            // 回调失败
            handlerFailure(responseInfo.getRequestInfo(), e);
        } finally {
            try {
                if (is != null) is.close();

                if (fos != null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public abstract void onSuccess(ElasticResponseInfo responseInfo, DownloadFileInfo downloadFileInfo);

    public abstract void onFailure(ElasticRequestInfo requestInfo, Exception e);

    public boolean onUpdateProgressListener(ElasticResponseInfo responseInfo, DownloadFileInfo downloadFileInfo, float progress) {
        return false;
    }


    public static class DownloadFileInfo {
        private String fileName;
        private String saveFileDir;
        private long fileSize;

        public DownloadFileInfo(String fileName, String saveFileDir, long fileSize) {
            this.fileName = fileName;
            this.saveFileDir = saveFileDir;
            this.fileSize = fileSize;
        }

        public String getFileName() {
            return fileName;
        }

        public long getFileSize() {
            return fileSize;
        }

        public String getSaveFileDir() {
            return saveFileDir;
        }

        @Override
        public String toString() {
            return "DownloadFileInfo{" +
                    "fileName='" + fileName + '\'' +
                    ", saveFileDir='" + saveFileDir + '\'' +
                    ", fileSize=" + fileSize +
                    '}';
        }
    }

}
