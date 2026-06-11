package dev.gogrdev.cameraproject.TakePhotoActivity;

import androidx.annotation.Nullable;

import dev.gogrdev.cameraproject.data.CommentedPhotoDataSource;

public interface TakePhotoContract {

    interface Presenter {
        void startPresenter();
        void setView(TakePhotoContract.View view);
        void setModel(CommentedPhotoDataSource model);
        void notifyAddClicked();

        /**
         * The user captured a photo and entered a comment.
         * @param latitude  where the photo was taken, or null if location was unavailable
         * @param longitude where the photo was taken, or null if location was unavailable
         */
        void notifyPictureCaptured(String photoPath, @Nullable Double latitude,
                                   @Nullable Double longitude, String comment);
    }

    interface View {
        void setPresenter(TakePhotoContract.Presenter presenter);
        void captureNewPhoto();
        void setPicture(String photoPath);
        void photoAdded(int id);
        void showError(String message);
    }
}
