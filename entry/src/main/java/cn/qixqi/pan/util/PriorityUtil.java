package cn.qixqi.pan.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PriorityUtil {

    public static Map<Integer, String> priorities;

    static {
        Map<Integer, String> map = new HashMap<>();
        map.put(0, "NORMAL_USER");
        map.put(1, "VIP_USER");
        map.put(2, "ADMIN");
        priorities = Collections.unmodifiableMap(map);
    }
}
