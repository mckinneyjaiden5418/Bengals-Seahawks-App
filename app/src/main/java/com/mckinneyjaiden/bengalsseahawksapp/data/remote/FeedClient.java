package com.mckinneyjaiden.bengalsseahawksapp.data.remote;

import com.mckinneyjaiden.bengalsseahawksapp.data.local.ArticleEntity;
import com.mckinneyjaiden.bengalsseahawksapp.model.ArticleLinks;
import com.mckinneyjaiden.bengalsseahawksapp.model.Team;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class FeedClient {
    private static final String SEARCH_URL =
            "https://news.google.com/rss/search?q=%s&hl=en-US&gl=US&ceid=US:en";
    private static final int MAX_ARTICLES = 50;

    public List<ArticleEntity> fetch(Team team) throws Exception {
        String query = team == Team.BENGALS ? "Cincinnati Bengals" : "Seattle Seahawks";
        URL url = new URL(String.format(Locale.US, SEARCH_URL,
                URLEncoder.encode(query, StandardCharsets.UTF_8.name())));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(10_000);
        connection.setReadTimeout(15_000);
        connection.setRequestProperty("User-Agent", "BengalsSeahawksNews/1.0");
        connection.setRequestProperty("Accept", "application/rss+xml, application/xml");

        try {
            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                throw new IOException("News service returned HTTP " + responseCode);
            }
            try (InputStream input = new BufferedInputStream(connection.getInputStream())) {
                return parse(input, team, System.currentTimeMillis());
            }
        } finally {
            connection.disconnect();
        }
    }

    List<ArticleEntity> parse(InputStream input, Team team, long fetchedAt) throws Exception {
        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(input, "UTF-8");

        List<ArticleEntity> articles = new ArrayList<>();
        String title = "";
        String link = "";
        String source = "";
        String published = "";
        boolean inItem = false;
        int event = parser.getEventType();

        while (event != XmlPullParser.END_DOCUMENT && articles.size() < MAX_ARTICLES) {
            if (event == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if ("item".equalsIgnoreCase(name)) {
                    inItem = true;
                    title = link = source = published = "";
                } else if (inItem && "title".equalsIgnoreCase(name)) {
                    title = safeNextText(parser);
                } else if (inItem && "link".equalsIgnoreCase(name)) {
                    link = safeNextText(parser);
                } else if (inItem && "source".equalsIgnoreCase(name)) {
                    source = safeNextText(parser);
                } else if (inItem && "pubDate".equalsIgnoreCase(name)) {
                    published = safeNextText(parser);
                }
            } else if (event == XmlPullParser.END_TAG && "item".equalsIgnoreCase(parser.getName())) {
                inItem = false;
                if (!title.trim().isEmpty() && ArticleLinks.isWebUrl(link)) {
                    articles.add(new ArticleEntity(
                            team.name() + "|" + link.trim(), link.trim(), team.name(), title.trim(),
                            source.trim().isEmpty() ? "Unknown source" : source.trim(),
                            parseDate(published, fetchedAt), fetchedAt, false));
                }
            }
            event = parser.next();
        }
        return articles;
    }

    private String safeNextText(XmlPullParser parser) throws Exception {
        String value = parser.nextText();
        return value.length() > 2_000 ? value.substring(0, 2_000) : value;
    }

    private long parseDate(String value, long fallback) {
        String[] formats = {"EEE, dd MMM yyyy HH:mm:ss z", "EEE, dd MMM yyyy HH:mm:ss Z"};
        for (String format : formats) {
            SimpleDateFormat parser = new SimpleDateFormat(format, Locale.US);
            parser.setTimeZone(TimeZone.getTimeZone("UTC"));
            try {
                return parser.parse(value).getTime();
            } catch (ParseException | NullPointerException ignored) {
                // Try the next common RSS date format.
            }
        }
        return fallback;
    }
}
