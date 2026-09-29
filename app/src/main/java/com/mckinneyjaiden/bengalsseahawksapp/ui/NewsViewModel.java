package com.mckinneyjaiden.bengalsseahawksapp.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.mckinneyjaiden.bengalsseahawksapp.data.local.ArticleEntity;
import com.mckinneyjaiden.bengalsseahawksapp.data.local.NewsDatabase;
import com.mckinneyjaiden.bengalsseahawksapp.data.remote.FeedClient;
import com.mckinneyjaiden.bengalsseahawksapp.data.repository.NewsRepository;
import com.mckinneyjaiden.bengalsseahawksapp.model.Team;

import java.util.List;

public class NewsViewModel extends AndroidViewModel {
    public enum Filter { BENGALS, SEAHAWKS, SAVED }

    private final NewsRepository repository;
    private final MutableLiveData<Filter> filter = new MutableLiveData<>(Filter.BENGALS);
    private final MutableLiveData<Boolean> refreshing = new MutableLiveData<>(false);
    private final MutableLiveData<String> message = new MutableLiveData<>();
    private final LiveData<List<ArticleEntity>> articles;
    private boolean initialRefreshStarted;

    public NewsViewModel(@NonNull Application application) {
        super(application);
        repository = new NewsRepository(
                NewsDatabase.getInstance(application).articleDao(), new FeedClient());
        articles = Transformations.switchMap(filter, selected -> {
            if (selected == Filter.SAVED) {
                return repository.observeBookmarks();
            }
            return repository.observeTeam(toTeam(selected));
        });
    }

    public LiveData<List<ArticleEntity>> getArticles() {
        return articles;
    }

    public LiveData<Filter> getFilter() {
        return filter;
    }

    public LiveData<Boolean> getRefreshing() {
        return refreshing;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void selectFilter(Filter selected) {
        filter.setValue(selected);
    }

    public void refreshOnce() {
        if (!initialRefreshStarted) {
            initialRefreshStarted = true;
            refresh();
        }
    }

    public void refresh() {
        if (Boolean.TRUE.equals(refreshing.getValue())) {
            return;
        }
        Filter selected = filter.getValue();
        if (selected == Filter.SAVED) {
            message.setValue("Saved stories are available offline.");
            return;
        }
        refreshing.setValue(true);
        repository.refresh(toTeam(selected), error -> {
            refreshing.postValue(false);
            message.postValue(error == null ? "Feed updated." : error);
        });
    }

    public void toggleBookmark(ArticleEntity article) {
        repository.setBookmarked(article, !article.bookmarked);
    }

    public void clearMessage() {
        message.setValue(null);
    }

    private Team toTeam(Filter selected) {
        return selected == Filter.SEAHAWKS ? Team.SEAHAWKS : Team.BENGALS;
    }
}
