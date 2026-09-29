package com.mckinneyjaiden.bengalsseahawksapp.model;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ArticleLinksTest {
    @Test
    public void acceptsHttpAndHttpsLinksWithHosts() {
        assertTrue(ArticleLinks.isWebUrl("https://example.com/story"));
        assertTrue(ArticleLinks.isWebUrl("HTTP://example.com/story"));
    }

    @Test
    public void rejectsMissingUnsafeAndMalformedLinks() {
        assertFalse(ArticleLinks.isWebUrl(null));
        assertFalse(ArticleLinks.isWebUrl("javascript:alert(1)"));
        assertFalse(ArticleLinks.isWebUrl("https:///missing-host"));
        assertFalse(ArticleLinks.isWebUrl("not a url"));
    }
}
