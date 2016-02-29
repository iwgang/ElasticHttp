package cn.iwgang.elastichttpdemo;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import java.io.File;

import cn.iwgang.elasticasynchttp.AsyncHttpLibrary;
import cn.iwgang.elastichttp.core.ElasticHttp;
import cn.iwgang.elastichttp.core.ElasticHttpConfiguration;
import cn.iwgang.elastichttp.core.ElasticRequest;
import cn.iwgang.elastichttp.core.ElasticResponse;
import cn.iwgang.elastichttp.interceptor.ElasticInterceptor;
import cn.iwgang.elasticokhttp.OkHttpLibrary;

/**
 * Created by apple on 16/2/1.
 */
public class MyApplication extends Application {
    public static final int HTTP_LIBRARY_KEY_ANDROID_ASYNC_HTTP = 20001;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化
        ElasticHttpConfiguration configure = new ElasticHttpConfiguration.Builder()
                .setCacheDir(Environment.getExternalStorageDirectory() + File.separator + "ElasticHttpCache")
                .setHttpLibrary(new AsyncHttpLibrary()) // 默认HttpLibrary
                .addHttpLibrary(HTTP_LIBRARY_KEY_ANDROID_ASYNC_HTTP, new OkHttpLibrary())
                .addInterceptor(new ElasticInterceptor() {
                    @Override
                    public void onRequest(ElasticRequest request) {
                        // 添加共用头信息
                        request.addHeader("auth", "test_auth_value");
                        request.addHeader("version", "1.0");
                        Log.i("wg", "拦截器 onRequest = " + request.getMethodStr() + " _ " + request.getHttpLibraryKey() + " _ " + request.getUrl());
                    }

                    @Override
                    public void onResponse(boolean isSuccess, ElasticResponse elasticResponse, Exception ex) {
                        Log.i("wg", "拦截器 onResponse = " + isSuccess + " _ " + elasticResponse + " _ " + ex);
                    }
                })
                .builder();
        ElasticHttp.getInstance().initialize(configure);
    }
}
