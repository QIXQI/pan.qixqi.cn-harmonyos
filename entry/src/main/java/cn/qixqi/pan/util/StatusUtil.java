package cn.qixqi.pan.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StatusUtil {

    public static Map<Integer, String> statuses;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "OFFLINE");
        map.put(1, "ONLINE");
        statuses = Collections.unmodifiableMap(map);
    }
}
