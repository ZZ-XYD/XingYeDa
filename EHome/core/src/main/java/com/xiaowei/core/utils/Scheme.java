package com.xiaowei.core.utils;

import java.util.Locale;

/**
 * Created by Administrator on 2016/12/8.
 */

public enum Scheme {
    HTTP("http"),
    HTTPS("https"),
    FILE("file"),
    CONTENT("content"),
    ASSETS("assets"),
    DRAWABLE("drawable"),
    UNKNOWN("");

    private String scheme;
    private String uriPrefix;

    private Scheme(String scheme) {
        this.scheme = scheme;
        this.uriPrefix = scheme + "://";
    }

    public static Scheme ofUri(String uri) {
        if (uri != null) {
            Scheme[] arr$ = values();
            int len$ = arr$.length;

            for (int i$ = 0; i$ < len$; ++i$) {
                Scheme s = arr$[i$];
                if (s.belongsTo(uri)) {
                    return s;
                }
            }
        }

        return UNKNOWN;
    }

    private boolean belongsTo(String uri) {
        return uri.toLowerCase(Locale.US).startsWith(this.uriPrefix);
    }

    public String wrap(String path) {
        return this.uriPrefix + path;
    }

    public String crop(String uri) {
        if (!this.belongsTo(uri)) {
            throw new IllegalArgumentException(String.format("URI [%1$s] " +
                    "doesn\'t have expected scheme [%2$s]", new Object[]{uri,
                    this.scheme}));
        } else {
            return uri.substring(this.uriPrefix.length());
        }
    }
}
