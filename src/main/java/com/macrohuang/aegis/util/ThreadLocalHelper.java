package com.macrohuang.aegis.util;

import java.util.Map;

public abstract class ThreadLocalHelper {
    private static final ThreadLocal<Object[]> keyLocal = new ThreadLocal<Object[]>();
    private static final ThreadLocal<Map<String, String>> PARAMS = new ThreadLocal<Map<String, String>>();

    public static void bindBlockedKey(Object[] key) {
        keyLocal.set(key);
    }

    public static Object[] getBlockedKey() {
        return keyLocal.get();
    }

    public static void bindParams(Map<String, String> params) {
        PARAMS.set(params);
    }

    public static Map<String, String> getParams() {
        return PARAMS.get();
    }

    public static String getParamByKey(String key) {
        return PARAMS.get().get(key);
    }
}
