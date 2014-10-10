package de.ozhan.burak.gpstester;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class APPLocationManager implements LocationListener{
    private static final String TAG = APPLocationManager.class.getName();

    private Context context;
    private GPStester gpstester;
    private Location lastLocation;
    static APPLocationManager instance = new APPLocationManager();

    private static LocationManager lm;
    private static boolean gpsInUse = false;

    public static APPLocationManager getInstance() {
        return instance;
    }

    private APPLocationManager() {}


    public void setContext(Context context, GPStester gpstester){

        this.context = context;
        this.gpstester = gpstester;
    }

    public void startUpdatingLocation() {
        Log.v("startUpdatingLocation", "has been called");
        if(context != null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            lm.addNmeaListener(gpstester);
        }
    }

    public void stopLocationUpdates() {
        if(context != null) {
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            lm.removeUpdates(this);
            lastLocation = null;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "got a location from "+location.getProvider()+" provider.");
        this.lastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public Location getLastLocation() {
        return lastLocation;
    }




}
