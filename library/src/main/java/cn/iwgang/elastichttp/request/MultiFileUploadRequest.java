package cn.iwgang.elastichttp.request;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.iwgang.elastichttp.constant.RequestMediaType;
import cn.iwgang.elastichttp.constant.RequestMethod;
import cn.iwgang.elastichttp.core.ElasticRequest;
import cn.iwgang.elastichttp.core.OnUploadProgressListener;

/**
 * 多文件上传 请求
 * Created by iWgang on 16/1/21.
 */
public class MultiFileUploadRequest extends ElasticRequest {
    private Map<String, String> mStringPartMap;
    private List<BasePartInfo> mPartList;
    private OnUploadProgressListener mOnUploadProgressListener;

    public MultiFileUploadRequest(String url) {
        super(url, RequestMethod.REQUEST_METHOD_POST);
    }

    public void addStringPart(String key, String value) {
        if (null == mStringPartMap) {
            mStringPartMap = new HashMap<>();
        }

        mStringPartMap.put(key, value);
    }

    public void setStringParts(Map<String, String> stringPartMap) {
        this.mStringPartMap = stringPartMap;
    }

    public void addFilePart(String name, String filename, File file) {
        this.addFilePart(name, filename, null, file);
    }

    public void addFilePart(String name, String filename, String contentType, File file) {
        if (null == mPartList) {
            mPartList = new ArrayList<>();
        }

        mPartList.add(new FilePartInfo(name, filename, contentType, file));
    }

    public void addBytePart(String name, String filename, byte[] content) {
        this.addBytePart(name, filename, null, content);
    }

    public void addBytePart(String name, String filename, String contentType, byte[] content) {
        this.addBytePart(name, filename, contentType, content, 0, 0);
    }

    public void addBytePart(String name, String filename, String contentType, byte[] content, int offset, int byteCount) {
        if (null == mPartList) {
            mPartList = new ArrayList<>();
        }

        mPartList.add(new BytePartInfo(name, filename, contentType, content, offset, byteCount));
    }

    public OnUploadProgressListener getOnUploadProgressListener() {
        return mOnUploadProgressListener;
    }

    public List<BasePartInfo> getPartList() {
        return mPartList;
    }

    public Map<String, String> getStringPartMap() {
        return mStringPartMap;
    }

    @Override
    public void setUploadProgressListener(OnUploadProgressListener onUploadProgressListener) {
        this.mOnUploadProgressListener = onUploadProgressListener;
    }


    public static class BasePartInfo {
        private String name;
        private String filename;
        private String mediaType;

        public BasePartInfo(String name, String filename, String mediaType) {
            this.name = name;
            this.filename = filename;
            this.mediaType = mediaType;
        }

        public String getFilename() {
            return filename;
        }

        public String getMediaType() {
            return mediaType;
        }

        public String getName() {
            return name;
        }
    }

    public static class FilePartInfo extends BasePartInfo {
        private File file;

        public FilePartInfo(String name, String filename, String contentType, File file) {
            super(name, filename, !TextUtils.isEmpty(contentType) ? contentType : RequestMediaType.MEDIA_TYPE_STREAM);
            this.file = file;
        }

        public File getFile() {
            return file;
        }
    }

    public static class BytePartInfo extends BasePartInfo {
        private byte[] content;
        private int offset;
        private int byteCount;

        public BytePartInfo(String name, String filename, String contentType, byte[] content, int offset, int byteCount) {
            super(name, filename, !TextUtils.isEmpty(contentType) ? contentType : RequestMediaType.MEDIA_TYPE_STREAM);
            this.content = content;
            this.offset = offset;
            this.byteCount = byteCount;
        }

        public int getByteCount() {
            return byteCount;
        }

        public byte[] getContent() {
            return content;
        }

        public int getOffset() {
            return offset;
        }
    }

}
