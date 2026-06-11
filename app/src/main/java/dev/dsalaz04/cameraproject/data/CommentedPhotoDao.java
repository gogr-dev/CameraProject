package dev.gogrdev.cameraproject.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;


@Dao
public interface CommentedPhotoDao {
    /**
     * Insert a CommentedPhoto into the table.
     * @return row ID for the newly inserted data
     */
    @Insert
    long insert(CommentedPhoto photo);

    /**
     * Select all CommentedPhotos.
     * @return every CommentedPhoto in the table
     */
    @Query("SELECT * FROM CommentedPhoto ORDER BY timestamp DESC")
    List<CommentedPhoto> findAll();

    /**
     * Select a single CommentedPhoto by ID.
     * @return the matching photo, or null if none exists
     */
    @Query("SELECT * FROM CommentedPhoto WHERE id = :id")
    CommentedPhoto findById(int id);

    /**
     * Delete a CommentedPhoto by ID.
     * @return the number of CommentedPhotos deleted
     */
    @Query("DELETE FROM CommentedPhoto WHERE id = :id")
    int delete(int id);

    /**
     * Update the CommentedPhoto.
     * @return the number of CommentedPhotos updated
     */
    @Update
    int update(CommentedPhoto photo);
}
