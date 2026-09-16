package com.mckinneyjaiden.bengalsseahawksapp.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public final class ArticleLinks {
    private ArticleLinks() {
    }

    public static boolean isWebUrl(String value) {
        if (value == null) {
            return false;
        }
        try {
            URI uri = new URI(value.trim());
            String scheme = uri.getScheme();
            return uri.getHost() != null && scheme != null
                    && ("https".equals(scheme.toLowerCase(Locale.US))
                    || "http".equals(scheme.toLowerCase(Locale.US)));
        } catch (URISyntaxException exception) {
            return false;
        }
    }
}
