package cn.iwgang.elastichttp.util;

import java.util.Map;

/**
 * Created by iWgang on 16/2/1.
 */
public final class ElasticUtil {

    public static String mapToStr(Map<String, String> map) {
        StringBuilder strBuilder = new StringBuilder();
        int index = 0;
        int rpMapSize = map.size();
        for (Map.Entry<String, String> rp : map.entrySet()) {
            strBuilder.append(rp.getKey()).append("=").append(rp.getKey());

            index ++;
            if (index != rpMapSize) {
                strBuilder.append("&");
            }
        }

        return strBuilder.toString();
    }

}
