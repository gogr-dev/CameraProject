package edu.csce4623.ahnelson.cameraproject.TakePhotoActivity;

import edu.csce4623.ahnelson.cameraproject.data.CommentedPhoto;
import edu.csce4623.ahnelson.cameraproject.data.CommentedPhotoDataSource;

public class TakePhotoPresenter implements TakePhotoContract.Presenter{

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
    public void notifyAddClicked() {
        mView.captureNewPhoto();
    }

    @Override
    public void setModel(CommentedPhotoDataSource model) {
        mModel = model;
    }

    @Override
    public void notifyPictureCaptured(String photoPath) {
        CommentedPhoto photo = new CommentedPhoto();
        photo.setFilename(photoPath);
        photo.setLatitude(36.0709448);
        photo.setLongitude(-94.1820971);
        photo.setComment("Photo!");
        mModel.createPhoto(photo, new CommentedPhotoDataSource.CreatePhotoCallback() {
            @Override
            public void onPhotoCreated(int id) {
                mView.photoAdded(id);
            }

            @Override
            public void onPhotoCreateFail() {
                return;
            }
        });
        mView.setPicture(photoPath);
    }
}
