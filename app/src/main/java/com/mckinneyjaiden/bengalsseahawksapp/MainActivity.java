package com.mckinneyjaiden.bengalsseahawksapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;
import com.mckinneyjaiden.bengalsseahawksapp.data.local.ArticleEntity;
import com.mckinneyjaiden.bengalsseahawksapp.model.ArticleLinks;
import com.mckinneyjaiden.bengalsseahawksapp.ui.ArticleAdapter;
import com.mckinneyjaiden.bengalsseahawksapp.ui.NewsViewModel;


public class MainActivity extends AppCompatActivity implements ArticleAdapter.Listener {
    private NewsViewModel viewModel;
    private TextView emptyMessage;
    private ProgressBar loading;
    private ImageButton refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        applySystemBarInsets();

        viewModel = new ViewModelProvider(this).get(NewsViewModel.class);
        emptyMessage = findViewById(R.id.empty_message);
        loading = findViewById(R.id.loading);
        refreshButton = findViewById(R.id.refresh_button);

        ArticleAdapter adapter = new ArticleAdapter(this);
        RecyclerView articleList = findViewById(R.id.article_list);
        articleList.setAdapter(adapter);

        MaterialButtonToggleGroup filters = findViewById(R.id.team_filters);
        filters.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            NewsViewModel.Filter filter;
            if (checkedId == R.id.filter_seahawks) {
                filter = NewsViewModel.Filter.SEAHAWKS;
            } else if (checkedId == R.id.filter_saved) {
                filter = NewsViewModel.Filter.SAVED;
            } else {
                filter = NewsViewModel.Filter.BENGALS;
            }
            viewModel.selectFilter(filter);
            viewModel.refresh();
        });
        refreshButton.setOnClickListener(view -> viewModel.refresh());

        viewModel.getArticles().observe(this, articles -> {
            adapter.submitList(articles);
            boolean empty = articles == null || articles.isEmpty();
            emptyMessage.setVisibility(empty ? View.VISIBLE : View.GONE);
            articleList.setVisibility(empty ? View.GONE : View.VISIBLE);
            NewsViewModel.Filter filter = viewModel.getFilter().getValue();
            emptyMessage.setText(filter == NewsViewModel.Filter.SAVED
                    ? R.string.empty_saved : R.string.empty_feed);
        });
        viewModel.getRefreshing().observe(this, refreshing -> {
            boolean active = Boolean.TRUE.equals(refreshing);
            loading.setVisibility(active ? View.VISIBLE : View.GONE);
            refreshButton.setEnabled(!active);
        });
        viewModel.getMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG).show();
                viewModel.clearMessage();
            }
        });
        viewModel.refreshOnce();
    }

    @Override
    public void onOpen(ArticleEntity article) {
        Uri uri = Uri.parse(article.url);
        if (!ArticleLinks.isWebUrl(article.url)) {
            showMessage(getString(R.string.invalid_article_link));
            return;
        }
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (ActivityNotFoundException exception) {
            showMessage(getString(R.string.invalid_article_link));
        }
    }

    @Override
    public void onToggleBookmark(ArticleEntity article) {
        viewModel.toggleBookmark(article);
    }

    private void applySystemBarInsets() {
        View root = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(root, (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    private void showMessage(String message) {
        Snackbar.make(findViewById(R.id.main), message, Snackbar.LENGTH_LONG).show();
    }
}
