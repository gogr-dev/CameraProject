package dev.gogrdev.cameraproject.TakePhotoActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import dev.gogrdev.cameraproject.R;
import dev.gogrdev.cameraproject.data.CommentedPhotoDataSource;
import dev.gogrdev.cameraproject.data.CommentedPhotoRepository;
import util.AppExecutors;

public class TakePhotoActivity extends AppCompatActivity {

    private TakePhotoContract.Presenter mPresenter;
    private TakePhotoContract.View mView;
    private CommentedPhotoDataSource mModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_photo_activity);
        mPresenter = new TakePhotoPresenter();
        mModel = CommentedPhotoRepository.getInstance(new AppExecutors(),getApplicationContext());
        mPresenter.setModel(mModel);
        mView = (TakePhotoContract.View) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
        mPresenter.setView(mView);
    }

    @Override
    protected void onStart(){
        super.onStart();
        mPresenter.startPresenter();
    }
}