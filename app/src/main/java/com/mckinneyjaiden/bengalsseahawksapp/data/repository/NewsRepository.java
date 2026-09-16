package com.mckinneyjaiden.bengalsseahawksapp.data.repository;

import androidx.lifecycle.LiveData;

import com.mckinneyjaiden.bengalsseahawksapp.data.local.ArticleDao;
import com.mckinneyjaiden.bengalsseahawksapp.data.local.ArticleEntity;
import com.mckinneyjaiden.bengalsseahawksapp.data.remote.FeedClient;
import com.mckinneyjaiden.bengalsseahawksapp.model.Team;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsRepository {
    private static final long CACHE_RETENTION_MS = 14L * 24 * 60 * 60 * 1000;

    public interface RefreshCallback {
        void onComplete(String errorMessage);
    }

    private final ArticleDao articleDao;
    private final FeedClient feedClient;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public NewsRepository(ArticleDao articleDao, FeedClient feedClient) {
        this.articleDao = articleDao;
        this.feedClient = feedClient;
    }

    public LiveData<List<ArticleEntity>> observeTeam(Team team) {
        return articleDao.observeTeam(team.name());
    }

    public LiveData<List<ArticleEntity>> observeBookmarks() {
        return articleDao.observeBookmarks();
    }

    public void refresh(Team team, RefreshCallback callback) {
        executor.execute(() -> {
            String error = null;
            try {
                long fetchedAt = System.currentTimeMillis();
                for (ArticleEntity article : feedClient.fetch(team)) {
                    articleDao.insertIfNew(article);
                    articleDao.updateRemoteFields(article.id, article.title, article.source,
                            article.publishedAt, article.fetchedAt);
                }
                articleDao.deleteOldUnbookmarked(fetchedAt - CACHE_RETENTION_MS);
            } catch (Exception exception) {
                error = exception.getMessage() == null
                        ? "Unable to refresh the feed." : exception.getMessage();
            }
            callback.onComplete(error);
        });
    }

    public void setBookmarked(ArticleEntity article, boolean bookmarked) {
        executor.execute(() -> articleDao.setBookmarked(article.id, bookmarked));
    }
}
