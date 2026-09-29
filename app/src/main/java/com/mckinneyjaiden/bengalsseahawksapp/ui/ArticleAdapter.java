package com.mckinneyjaiden.bengalsseahawksapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.mckinneyjaiden.bengalsseahawksapp.R;
import com.mckinneyjaiden.bengalsseahawksapp.data.local.ArticleEntity;

import java.text.DateFormat;
import java.util.Date;

public class ArticleAdapter extends ListAdapter<ArticleEntity, ArticleAdapter.ArticleViewHolder> {
    public interface Listener {
        void onOpen(ArticleEntity article);
        void onToggleBookmark(ArticleEntity article);
    }

    private static final DiffUtil.ItemCallback<ArticleEntity> DIFF =
            new DiffUtil.ItemCallback<ArticleEntity>() {
                @Override
                public boolean areItemsTheSame(@NonNull ArticleEntity oldItem,
                                               @NonNull ArticleEntity newItem) {
                    return oldItem.id.equals(newItem.id);
                }

                @Override
                public boolean areContentsTheSame(@NonNull ArticleEntity oldItem,
                                                  @NonNull ArticleEntity newItem) {
                    return oldItem.title.equals(newItem.title)
                            && oldItem.source.equals(newItem.source)
                            && oldItem.publishedAt == newItem.publishedAt
                            && oldItem.bookmarked == newItem.bookmarked;
                }
            };

    private final Listener listener;

    public ArticleAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_article, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ArticleViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView metadata;
        private final ImageButton bookmark;

        ArticleViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.article_title);
            metadata = itemView.findViewById(R.id.article_metadata);
            bookmark = itemView.findViewById(R.id.bookmark_button);
        }

        void bind(ArticleEntity article) {
            title.setText(article.title);
            String date = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(new Date(article.publishedAt));
            metadata.setText(itemView.getContext().getString(
                    R.string.article_metadata, article.source, date));
            bookmark.setImageResource(article.bookmarked
                    ? android.R.drawable.btn_star_big_on : android.R.drawable.btn_star_big_off);
            bookmark.setContentDescription(itemView.getContext().getString(
                    article.bookmarked ? R.string.remove_bookmark : R.string.add_bookmark));
            bookmark.setOnClickListener(view -> listener.onToggleBookmark(article));
            itemView.setOnClickListener(view -> listener.onOpen(article));
        }
    }
}
