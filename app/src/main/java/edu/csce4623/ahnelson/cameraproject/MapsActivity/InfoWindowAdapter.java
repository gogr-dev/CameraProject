package edu.csce4623.ahnelson.cameraproject.MapsActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import edu.csce4623.ahnelson.cameraproject.R;


public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;
    private Context mContext;

    public InfoWindowAdapter(Context context) {
        mContext = context;
        mWindow = LayoutInflater.from(context).inflate(R.layout.info_window, null);
    }

    private void windowText(Marker marker, View view){
        String imgName = marker.getTitle();
        TextView tvTitle = (TextView) view.findViewById(R.id.imgName);
        tvTitle.setText(imgName);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        windowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        windowText(marker, mWindow);
        return mWindow;
    }
}

