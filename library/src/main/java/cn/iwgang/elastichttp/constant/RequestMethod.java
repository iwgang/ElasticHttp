package cn.iwgang.elastichttp.constant;

/**
 * Created by apple on 16/1/15.
 */
public final class RequestMethod {
    /** Request Method GET */
    public static final int REQUEST_METHOD_GET = 1001;
    /** Request Method POST */
    public static final int REQUEST_METHOD_POST = 1002;
    /** Request Method HEAD */
    public static final int REQUEST_METHOD_HEAD = 1003;
    /** Request Method DELETE */
    public static final int REQUEST_METHOD_DELETE = 1004;
    /** Request Method PUT */
    public static final int REQUEST_METHOD_PUT = 1005;
    /** Request Method PATCH */
    public static final int REQUEST_METHOD_PATCH = 1006;


    public static String toMethodStr(int code) {
        String retMethodStr = null;
        switch (code) {
            case RequestMethod.REQUEST_METHOD_GET:
                retMethodStr = "GET";
                break;
            case RequestMethod.REQUEST_METHOD_POST:
                retMethodStr = "POST";
                break;
            case RequestMethod.REQUEST_METHOD_HEAD:
                retMethodStr = "HEAD";
                break;
            case RequestMethod.REQUEST_METHOD_DELETE:
                retMethodStr = "DELETE";
                break;
            case RequestMethod.REQUEST_METHOD_PUT:
                retMethodStr = "PUT";
                break;
            case RequestMethod.REQUEST_METHOD_PATCH:
                retMethodStr = "PATCH";
                break;
        }

        return retMethodStr;
    }

}
