package edu.csce4623.ahnelson.cameraproject.data;

import android.database.Cursor;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;


@Dao
public interface CommentedPhotoDao {
    /**
     * Insert a CommentedPhoto into the table
     * @return row ID for newly inserted data
     */
    @Insert
    long insert(CommentedPhoto photo);

    /**
     * select all CommentedPhotos
     * @return A {@link Cursor} of all commentedPhotos in the table
     */
    @Query("SELECT * FROM CommentedPhoto")
    Cursor findAll();

    /**
     * Delete a CommentedPhoto by ID
     * @return A number of CommentedPhotos deleted
     */
    @Query("DELETE FROM CommentedPhoto WHERE id = :id ")
    int delete(long id);

    /**
     * Update the CommentedPhoto
     * @return A number of CommentedPhotos updated
     */
    @Update
    int update(CommentedPhoto photo);
}


