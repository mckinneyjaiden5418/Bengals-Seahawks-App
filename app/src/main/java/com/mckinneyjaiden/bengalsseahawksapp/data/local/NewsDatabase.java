package com.mckinneyjaiden.bengalsseahawksapp.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {ArticleEntity.class}, version = 1, exportSchema = false)
public abstract class NewsDatabase extends RoomDatabase {
    private static volatile NewsDatabase instance;

    public abstract ArticleDao articleDao();

    public static NewsDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (NewsDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            NewsDatabase.class,
                            "news.db"
                    ).build();
                }
            }
        }
        return instance;
    }
}
