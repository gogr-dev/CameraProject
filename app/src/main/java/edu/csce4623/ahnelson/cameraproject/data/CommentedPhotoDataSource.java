package edu.csce4623.ahnelson.cameraproject.data;

import androidx.annotation.NonNull;

import java.util.List;

public interface CommentedPhotoDataSource {
    /**
     * LoadMessagesCallback interface
     * Example of how to implement callback functions depending on the result of functions in interfaces
     * Currently, onDataNotAvailable is not implemented
     */
    interface LoadPhotosCallback {

        void onPhotosLoaded(List<CommentedPhoto> photos);
        void onDataNotAvailable();
    }

    /**
     * GetPhotoCallback interface
     */
    interface GetPhotoCallback {
        void onPhotoLoaded(CommentedPhoto photos);
        void onDataNotAvailable();
    }

    /**
     * CreatePhotoCallback interface
     * Return the id in createSuccessful
     */
    interface CreatePhotoCallback{
        void onPhotoCreated(int id);
        void onPhotoCreateFail();
    }

    /**
     * DeletePhotoCallback interface
     * Identify when a deletion is successful
     */
    interface DeletePhotoCallback{
        void onPhotoDeleted();
        void onPhotoDeleteFailure();
    }

    /**
     * getPhotos loads all Photos, calls either success or failure callback
     * @param callback - Callback function
     */
    void getPhotos(@NonNull LoadPhotosCallback callback);

    /**
     * getPhoto - Get a single Photo
     * @param PhotoId - String of the current PhotoID to be retrieved
     * @param callback - Callback function
     */
    void getPhoto(@NonNull Integer PhotoId, @NonNull GetPhotoCallback callback);

    /**
     * SavePhoto saves a CommentedPhoto to the database
     * @param photo - Photo to be updated
     */
    void savePhoto(@NonNull final CommentedPhoto photo);

    /**
     * CreatePhoto adds a CommentedPhoto to the database
     * @param photo - Photo to be added
     * @param callback - Callback function after thread completion
     */
    void createPhoto(@NonNull CommentedPhoto photo, @NonNull CreatePhotoCallback callback);


    /**
     * deletePhoto deletes a CommentedPhoto from the database
     * @param id
     */
    void deletePhoto(@NonNull Integer id, @NonNull DeletePhotoCallback callback);

}


