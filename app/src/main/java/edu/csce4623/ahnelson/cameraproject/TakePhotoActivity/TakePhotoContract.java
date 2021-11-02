package edu.csce4623.ahnelson.cameraproject.TakePhotoActivity;

import edu.csce4623.ahnelson.cameraproject.data.CommentedPhotoDataSource;

public interface TakePhotoContract {

    interface Presenter{
        void startPresenter();
        void setView(TakePhotoContract.View view);
        void notifyAddClicked();
        void setModel(CommentedPhotoDataSource model);
        void notifyPictureCaptured(String photoPath);
    }

    interface View{
        void setPresenter(TakePhotoContract.Presenter presenter);
        void captureNewPhoto();
        void setPicture(String photoPath);
        void photoAdded(int id);
    }
}
