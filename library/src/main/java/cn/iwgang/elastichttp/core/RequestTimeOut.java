package cn.iwgang.elastichttp.core;

/**
 * 请求超时信息
 * Created by iWgang on 16/1/20.
 */
public class RequestTimeOut {
    private int connectTimeout; // 连接超时时间, 不填将使用默认值  单位 秒
    private int writeTimeout; // 写入超时时间, 不填将使用默认值  单位 秒
    private int readTimeout; // 读取超时时间, 不填将使用默认值  单位 秒

    public RequestTimeOut(){}

    public RequestTimeOut(int connectTimeout, int readTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public RequestTimeOut(int connectTimeout, int readTimeout, int writeTimeout) {
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
        this.writeTimeout = writeTimeout;
    }

    public RequestTimeOut setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public RequestTimeOut setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public RequestTimeOut setWriteTimeout(int writeTimeout) {
        this.writeTimeout = writeTimeout;
        return this;
    }


    public int getConnectTimeout() {
        return connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getWriteTimeout() {
        return writeTimeout;
    }

    @Override
    public String toString() {
        return "RequestTimeOut{" +
                "connectTimeout=" + connectTimeout +
                ", writeTimeout=" + writeTimeout +
                ", readTimeout=" + readTimeout +
                '}';
    }

}
