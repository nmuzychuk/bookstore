package com.nmuzychuk;

import com.google.gson.Gson;

public final class GsonHelper {
    private static final Gson gson = new Gson();

    private GsonHelper() {
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }
}
