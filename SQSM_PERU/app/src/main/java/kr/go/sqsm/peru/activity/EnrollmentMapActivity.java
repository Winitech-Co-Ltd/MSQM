package kr.go.sqsm.peru.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.servicedata.LocationData;

public class EnrollmentMapActivity extends GoogleMapActivity {
    LocationData mLocationData;
    boolean isUser;

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_location_regist:
                    mCameraPosition = mMap.getCameraPosition();

                    mLocationData.setISLLC_XCNTS(String.valueOf(mCameraPosition.target.longitude));
                    mLocationData.setISLLC_YDNTS(String.valueOf(mCameraPosition.target.latitude));
                    saveDialog(mContext.getString(R.string.add_location), mContext.getString(R.string.location_search));
                    break;

                case R.id.bt_location_close:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationData = new LocationData();
    }

    @Override
    protected void set() {
        super.set();
        bt_location_regist.setOnClickListener(mOnClickListener);
        bt_location_close.setOnClickListener(mOnClickListener);

        if (getIntent() != null) {
            isUser = getIntent().getExtras().getBoolean("isUser");
        }

        if (!isUser) {
            iv_location_marker.setVisibility(View.VISIBLE);
            bt_location_regist.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        super.onMapReady(googleMap);

        if (isUser) {
            if (getIntent() != null) {
                qMarker(getIntent().getExtras().getString("locationX", "0.0"),
                        getIntent().getExtras().getString("locationY", "0.0"));
            }
        }
    }

    /**
     * Cuadro de diálogo para guardar ubicación
     * @param title
     * @param msg
     */
    private void saveDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getString(R.string.check), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setResult(RESULT_OK, new Intent().putExtra("location", mLocationData));
                finish();
            }
        });
        builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
