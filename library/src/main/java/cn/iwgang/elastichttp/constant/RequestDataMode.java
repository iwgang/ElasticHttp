package cn.iwgang.elastichttp.constant;

/**
 * Created by iWgang on 16/1/30.
 */
public final class RequestDataMode {
    /** 只请求网络 */
    public static final int ONLY_NET = 3001;
    /** 网络和缓存都请求 (检查有缓存后先回调, 然后再请求网络成功后再回调 二者都有数据的场景下是回调两次onSuccess()) */
    public static final int NET_AND_CACHE = 3003;
    /** 只请求网络，但保存缓存（界面有需要缓存，并且用户主动刷新的场景请使用这个） */
    public static final int ONLY_NET_BUT_SAVA_CACHE = 3004;
}