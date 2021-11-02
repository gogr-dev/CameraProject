package edu.csce4623.ahnelson.cameraproject.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {CommentedPhoto.class}, version = 1, exportSchema = false)
public abstract class CommentedPhotoDatabase extends RoomDatabase {
    public static final String DATABASE_NAME = "commentedphotos_db";
    private static CommentedPhotoDatabase INSTANCE;

    public static CommentedPhotoDatabase getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = Room.databaseBuilder(context,CommentedPhotoDatabase.class,DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public abstract CommentedPhotoDao getCommentedPhotoDao();

}




