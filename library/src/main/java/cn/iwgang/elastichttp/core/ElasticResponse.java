package cn.iwgang.elastichttp.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Http框架响应体接口定义
 * Created by iWgang on 16/1/15.
 */
public interface ElasticResponse {

    /**
     * 获取请求信息对象
     *
     * @return
     */
    ElasticRequestInfo getRequestInfo();

    /**
     * 获取响应体InputStream
     *
     * @return
     * @throws IOException
     */
    InputStream getBodyIs() throws IOException;

    /**
     * 获取响应体String
     *
     * @return
     * @throws IOException
     */
    String getBodyStr() throws IOException;

    /**
     * 获取Http状态码
     *
     * @return
     */
    int getHttpCode();

    /**
     * 判断响应是否成功
     *
     * @return
     */
    boolean isSuccessful();

    /**
     * 获取响应头信息
     *
     * @return
     */
    Map<String, String> getResponseHeader();

    long getContentLength();

    String getEncodedPath();

}
