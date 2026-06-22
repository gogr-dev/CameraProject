package dev.gogrdev.cameraproject.MapsActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import dev.gogrdev.cameraproject.R;
import dev.gogrdev.cameraproject.TakePhotoActivity.TakePhotoActivity;
import dev.gogrdev.cameraproject.data.CommentedPhoto;
import dev.gogrdev.cameraproject.data.CommentedPhotoDataSource;
import dev.gogrdev.cameraproject.data.CommentedPhotoRepository;
import dev.gogrdev.cameraproject.databinding.ActivityMapsBinding;
import util.AppExecutors;

/**
 * Shows every saved photo as a marker on a Google Map. Tapping a marker opens an info
 * window with the photo and its comment; tapping the window offers to delete it. A
 * floating button launches {@link TakePhotoActivity} to add a new one.
 */
public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private static final String TAG = "MapsActivity";
    private static final float DEFAULT_ZOOM = 14f;

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    private CommentedPhotoDataSource mModel;

    private final ActivityResultLauncher<String[]> locationPermissionRequest =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    result -> {
                        Boolean fine = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                        Boolean coarse = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                        if ((fine != null && fine) || (coarse != null && coarse)) {
                            enableMyLocation();
                        } else {
                            Log.d(TAG, "Location permission not granted");
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mModel = CommentedPhotoRepository.getInstance(new AppExecutors(), getApplicationContext());

        binding.fabAddPhoto.setOnClickListener(v ->
                startActivity(new Intent(this, TakePhotoActivity.class)));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new InfoWindowAdapter(this));
        mMap.setOnInfoWindowClickListener(this);
        if (hasLocationPermission()) {
            enableMyLocation();
        }
        loadPhotos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh markers when returning from the camera screen.
        if (mMap != null) {
            loadPhotos();
        }
    }

    private void loadPhotos() {
        mModel.getPhotos(new CommentedPhotoDataSource.LoadPhotosCallback() {
            @Override
            public void onPhotosLoaded(List<CommentedPhoto> photos) {
                if (mMap == null) {
                    return;
                }
                mMap.clear();
                for (CommentedPhoto photo : photos) {
                    if (photo.getLatitude() == null || photo.getLongitude() == null) {
                        continue;
                    }
                    LatLng position = new LatLng(photo.getLatitude(), photo.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions()
                            .position(position)
                            .title(photo.getComment()));
                    if (marker != null) {
                        marker.setTag(photo);
                    }
                }
            }

            @Override
            public void onDataNotAvailable() {
                if (mMap != null) {
                    mMap.clear();
                }
            }
        });
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Object tag = marker.getTag();
        if (!(tag instanceof CommentedPhoto)) {
            return;
        }
        CommentedPhoto photo = (CommentedPhoto) tag;
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_photo_title)
                .setMessage(R.string.delete_photo_message)
                .setPositiveButton(R.string.delete, (dialog, which) -> deletePhoto(photo))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void deletePhoto(CommentedPhoto photo) {
        mModel.deletePhoto(photo.getId(), new CommentedPhotoDataSource.DeletePhotoCallback() {
            @Override
            public void onPhotoDeleted() {
                Toast.makeText(MapsActivity.this, R.string.photo_deleted, Toast.LENGTH_SHORT).show();
                loadPhotos();
            }

            @Override
            public void onPhotoDeleteFailure() {
                Toast.makeText(MapsActivity.this, R.string.photo_delete_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void enableMyLocation() {
        // Permission check is inlined (not via hasLocationPermission()) so lint's
        // MissingPermission dataflow analysis can verify the guard.
        if (mMap == null
                || (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null && mMap != null) {
                    LatLng here = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(here, DEFAULT_ZOOM));
                }
            });
        } catch (SecurityException e) {
            Log.e(TAG, "enableMyLocation", e);
        }
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }
}
