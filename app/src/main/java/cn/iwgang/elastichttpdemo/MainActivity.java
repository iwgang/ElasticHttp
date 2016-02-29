package cn.iwgang.elastichttpdemo;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import cn.iwgang.elastichttp.callback.DownloadCallback;
import cn.iwgang.elastichttp.callback.DownloadCallbackWrap;
import cn.iwgang.elastichttp.callback.HeadCallback;
import cn.iwgang.elastichttp.callback.TextCallback;
import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticRequestInfo;
import cn.iwgang.elastichttp.core.ElasticResponseInfo;
import cn.iwgang.elastichttp.core.OnUploadProgressListener;
import cn.iwgang.elastichttp.core.RequestTimeOut;
import cn.iwgang.elastichttp.request.GetRequest;
import cn.iwgang.elastichttp.request.HeadRequest;
import cn.iwgang.elastichttp.request.MultiFileUploadRequest;
import cn.iwgang.elastichttp.request.PostJsonRequest;

public class MainActivity extends AppCompatActivity implements DownloadCallbackWrap.InternalCallback {
    private Button mBtnGet, mBtnPost, mBtnHead;
    private Button mBtnDownload1, mBtnDownload2, mBtnDownload3;
    private Button mBtnUpload;
    private TextView mTvText;

    private String currTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTvText = (TextView)findViewById(R.id.tv_text);
        mBtnGet = (Button)findViewById(R.id.btn_get);
        mBtnPost = (Button)findViewById(R.id.btn_post);
        mBtnHead = (Button)findViewById(R.id.btn_head);
        mBtnDownload1 = (Button)findViewById(R.id.btn_download1);
        mBtnDownload2 = (Button)findViewById(R.id.btn_download2);
        mBtnDownload3 = (Button)findViewById(R.id.btn_download3);
        mBtnUpload = (Button)findViewById(R.id.btn_upload);

        currTime = String.valueOf(System.currentTimeMillis() / 1000);

        // GET
        mBtnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnGet.setText("GET 准备中");

                // Text callback
                Map<String, String> requestParamMap = new HashMap<>();
                requestParamMap.put("key", "9be658764b4446ebbcc8f5d9e1263e88");
                requestParamMap.put("consName", "射手座");
                requestParamMap.put("type", "today");
                final GetRequest getRequest = new GetRequest("http://api.avatardata.cn/Constellation/Query", requestParamMap);
                getRequest.setHttpLibraryKey(MyApplication.HTTP_LIBRARY_KEY_ANDROID_ASYNC_HTTP); // 使用android-async-http请求

                ElasticHttp.getInstance().asyncExecute(getRequest, new TextCallback() {
                    @Override
                    public void onSuccess(ElasticResponseInfo responseInfo, String retStr) {
                        Map<String, String> responseHeader = responseInfo.getResponseHeader();
                        Log.i("wg", "Date = " + responseInfo.getResponseHeader("Date"));
                        if (null != responseHeader) {
                            for (Map.Entry<String, String> header : responseHeader.entrySet()) {
                                Log.i("wg", header.toString());
                            }
                        } else {
                            Log.i("wg", "responseHeader null");
                        }

                        mBtnGet.setText("GET 成功");
                        mTvText.setText(retStr);
                    }

                    @Override
                    public void onFailure(ElasticRequestInfo requestInfo, Exception e) {
                        mBtnGet.setText("GET 失败");
                        mTvText.setText(e.toString());
                    }
                });

//                // JSON callback
//                ElasticHttp.getInstance().asyncExecute(new GetRequest("http://120.25.147.149/data/tag.json"), new JsonObjCallback<List<String>>() {
//                    @Override
//                    public void onFailure(ElasticRequestInfo requestInfo, Exception e) {
//                        mBtnGet.setText("GET 失败");
//                        mTvText.setText(e.toString());
//                    }
//
//                    @Override
//                    public void onSuccess(ElasticResponseInfo responseInfo, List<String> strings) {
//                        mBtnGet.setText("GET 成功");
//                        mTvText.setText(strings.toString());
//                    }
//                });


                // 同步请求, 其余Request Method类似
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            ElasticResponse elasticResponse = ElasticHttp.getInstance().syncExecute(getRequest);
//                            Log.i("wg", "elasticResponse = " + elasticResponse.isSuccessful() + " _ " + elasticResponse.getHttpCode() + " _ " + elasticResponse.getBodyStr());
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            Log.i("wg", "e = " + e.toString());
//                        }
//                    }
//                }).start();
            }
        });


        // POST
        mBtnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnPost.setText("Post 准备中");

                // Post RequestParam 如: key1=value1&key2=value2
//                PostRequest postRequestParam = new PostRequest("http://api.avatardata.cn/Constellation/Query");
//                postRequestParam.setHttpLibraryKey(MyApplication.HTTP_LIBRARY_KEY_ANDROID_ASYNC_HTTP);
//                postRequestParam.addRequestParam("key", "9be658764b4446ebbcc8f5d9e1263e88");
//                postRequestParam.addRequestParam("consName", "射手座");
//                postRequestParam.addRequestParam("type", "today");

                // Json body
                final String jsonStr = "{\"name\":\"iwgang\", \"city\":\"成都\"}";
                PostJsonRequest postJsonRequest = new PostJsonRequest("http://192.168.1.1:8099/iwgangser/modInfo", jsonStr);
                postJsonRequest.addHeader("token", "test_token");
                postJsonRequest.setTag("aabbcc");

                ElasticHttp.getInstance().asyncExecute(postJsonRequest, new TextCallback() {
                    @Override
                    public void onSuccess(ElasticResponseInfo responseInfo, String retStr) {
                        Map<String, String> responseHeader = responseInfo.getResponseHeader();
                        if (null != responseHeader) {
                            for (Map.Entry<String, String> header : responseHeader.entrySet()) {
                                Log.i("wg", header.toString());
                            }
                        } else {
                            Log.i("wg", "responseHeader null");
                        }

                        mBtnPost.setText("Post 成功");
                        mTvText.setText(retStr);
//                        Log.i("wg", "Post onSuccess  " + responseInfo.getRequestInfo().getTag());
//                        Log.i("wg", "Post onSuccess  " + retStr);
                    }

                    @Override
                    public void onFailure(ElasticRequestInfo requestInfo, Exception e) {
                        mBtnPost.setText("Post 失败");
                        mTvText.setText(e.toString());
//                        Log.i("wg", "Post onFailure  " + requestInfo.getTag());
//                        Log.i("wg", "Post onFailure  " + e);
                    }
                });
            }
        });


        // HEAD
        mBtnHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnHead.setText("HEAD 准备中");

//                Map<String, String> requestParamMap = new HashMap<>();
//                requestParamMap.put("key", "9be658764b4446ebbcc8f5d9e1263e88");
//                requestParamMap.put("consName", "射手座");
//                requestParamMap.put("type", "today");
//                HeadRequest headRequest = new HeadRequest("http://api.avatardata.cn/Constellation/Query", requestParamMap);

                HeadRequest headRequest = new HeadRequest("https://dl.wandoujia.com/files/jupiter/latest/wandoujia-wandoujia_web.apk");

                headRequest.setHttpLibraryKey(MyApplication.HTTP_LIBRARY_KEY_ANDROID_ASYNC_HTTP); // 使用android-async-http请求

                ElasticHttp.getInstance().asyncExecute(headRequest, new HeadCallback() {
                    @Override
                    public void onSuccess(ElasticResponseInfo responseInfo) {
                        Map<String, String> responseHeader = responseInfo.getResponseHeader();
                        Log.i("wg", "Content-Length = " + responseInfo.getResponseHeader("Content-Length"));
                        if (null != responseHeader) {
                            StringBuilder headStrBuilder = new StringBuilder();
                            for (Map.Entry<String, String> header : responseHeader.entrySet()) {
                                headStrBuilder.append(header.getKey()).append(" : ").append(header.getValue()).append("\n\r");
                            }
                            mTvText.setText(headStrBuilder.toString());
                        } else {
                            mTvText.setText("responseHeader null");
                        }

                        mBtnHead.setText("HEAD 成功");
                    }

                    @Override
                    public void onFailure(ElasticRequestInfo requestInfo, Exception e) {
                        mBtnHead.setText("HEAD 失败");
                        mTvText.setText(e.toString());
                    }
                });
            }
        });


        // 多文件文件上传
        final File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "testimage1.jpg");
        final File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "testimage2.jpg");
//        final File file2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "wdj.apk");
        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnUpload.setText("upload 准备中");

                MultiFileUploadRequest multiFileUploadRequest = new MultiFileUploadRequest("http://192.168.1.1:8099/iwgangser/uploadND");

                multiFileUploadRequest.addHeader("token", "test_token");
                multiFileUploadRequest.addFilePart("pic1", UUID.randomUUID().toString() + ".jpg", file1);
                multiFileUploadRequest.addFilePart("pic2", UUID.randomUUID().toString() + ".jpg", file2);
//                multiFileUploadRequest.addStringPart("nickname", "iWgang");
                multiFileUploadRequest.setUploadProgressListener(new OnUploadProgressListener() {
                    @Override
                    public void onProgressUpdate(ElasticRequestInfo requestInfo, float progress) {
                        mBtnUpload.setText(String.format("upload 上传中(%.2f%%)", progress));
                    }
                });
                ElasticHttp.getInstance().asyncExecute(multiFileUploadRequest, new TextCallback() {
                    @Override
                    public void onSuccess(ElasticResponseInfo responseInfo, String retStr) {
                        mBtnUpload.setText("update 上传成功");
                        mTvText.setText(retStr);
//                        Log.i("wg", "文件上传 onSuccess " + retStr);
                    }

                    @Override
                    public void onFailure(ElasticRequestInfo requestInfo, Exception e) {
                        mBtnUpload.setText("update 上传失败");
                        mTvText.setText(e.toString());
//                        Log.i("wg", "文件上传 onFailure " + e);
                    }
                });
            }
        });


        // 文件下载
        mBtnDownload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnDownload1.setText("download 1 准备中");
                GetRequest downloadRequest = new GetRequest("https://dl.wandoujia.com/files/jupiter/latest/wandoujia-wandoujia_web.apk");
                downloadRequest.setRequestTimeOut(new RequestTimeOut(60, 60));
                downloadRequest.setTag(1);
                ElasticHttp.getInstance().asyncExecute(downloadRequest, new DownloadCallbackWrap(MainActivity.this));
            }
        });
        mBtnDownload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnDownload2.setText("download 2 准备中");
                GetRequest downloadRequest = new GetRequest("http://download.immomo.com/android/market/sem/momo_6.5.3_visitor_c29.apk");
                downloadRequest.setRequestTimeOut(new RequestTimeOut(60, 60));
                downloadRequest.setTag(2);
                ElasticHttp.getInstance().asyncExecute(downloadRequest, new DownloadCallbackWrap(MainActivity.this));
            }
        });
        mBtnDownload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnDownload3.setText("download 3 准备中");
                GetRequest downloadRequest = new GetRequest("http://app.mi.com/download/82348");
                downloadRequest.setHttpLibraryKey(MyApplication.HTTP_LIBRARY_KEY_ANDROID_ASYNC_HTTP); // 使用android-async-http请求
                ElasticHttp.getInstance().asyncExecute(downloadRequest, new DownloadCallback() {
                    @Override
                    public void onSuccess(ElasticResponseInfo responseInfo, DownloadFileInfo downloadFileInfo) {
                        Map<String, String> responseHeader = responseInfo.getResponseHeader();
                        Log.i("wg", "Date = " + responseInfo.getResponseHeader("Date"));
                        if (null != responseHeader) {
                            for (Map.Entry<String, String> header : responseHeader.entrySet()) {
                                Log.i("wg", header.toString());
                            }
                        } else {
                            Log.i("wg", "responseHeader null");
                        }

                        Log.i("wg", "onDownloadSuccess ... " + responseInfo + " downloadFileInfo = " + downloadFileInfo);
                        mBtnDownload3.setText("download 3 完成");
                        mTvText.setText(downloadFileInfo.toString());
                    }

                    @Override
                    public void onFailure(ElasticRequestInfo requestInfo, Exception e) {
                        Log.i("wg", "onDownloadFailure ... " + requestInfo + " e = " + e);
                        mBtnDownload3.setText("download 3 失败 ");
                        mTvText.setText("download 3 " + e.toString());
                    }

                    @Override
                    public boolean onUpdateProgressListener(ElasticResponseInfo responseInfo, DownloadFileInfo downloadFileInfo, float progress) {
                        mBtnDownload3.setText(String.format("download 3 (%.2f%%)", progress));
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void onDownloadFailure(ElasticRequestInfo requestInfo, Exception e) {
        Log.i("wg", "onDownloadFailure ... " + requestInfo + " e = " + e);

        int tag = Integer.valueOf(requestInfo.getTag().toString());
        switch (tag) {
            case 1:
                mBtnDownload1.setText("download 1 失败");
                mTvText.setText("download 1 " + e.toString());
                break;
            case 2:
                mBtnDownload2.setText("download 2 失败");
                mTvText.setText("download 2 " + e.toString());
                break;
        }
    }

    @Override
    public void onDownloadSuccess(ElasticResponseInfo responseInfo, DownloadCallback.DownloadFileInfo downloadFileInfo) {
        Log.i("wg", "onDownloadSuccess ... " + responseInfo + " downloadFileInfo = " + downloadFileInfo);

        int tag = Integer.valueOf(responseInfo.getRequestInfo().getTag().toString());
        switch (tag) {
            case 1:
                mBtnDownload1.setText("download 1 完成");
                mTvText.setText("download 1 " + downloadFileInfo.toString());
                break;
            case 2:
                mBtnDownload2.setText("download 2 完成");
                mTvText.setText("download 2 " + downloadFileInfo.toString());
                break;
        }
    }

    @Override
    public boolean onDownloadProgressListener(ElasticResponseInfo responseInfo, DownloadCallback.DownloadFileInfo downloadFileInfo, float progress) {
//        Log.i("wg", "onDownloadProgressListener " + progress);
        int tag = Integer.valueOf(responseInfo.getRequestInfo().getTag().toString());
        switch (tag) {
            case 1:
                mBtnDownload1.setText(String.format(Locale.getDefault(), "download 1 (%.2f%%)", progress));
                break;
            case 2:
                mBtnDownload2.setText(String.format(Locale.getDefault(), "download 2 (%.2f%%)", progress));
                break;
        }

        return true;
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
