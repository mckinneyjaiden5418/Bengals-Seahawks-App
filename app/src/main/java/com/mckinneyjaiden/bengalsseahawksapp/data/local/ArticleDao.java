package com.mckinneyjaiden.bengalsseahawksapp.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArticleDao {
    @Query("SELECT * FROM articles WHERE team = :team ORDER BY publishedAt DESC, fetchedAt DESC")
    LiveData<List<ArticleEntity>> observeTeam(String team);

    @Query("SELECT * FROM articles WHERE bookmarked = 1 ORDER BY publishedAt DESC, fetchedAt DESC")
    LiveData<List<ArticleEntity>> observeBookmarks();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertIfNew(ArticleEntity article);

    @Query("UPDATE articles SET title = :title, source = :source, publishedAt = :publishedAt, fetchedAt = :fetchedAt WHERE id = :id")
    void updateRemoteFields(String id, String title, String source, long publishedAt, long fetchedAt);

    @Query("UPDATE articles SET bookmarked = :bookmarked WHERE id = :id")
    void setBookmarked(String id, boolean bookmarked);

    @Query("DELETE FROM articles WHERE bookmarked = 0 AND fetchedAt < :cutoff")
    void deleteOldUnbookmarked(long cutoff);
}
