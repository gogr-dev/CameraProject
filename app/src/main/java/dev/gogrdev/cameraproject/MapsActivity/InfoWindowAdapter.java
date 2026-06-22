package dev.gogrdev.cameraproject.MapsActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import dev.gogrdev.cameraproject.R;
import dev.gogrdev.cameraproject.data.CommentedPhoto;


/**
 * Renders a marker's info window from the {@link CommentedPhoto} stored in its tag:
 * a thumbnail of the photo plus its comment, capture time and coordinates.
 */
public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static final int THUMB_SIZE = 200; // px

    private final View mWindow;

    public InfoWindowAdapter(Context context) {
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window, null);
    }

    private void render(Marker marker, View view) {
        TextView name = view.findViewById(R.id.imgName);
        TextView timestamp = view.findViewById(R.id.imgTimeStamp);
        TextView location = view.findViewById(R.id.imgLocation);
        ImageView photoView = view.findViewById(R.id.imgPhoto);

        Object tag = marker.getTag();
        if (!(tag instanceof CommentedPhoto)) {
            name.setText(marker.getTitle());
            timestamp.setText("");
            location.setText("");
            photoView.setImageResource(R.drawable.ic_launcher_foreground);
            return;
        }

        CommentedPhoto photo = (CommentedPhoto) tag;

        String comment = photo.getComment();
        name.setText(comment == null || comment.isEmpty() ? "(no comment)" : comment);

        if (photo.getTimestamp() != null) {
            timestamp.setText(DateFormat.getDateTimeInstance().format(new Date(photo.getTimestamp())));
        } else {
            timestamp.setText("");
        }

        if (photo.getLatitude() != null && photo.getLongitude() != null) {
            location.setText(String.format(Locale.US, "%.5f, %.5f",
                    photo.getLatitude(), photo.getLongitude()));
        } else {
            location.setText("");
        }

        Bitmap thumb = decodeSampledBitmap(photo.getFilename(), THUMB_SIZE, THUMB_SIZE);
        if (thumb != null) {
            photoView.setImageBitmap(thumb);
        } else {
            photoView.setImageResource(R.drawable.ic_launcher_foreground);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        // Returning null tells the map to use the whole window from getInfoWindow().
        return null;
    }

    /** Decode a (possibly large) image file down to roughly reqWidth x reqHeight. */
    private static Bitmap decodeSampledBitmap(String path, int reqWidth, int reqHeight) {
        if (path == null) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int inSampleSize = 1;
        int halfHeight = options.outHeight / 2;
        int halfWidth = options.outWidth / 2;
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = inSampleSize;
        return BitmapFactory.decodeFile(path, options);
    }
}
