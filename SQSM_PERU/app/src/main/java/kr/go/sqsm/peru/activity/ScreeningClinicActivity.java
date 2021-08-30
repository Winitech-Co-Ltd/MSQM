package kr.go.sqsm.peru.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.data.servicedata.ClinicData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScreeningClinicActivity extends GoogleMapActivity {
    ArrayList<ClinicData> clinicDataList;

    /**
     * Consulta de Centros de Salud  Callback
     */
    public Callback<RetrofitData> perupu0001_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getResults() != null) {
                        try {
                            JSONArray mJsonArray = new JSONArray(response.body().getResults().toString());
                            logE("mJsonArray - " + mJsonArray.toString());
                            if (mJsonArray.length() > 0) {
                                clinicDataList = new ArrayList<>();
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject mJsonObject = (JSONObject) mJsonArray.get(i);
                                    ClinicData clinicData = new ClinicData();
                                    clinicData.setMDLCNST_SN(jsonNullCheck(mJsonObject, "MDLCNST_SN"));
                                    clinicData.setMDLCNST_SE_CODE(jsonNullCheck(mJsonObject, "MDLCNST_SE_CODE"));
                                    clinicData.setMDLCNST_SE_CODE_NM(jsonNullCheck(mJsonObject, "MDLCNST_SE_CODE_NM"));
                                    clinicData.setMDLCNST_NM(jsonNullCheck(mJsonObject, "MDLCNST_NM"));
                                    clinicData.setMDLCNST_XCNTS(jsonNullCheck(mJsonObject, "MDLCNST_XCNTS"));
                                    clinicData.setMDLCNST_YDNTS(jsonNullCheck(mJsonObject, "MDLCNST_YDNTS"));
                                    clinicData.setMDLCNST_ADRES(jsonNullCheck(mJsonObject, "MDLCNST_ADRES"));
                                    clinicData.setCTTPC(jsonNullCheck(mJsonObject, "CTTPC"));

                                    clinicDataList.add(clinicData);
                                }

                                setMarker(clinicDataList);
                            } else {
                                makeToast("No hay Centros de Salud cercano a su alrededor. ");
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "perupu0001_callback JSON ERROR - " + e);
                        }
                    } else {
                        logE("Error Message - " + mContext.getString(R.string.network_error_message));
                    }
                } else {
                    makeToast(mContext.getString(R.string.not_data_message));
                }
            } else {
                makeToast(mContext.getString(R.string.network_error_message));
            }

        }

        @Override
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mLocationDevice.getLocation();
        retrofit.private_Service(mApplicationPERU.getHeader(), mRetrofitBody.PERUPU0001(
                String.valueOf(mLocationDevice.get().getLongitude()),
                String.valueOf(mLocationDevice.get().getLatitude())
        )).enqueue(perupu0001_callback);
    }

    @Override
    protected void set() {
        super.set();

        bt_location_pharmacy.setVisibility(View.VISIBLE);
        bt_location_close.setOnClickListener(mOnClickListener);
        bt_location_pharmacy.setOnClickListener(mOnClickListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //Acción cuando se presiona la tecla Atrás del Toolbar (barra de herramientas)
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_location_close:
                    finish();
                    break;
                case R.id.bt_location_pharmacy:
                    searchPharmacy();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Buscar farmacias basadas en las coordenadas del centro del mapa actual
     */
    private void searchPharmacy() {
        mCameraPosition = mMap.getCameraPosition();

        startActivity(new Intent(
                Intent.ACTION_VIEW,
//                            Uri.parse("https://www.google.co.kr/maps/search/farmacia/@" + "-12.046352,-77.042780" + ",17z") // Lima TEST
                Uri.parse("https://www.google.co.kr/maps/search/farmacia/@" + mCameraPosition.target.latitude + "," + mCameraPosition.target.longitude + ",17z")
        ));
    }
}
