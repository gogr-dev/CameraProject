package dev.gogrdev.cameraproject.TakePhotoActivity;

import androidx.annotation.Nullable;

import dev.gogrdev.cameraproject.data.CommentedPhoto;
import dev.gogrdev.cameraproject.data.CommentedPhotoDataSource;

public class TakePhotoPresenter implements TakePhotoContract.Presenter {

    private TakePhotoContract.View mView;
    private CommentedPhotoDataSource mModel;

    @Override
    public void startPresenter() {
        mView.setPresenter(this);
    }

    @Override
    public void setView(TakePhotoContract.View view) {
        mView = view;
    }

    @Override
    public void setModel(CommentedPhotoDataSource model) {
        mModel = model;
    }

    @Override
    public void notifyAddClicked() {
        mView.captureNewPhoto();
    }

    @Override
    public void notifyPictureCaptured(String photoPath, @Nullable Double latitude,
                                      @Nullable Double longitude, String comment) {
        CommentedPhoto photo = new CommentedPhoto();
        photo.setFilename(photoPath);
        photo.setLatitude(latitude);
        photo.setLongitude(longitude);
        photo.setComment(comment);
        photo.setTimestamp(System.currentTimeMillis());

        mModel.createPhoto(photo, new CommentedPhotoDataSource.CreatePhotoCallback() {
            @Override
            public void onPhotoCreated(int id) {
                mView.photoAdded(id);
            }

            @Override
            public void onPhotoCreateFail() {
                mView.showError("Failed to save photo");
            }
        });
    }
}
