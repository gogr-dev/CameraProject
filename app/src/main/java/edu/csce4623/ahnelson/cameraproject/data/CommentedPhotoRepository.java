package edu.csce4623.ahnelson.cameraproject.data;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import util.AppExecutors;

public class CommentedPhotoRepository implements CommentedPhotoDataSource{

    public static volatile CommentedPhotoRepository INSTANCE;
    CommentedPhotoDao commentedPhotoDao;
    AppExecutors mAppExecutors;



    private CommentedPhotoRepository(AppExecutors executors,Context context){
        commentedPhotoDao = CommentedPhotoDatabase.getInstance(context).getCommentedPhotoDao();
        mAppExecutors = executors;
    }

    /**
     * public constructor - prevent creation of instance if one already exists
     * @param appExecutors
     * @param context
     * @return
     */
    public static CommentedPhotoRepository getInstance(@NonNull AppExecutors appExecutors, @NonNull Context context){
        if(INSTANCE == null){
            synchronized (CommentedPhotoRepository.class){
                if(INSTANCE == null){
                    INSTANCE = new CommentedPhotoRepository(appExecutors, context);
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public void getPhotos(@NonNull LoadPhotosCallback callback) {

    }

    @Override
    public void getPhoto(@NonNull Integer PhotoId, @NonNull GetPhotoCallback callback) {

    }

    @Override
    public void savePhoto(@NonNull CommentedPhoto photo) {

    }

    @Override
    public void createPhoto(@NonNull CommentedPhoto photo, @NonNull CreatePhotoCallback callback) {
        Log.d("REPOSITORY","Deleting...");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long id = commentedPhotoDao.insert(photo);
                mAppExecutors.mainThread().execute(new Runnable(){
                    @Override
                    public void run() {
                        callback.onPhotoCreated((int)id);
                    }
                });
            }
        };
        mAppExecutors.diskIO().execute(runnable);

    }

    @Override
    public void deletePhoto(@NonNull Integer id, @NonNull DeletePhotoCallback callback) {
        Log.d("REPOSITORY","Deleting...");
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    int count = commentedPhotoDao.delete(id);
                    callback.onPhotoDeleted();
                }catch (Exception ex){
                    Log.e("REPOSITORY",ex.toString());
                    callback.onPhotoDeleteFailure();
                }
            }
        };
        mAppExecutors.diskIO().execute(runnable);
    }
}
