package dev.gogrdev.cameraproject.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.List;

import util.AppExecutors;

/**
 * Single source of truth for CommentedPhotos. All database work runs on the disk-IO
 * executor; callbacks are delivered back on the main thread so the UI can use them
 * directly.
 */
public class CommentedPhotoRepository implements CommentedPhotoDataSource {

    private static final String TAG = "REPOSITORY";
    private static volatile CommentedPhotoRepository INSTANCE;

    private final CommentedPhotoDao commentedPhotoDao;
    private final AppExecutors mAppExecutors;

    private CommentedPhotoRepository(AppExecutors executors, Context context) {
        commentedPhotoDao = CommentedPhotoDatabase.getInstance(context).getCommentedPhotoDao();
        mAppExecutors = executors;
    }

    /**
     * Return the shared repository, creating it on first use (thread-safe).
     */
    public static CommentedPhotoRepository getInstance(@NonNull AppExecutors appExecutors,
                                                       @NonNull Context context) {
        if (INSTANCE == null) {
            synchronized (CommentedPhotoRepository.class) {
                if (INSTANCE == null) {
                    INSTANCE = new CommentedPhotoRepository(appExecutors, context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getPhotos(@NonNull LoadPhotosCallback callback) {
        mAppExecutors.diskIO().execute(() -> {
            final List<CommentedPhoto> photos = commentedPhotoDao.findAll();
            mAppExecutors.mainThread().execute(() -> {
                if (photos == null || photos.isEmpty()) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onPhotosLoaded(photos);
                }
            });
        });
    }

    @Override
    public void getPhoto(@NonNull Integer photoId, @NonNull GetPhotoCallback callback) {
        mAppExecutors.diskIO().execute(() -> {
            final CommentedPhoto photo = commentedPhotoDao.findById(photoId);
            mAppExecutors.mainThread().execute(() -> {
                if (photo == null) {
                    callback.onDataNotAvailable();
                } else {
                    callback.onPhotoLoaded(photo);
                }
            });
        });
    }

    @Override
    public void savePhoto(@NonNull CommentedPhoto photo) {
        mAppExecutors.diskIO().execute(() -> commentedPhotoDao.update(photo));
    }

    @Override
    public void createPhoto(@NonNull CommentedPhoto photo, @NonNull CreatePhotoCallback callback) {
        mAppExecutors.diskIO().execute(() -> {
            try {
                final long id = commentedPhotoDao.insert(photo);
                mAppExecutors.mainThread().execute(() -> callback.onPhotoCreated((int) id));
            } catch (Exception ex) {
                Log.e(TAG, "createPhoto failed", ex);
                mAppExecutors.mainThread().execute(callback::onPhotoCreateFail);
            }
        });
    }

    @Override
    public void deletePhoto(@NonNull Integer id, @NonNull DeletePhotoCallback callback) {
        mAppExecutors.diskIO().execute(() -> {
            try {
                commentedPhotoDao.delete(id);
                mAppExecutors.mainThread().execute(callback::onPhotoDeleted);
            } catch (Exception ex) {
                Log.e(TAG, "deletePhoto failed", ex);
                mAppExecutors.mainThread().execute(callback::onPhotoDeleteFailure);
            }
        });
    }
}
