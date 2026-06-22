package dev.gogrdev.cameraproject.TakePhotoActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dev.gogrdev.cameraproject.R;

/**
 * Captures a photo with the device camera, asks the user for a comment, tags it with the
 * current location, and hands it to the presenter to be saved.
 */
public class TakePhotoFragmentView extends Fragment implements TakePhotoContract.View {

    private static final String AUTHORITY = "dev.gogrdev.cameraproject.fileprovider";

    private TakePhotoContract.Presenter mPresenter;
    private ActivityResultLauncher<Intent> mCameraLauncher;
    private FusedLocationProviderClient mFusedLocationClient;
    private String currentPhotoPath;
    private ImageView imageView;

    public TakePhotoFragmentView() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext());
        mCameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && currentPhotoPath != null) {
                        setPicture(currentPhotoPath);
                        fetchLocationThenPromptComment();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_take_photo_view, container, false);
        FloatingActionButton fabTakeNewPhoto = root.findViewById(R.id.fabTakePhoto);
        fabTakeNewPhoto.setOnClickListener(view -> mPresenter.notifyAddClicked());
        imageView = root.findViewById(R.id.imageView);
        return root;
    }

    @Override
    public void setPresenter(TakePhotoContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void captureNewPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) == null) {
            showError("No camera app available");
            return;
        }
        File photoFile;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {
            showError("Could not create image file");
            return;
        }
        Uri photoURI = FileProvider.getUriForFile(requireContext(), AUTHORITY, photoFile);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        mCameraLauncher.launch(takePictureIntent);
    }

    /** Look up the last known location (if permitted), then ask for a comment. */
    private void fetchLocationThenPromptComment() {
        // Inline permission check so lint's MissingPermission analysis recognizes the guard.
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(requireActivity(), this::promptForComment)
                    .addOnFailureListener(e -> promptForComment(null));
        } else {
            promptForComment(null);
        }
    }

    private void promptForComment(@Nullable Location location) {
        EditText input = new EditText(requireContext());
        input.setHint(R.string.comment_hint);
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.add_comment_title)
                .setView(input)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String comment = input.getText().toString().trim();
                    Double lat = location != null ? location.getLatitude() : null;
                    Double lng = location != null ? location.getLongitude() : null;
                    mPresenter.notifyPictureCaptured(currentPhotoPath, lat, lng, comment);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public void setPicture(String photoPath) {
        // Decode the image scaled to roughly the ImageView size to avoid OOM on large photos.
        int targetW = Math.max(1, imageView.getWidth());
        int targetH = Math.max(1, imageView.getHeight());

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
    }

    @Override
    public void photoAdded(int id) {
        Toast.makeText(getActivity(), getString(R.string.photo_saved, id), Toast.LENGTH_LONG).show();
        // Return to the map, which reloads its markers in onResume.
        requireActivity().finish();
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
