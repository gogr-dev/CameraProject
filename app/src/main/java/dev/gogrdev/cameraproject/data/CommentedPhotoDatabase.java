package dev.gogrdev.cameraproject.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

// version 2: added the `timestamp` column. Destructive fallback is acceptable here —
// this is a personal photo journal, not critical data.
@Database(entities = {CommentedPhoto.class}, version = 2, exportSchema = false)
public abstract class CommentedPhotoDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "commentedphotos_db";
    private static volatile CommentedPhotoDatabase INSTANCE;

    public static CommentedPhotoDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CommentedPhotoDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    CommentedPhotoDatabase.class,
                                    DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract CommentedPhotoDao getCommentedPhotoDao();
}
