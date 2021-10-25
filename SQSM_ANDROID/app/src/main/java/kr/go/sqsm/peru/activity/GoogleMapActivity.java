package kr.go.sqsm.peru.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.servicedata.ClinicData;
import kr.go.sqsm.peru.util.BaseActivity;

public class GoogleMapActivity extends BaseActivity implements OnMapReadyCallback {
    public CameraPosition mCameraPosition;
    Toolbar toolbar;
    Button bt_location_regist, bt_location_close, bt_location_pharmacy;
    ImageView iv_location_marker;
    SupportMapFragment mSupportMapFragment;
    GoogleMap mMap;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        mLocationDevice.getLocation();

        latLng = new LatLng(mLocationDevice.getLastLatitude(), mLocationDevice.getLastLongitude());
//        latLng = new LatLng(-12.019068437579563, -76.98416339648438);
        bind();
        set();
    }

    @Override
    protected void bind() {
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        toolbar = findViewById(R.id.toolbar);
        bt_location_regist = findViewById(R.id.bt_location_regist);
        bt_location_close = findViewById(R.id.bt_location_close);
        iv_location_marker = findViewById(R.id.iv_location_marker);
        bt_location_pharmacy = findViewById(R.id.bt_location_pharmacy);
    }

    @Override
    protected void set() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mapUISetting();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    /**
     * Configuración UI(user interface) del mapa
     */
    private void mapUISetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
    }

    /**
     * POI Centro de salud
     * @param arrayList Centro de salud Data
     */
    public void setMarker(ArrayList<ClinicData> arrayList) {
        try {
            MarkerOptions makerOptions;
            for (ClinicData mData : arrayList) {
                latLng = new LatLng(
                        Double.parseDouble(mData.getMDLCNST_YDNTS()),
                        Double.parseDouble(mData.getMDLCNST_XCNTS())
                );
                makerOptions = new MarkerOptions();
                makerOptions.position(latLng)
                        .title(mData.getMDLCNST_NM())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.poi_hospital))
                        .snippet(mData.getMDLCNST_ADRES());

                mMap.addMarker(makerOptions);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Double parse Error -" + e);
        }
    }

    /**
     * POI(punto de interés) ubicación del paciente en cuarentena
     * @param x
     * @param y
     */
    public void qMarker(String x, String y) {
        try {
            LatLng latLng = new LatLng(Double.parseDouble(y), Double.parseDouble(x));
            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions.position(latLng)
                    .title(mContext.getString(R.string.self_contained_location))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.dot_pin));

            mMap.addMarker(makerOptions).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Double parse Error -" + e);
        }
    }
}
