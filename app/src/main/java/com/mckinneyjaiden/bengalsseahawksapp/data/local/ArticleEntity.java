package com.mckinneyjaiden.bengalsseahawksapp.data.local;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "articles")
public class ArticleEntity {
    @PrimaryKey
    @NonNull
    public final String id;
    @NonNull
    public final String url;
    @NonNull
    public final String team;
    @NonNull
    public final String title;
    @NonNull
    public final String source;
    public final long publishedAt;
    public final long fetchedAt;
    public final boolean bookmarked;

    public ArticleEntity(@NonNull String id, @NonNull String url, @NonNull String team,
                         @NonNull String title, @NonNull String source,
                         long publishedAt, long fetchedAt, boolean bookmarked) {
        this.id = id;
        this.url = url;
        this.team = team;
        this.title = title;
        this.source = source;
        this.publishedAt = publishedAt;
        this.fetchedAt = fetchedAt;
        this.bookmarked = bookmarked;
    }
}
