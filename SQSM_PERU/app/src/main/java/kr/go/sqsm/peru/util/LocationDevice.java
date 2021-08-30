package kr.go.sqsm.peru.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class LocationDevice {
    private final String TAG = this.getClass().getSimpleName();

    private Context mContext;
    private LocationManager mLocationManager;

    private long minTime = 1000;
    private float minDistance = 1;

    private double lastLatitude = 0, lastLongitude = 0;
    private boolean isFromMockProvider = false;
    Location GPS,NETWORK;

    public boolean isFromMockProvider() {
        return isFromMockProvider;
    }

    public LocationDevice(Context context) {
        mContext = context;
        getLocation();

    }

    /**
     * Obtener ubicación actual
     */
    public Location get() {
        Location mLocation = new Location("GET");
        mLocation.setLongitude(lastLongitude);
        mLocation.setLatitude(lastLatitude);
        return mLocation;
    }

    /**
     * Guardar ubicación actual
     */
    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "You do not have PERMISSION to search for location information.");
        } else {
            if (mLocationManager == null) {
                mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, mLocationListener);
            }
            GPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            boolean isGPSEnable = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetWorkEnable = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (isGPSEnable) {
                if (mLocationManager != null) {
                    if (GPS != null) {
                        lastLatitude = GPS.getLatitude();
                        lastLongitude = GPS.getLongitude();
                    }
                }
            }

            if (isNetWorkEnable) {
                if (mLocationManager != null) {
                    mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, mLoc2ationListener);
                    NETWORK = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (NETWORK != null) {
                        lastLatitude = NETWORK.getLatitude();
                        lastLongitude = NETWORK.getLongitude();
                    }
                }
            }
        }
    }

    /**
     * GPS check
     */
    public boolean checkGPS() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * GPS Guardar ubicación actual Listener
     */
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            if(location.isFromMockProvider())
                isFromMockProvider = location.isFromMockProvider();
            else
                isFromMockProvider = false;
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
    };

    /**
     * Ubicación actual Y
     */
    public double getLastLatitude() {
        return lastLatitude;
    }

    /**
     * Ubicación actual X
     */
    public double getLastLongitude() {
        return lastLongitude;
    }

    /**
     * Network Guardar ubicación actual Listener
     */
    private final LocationListener mLoc2ationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            lastLatitude = location.getLatitude();
            lastLongitude = location.getLongitude();
            if(location.isFromMockProvider())
                isFromMockProvider = location.isFromMockProvider();
            else
                isFromMockProvider = false;
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
    };
}
