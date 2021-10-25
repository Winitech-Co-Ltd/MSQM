package kr.go.sqsmo.peru.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.data.RetrofitData;
import kr.go.sqsmo.peru.data.servicedata.InsulatorData;
import kr.go.sqsmo.peru.data.servicedata.LocationData;
import kr.go.sqsmo.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoogleMapActivity extends BaseActivity implements OnMapReadyCallback {
    public CameraPosition mCameraPosition;

    /**
     * Peru Lima
     */
    LatLng ll = new LatLng(-12.046371, -77.042754);

    GoogleMap mMap;
    SupportMapFragment mSupportMapFragment;
    Toolbar toolbar;
    Button bt_location_regist, bt_location_modify, bt_location_cancel, bt_location_close, bt_location_copy;
    ImageView iv_location_marker;
    InsulatorData mInsulatorData;
    LocationData mLocationData;
    Button bt_jujeonbu, bt_sijeongu, bt_gucheong; // 다이얼로그 Button
    AlertDialog codeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);

        bind();
        set();
    }

    @Override
    protected void bind() {
        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.google_map);
        toolbar = findViewById(R.id.toolbar);
        bt_location_regist = findViewById(R.id.bt_location_regist);
        bt_location_cancel = findViewById(R.id.bt_location_cancel);
        bt_location_modify = findViewById(R.id.bt_location_modify);
        bt_location_close = findViewById(R.id.bt_location_close);
        iv_location_marker = findViewById(R.id.iv_location_marker);
        bt_location_copy = findViewById(R.id.bt_location_copy);
    }

    @Override
    protected void set() {
        bt_location_regist.setOnClickListener(mOnClickListener);
        bt_location_cancel.setOnClickListener(mOnClickListener);
        bt_location_modify.setOnClickListener(mOnClickListener);
        bt_location_close.setOnClickListener(mOnClickListener);
        bt_location_copy.setOnClickListener(mOnClickListener);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(this);
        }

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_location_regist:
                    locationRegist();
                    break;
                case R.id.bt_location_cancel:
                    saveCancel();
                    break;
                case R.id.bt_location_modify:
                    modify();
                    break;
                case R.id.bt_location_close:
                    finish();
                    break;
                case R.id.bt_location_copy:
                    locationCopy();
                    break;
                case R.id.bt_jujeonbu:
                    bt_jujeonbu.setText("");
                    bt_sijeongu.setText("");
                    bt_gucheong.setText("");

                    mInsulatorData.setISLLC_DPRTMNT_CODE("");
                    mInsulatorData.setISLLC_PRVNCA_CODE("");
                    mInsulatorData.setISLLC_DSTRT_CODE("");
                    retrofit.peruo_Service(mApplicationPERUO.getHeader(),mRetrofitBody.PERUC0002()).enqueue(sqsmc0002_callback);
                    break;
                case R.id.bt_sijeongu:
                    if (!"".equals(mInsulatorData.getISLLC_DPRTMNT_CODE())) {
                        bt_sijeongu.setText("");
                        bt_gucheong.setText("");
                        mInsulatorData.setISLLC_PRVNCA_CODE("");
                        mInsulatorData.setISLLC_DSTRT_CODE("");
                        retrofit.peruo_Service(mApplicationPERUO.getHeader(),mRetrofitBody.PERUC0003(mInsulatorData.getISLLC_DPRTMNT_CODE())).enqueue(sqsmc0003_callback);
                    } else {
                        makeToast(mContext.getString(R.string.region_not_message));
                    }
                    break;
                case R.id.bt_gucheong:
                    if (!"".equals(mInsulatorData.getISLLC_DPRTMNT_CODE())) {
                        if (!"".equals(mInsulatorData.getISLLC_PRVNCA_CODE())) {
                            bt_gucheong.setText("");
                            mInsulatorData.setISLLC_DSTRT_CODE("");
                            retrofit.peruo_Service(mApplicationPERUO.getHeader(),mRetrofitBody.PERUC0004(mInsulatorData.getISLLC_DPRTMNT_CODE(), mInsulatorData.getISLLC_PRVNCA_CODE())).enqueue(sqsmc0004_callback);
                        } else {
                            makeToast(mContext.getString(R.string.provincia_not_message));
                        }
                    } else {
                        makeToast(mContext.getString(R.string.region_not_message));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Guardar valores de coordenadas en el centro del mapa
     */
    private void locationRegist() {
        mCameraPosition = mMap.getCameraPosition();
        mLocationData = new LocationData();

        mLocationData.setISLLC_XCNTS(String.valueOf(mCameraPosition.target.longitude));
        mLocationData.setISLLC_YDNTS(String.valueOf(mCameraPosition.target.latitude));

        addrDialog();
    }

    /**
     * Uso de ClipboardManager
     * Copiar y transferir coordenadas de ubicación al Clipboard
     */
    private void locationCopy() {
        if (mInsulatorData.getISLPRSN_YDNTS() != null && !"".equals(mInsulatorData.getISLPRSN_YDNTS())
                && mInsulatorData.getISLPRSN_XCNTS() != null && !"".equals(mInsulatorData.getISLPRSN_XCNTS())) {
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("locationYX", mInsulatorData.getISLPRSN_YDNTS() + "," + mInsulatorData.getISLPRSN_XCNTS());
            if (clipboardManager != null) {
                clipboardManager.setPrimaryClip(clipData);
                makeToast(mContext.getString(R.string.location_copy_correct));
            }
        } else {
            makeToast(mContext.getString(R.string.location_copy_not));
        }
    }

    private void modify() {
        bt_location_modify.setVisibility(View.INVISIBLE);
        bt_location_close.setVisibility(View.INVISIBLE);
        iv_location_marker.setVisibility(View.VISIBLE);
        bt_location_regist.setVisibility(View.VISIBLE);
        bt_location_cancel.setVisibility(View.VISIBLE);
    }

    private void saveCancel() {
        bt_location_modify.setVisibility(View.VISIBLE);
        bt_location_close.setVisibility(View.VISIBLE);
        iv_location_marker.setVisibility(View.INVISIBLE);
        bt_location_regist.setVisibility(View.INVISIBLE);
        bt_location_cancel.setVisibility(View.INVISIBLE);
    }

    /**
     * Modificar información de la persona en autocuarentena Callback
     */
    public Callback<RetrofitData> peru0005_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getResults() != null) {
                        try {
                            JSONArray array = new JSONArray(response.body().getResults().toString());
                            JSONObject mJsonObject = (JSONObject) array.get(array.length() - 1);
                            mInsulatorData.setISLPRSN_SN(jsonNullCheck(mJsonObject, "ISLPRSN_SN"));
                            mInsulatorData.setISLPRSN_NM(jsonNullCheck(mJsonObject, "ISLPRSN_NM"));
                            mInsulatorData.setSEXDSTN_CODE(jsonNullCheck(mJsonObject, "SEXDSTN"));
                            mInsulatorData.setSEXDSTN_CODE_NM(jsonNullCheck(mJsonObject, "SEXDSTN_CODE"));
                            mInsulatorData.setISLLC_XCNTS(jsonNullCheck(mJsonObject, "ISLLC_XCNTS"));
                            mInsulatorData.setISLLC_YDNTS(jsonNullCheck(mJsonObject, "ISLLC_YDNTS"));
                            mInsulatorData.setTELNO(jsonNullCheck(mJsonObject, "TELNO"));
                            mInsulatorData.setEMGNC_TELNO(jsonNullCheck(mJsonObject, "EMGNC_TELNO"));
                            mInsulatorData.setDISTANCE(jsonNullCheck(mJsonObject, "DISTANCE"));
                            mInsulatorData.setPSPRNBR(jsonNullCheck(mJsonObject, "PSPRNBR"));
                            mInsulatorData.setLC_TRNSMIS_USE_AT(jsonNullCheck(mJsonObject, "LC_TRNSMIS_USE_AT"));
                            mInsulatorData.setISLLC_DPRTMNT_CODE(jsonNullCheck(mJsonObject, "ISLLC_DPRTMNT_CODE"));
                            mInsulatorData.setISLLC_DPRTMNT_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_DPRTMNT_CODE_NM"));
                            mInsulatorData.setISLLC_PRVNCA_CODE(jsonNullCheck(mJsonObject, "ISLLC_PRVNCA_CODE"));
                            mInsulatorData.setISLLC_PRVNCA_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_PRVNCA_CODE_NM"));
                            mInsulatorData.setISLLC_DSTRT_CODE(jsonNullCheck(mJsonObject, "ISLLC_DSTRT_CODE"));
                            mInsulatorData.setISLLC_DSTRT_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_DSTRT_CODE_NM"));
                            mInsulatorData.setISLLC_ETC_ADRES(jsonNullCheck(mJsonObject, "ISLLC_ETC_ADRES"));
                            mInsulatorData.setINHT_ID(jsonNullCheck(mJsonObject, "INHT_ID"));
                        } catch (JSONException e) {
                            Log.e(TAG, "peru0005_callback JSON Error - " + e);
                        }
                        makeToast(mContext.getString(R.string.save_location_success));
                        saveCancel();

                        mMap.clear();
                        qMarker(mInsulatorData);
                        cMarker(mInsulatorData);
//                        if (quarantineLocation != null && currentLocation != null) {
//                            goMid();
//                        }
                    } else {
                        logE("peru0005_callback Message - " + response.body().getRes_msg());
                        makeToast(response.body().getRes_msg());
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

    /**
     * Region Callback
     */
    public Callback<RetrofitData> sqsmc0002_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            try {
                                ArrayList<HashMap<String, String>> statusCode = new ArrayList<>();
                                JSONArray array = new JSONArray(response.body().getResults().toString());
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject mJsonObject = (JSONObject) array.get(i);
                                    String name = (String) jsonNullCheck(mJsonObject, "CODE_NM");
                                    String code = (String) jsonNullCheck(mJsonObject, "CODE");
                                    if (!"".equals(code)) {
                                        HashMap<String, String> tempMap = new HashMap<>();
                                        tempMap.put(name, code);
                                        statusCode.add(tempMap);
                                    }
                                }
                                codeDialog("sqsmc0002", statusCode);
                            } catch (JSONException e) {
                                Log.e(TAG, "sqsmc0002_callback JSON ERROR - " + e);
                                makeToast(mContext.getString(R.string.fail_data_message));
                            }
                        } else {
                            makeToast(mContext.getString(R.string.fail_data_message));
                        }
                    } else {
                        makeToast(mContext.getString(R.string.network_error_message));
                    }
                } else {
                    makeToast(mContext.getString(R.string.network_error_message));
                }
            } else {
                makeToast(mContext.getString(R.string.not_data_message));
            }
        }

        @Override
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    /**
     * Provincia Callback
     */
    public Callback<RetrofitData> sqsmc0003_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            try {
                                ArrayList<HashMap<String, String>> statusCode = new ArrayList<>();
                                JSONArray array = new JSONArray(response.body().getResults().toString());
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject mJsonObject = (JSONObject) array.get(i);
                                    String name = (String) jsonNullCheck(mJsonObject, "CODE_NM");
                                    String code = (String) jsonNullCheck(mJsonObject, "CODE");
                                    if (!"".equals(code)) {
                                        HashMap<String, String> tempMap = new HashMap<>();
                                        tempMap.put(name, code);
                                        statusCode.add(tempMap);
                                    }
                                }
                                codeDialog("sqsmc0003", statusCode);
                            } catch (JSONException e) {
                                Log.e(TAG, "sqsmc0003_callback JSON ERROR - " + e);
                                makeToast(mContext.getString(R.string.fail_data_message));
                            }
                        } else {
                            makeToast(mContext.getString(R.string.fail_data_message));
                        }
                    } else {
                        makeToast(mContext.getString(R.string.network_error_message));
                    }
                } else {
                    makeToast(mContext.getString(R.string.network_error_message));
                }
            } else {
                makeToast(mContext.getString(R.string.not_data_message));
            }
        }

        @Override
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    /**
     * Distrito Callback
     */
    public Callback<RetrofitData> sqsmc0004_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            try {
                                ArrayList<HashMap<String, String>> statusCode = new ArrayList<>();
                                JSONArray array = new JSONArray(response.body().getResults().toString());
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject mJsonObject = (JSONObject) array.get(i);
                                    String name = (String) jsonNullCheck(mJsonObject, "CODE_NM");
                                    String code = (String) jsonNullCheck(mJsonObject, "CODE");
                                    if (!"".equals(code)) {
                                        HashMap<String, String> tempMap = new HashMap<>();
                                        tempMap.put(name, code);
                                        statusCode.add(tempMap);
                                    }
                                }
                                codeDialog("sqsmc0004", statusCode);
                            } catch (JSONException e) {
                                Log.e(TAG, "sqsmc0004_callback JSON ERROR - " + e);
                                makeToast(mContext.getString(R.string.fail_data_message));
                            }
                        } else {
                            makeToast(mContext.getString(R.string.fail_data_message));
                        }
                    } else {
                        makeToast(mContext.getString(R.string.network_error_message));
                    }
                } else {
                    makeToast(mContext.getString(R.string.network_error_message));
                }
            } else {
                makeToast(mContext.getString(R.string.not_data_message));
            }
        }

        @Override
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    /**
     * Cuadro de diálogo para guardar ubicación
     */
    private void saveDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(mContext.getString(R.string.add_location));
        builder.setMessage(mContext.getString(R.string.location_search));
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                menuDialog.dismiss();
                retrofit.peruo_Service(mApplicationPERUO.getHeader(),mRetrofitBody.PERU0005(
                        mLocationData,
                        mInsulatorData
                )).enqueue(peru0005_callback);
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

    /**
     * Llamar cuando esté listo para abrir Google Maps
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mapUISetting();

        if (getIntent() != null) {
            mInsulatorData = (InsulatorData) getIntent().getExtras().getSerializable("InsulatorData");
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));
        if (!"".equals(mInsulatorData.getISLLC_XCNTS()) &&
                !"".equals(mInsulatorData.getISLLC_YDNTS())) {
            qMarker(mInsulatorData);
        }
        if (!"".equals(mInsulatorData.getISLPRSN_XCNTS()) &&
                !"".equals(mInsulatorData.getISLPRSN_YDNTS())) {
            cMarker(mInsulatorData);
        }
    }

    /**
     * Configuración UI(user interface) del mapa
     */
    private void mapUISetting() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setCompassEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);
    }

    /**
     * Acción cuando se presiona la tecla Atrás del Toolbar (barra de herramientas)
     * @param item android.R.id.home
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * POI(punto de interés) ubicación del paciente en cuarentena
     * @param mData Datos de la persona en autocuarentena
     */
    public void qMarker(InsulatorData mData) {
        try {
            LatLng latLng = new LatLng(
                    Double.parseDouble(mData.getISLLC_YDNTS()),
                    Double.parseDouble(mData.getISLLC_XCNTS())
            );
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

    /**
     * Ingresar el diálogo detallado de la dirección
     */
    private void addrDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View menuView = inflater.inflate(R.layout.dialog_etc_address, nullParent);
        final EditText et_addr_etc;
        et_addr_etc = menuView.findViewById(R.id.et_addr_etc);
        bt_jujeonbu = menuView.findViewById(R.id.bt_jujeonbu);
        bt_sijeongu = menuView.findViewById(R.id.bt_sijeongu);
        bt_gucheong = menuView.findViewById(R.id.bt_gucheong);

        bt_jujeonbu.setOnClickListener(mOnClickListener);
        bt_sijeongu.setOnClickListener(mOnClickListener);
        bt_gucheong.setOnClickListener(mOnClickListener);

        menuView.findViewById(R.id.ll_addr_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
            }
        });
        menuView.findViewById(R.id.ll_addr_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(bt_jujeonbu.getText().toString())) {
                    makeToast(mContext.getString(R.string.region_not_message));
                } else if ("".equals(bt_sijeongu.getText().toString())) {
                    makeToast(mContext.getString(R.string.provincia_not_message));
                } else if ("".equals(bt_gucheong.getText().toString())) {
                    makeToast(mContext.getString(R.string.distrito_not_message));
                } else {
                    String etcAddr = et_addr_etc.getText().toString();
                    mLocationData.setISLLC_ETC_ADRES(etcAddr);
                    saveDialog();
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setView(menuView);
        menuDialog = builder.create();
        menuDialog.getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_dialog));
        menuDialog.show();
    }

    /**
     * POI(punto de interés) ubicación actual del paciente en cuarentena
     * @param mData Datos de la persona en autocuarentena
     */
    public void cMarker(InsulatorData mData) {
        try {
            LatLng latLng = new LatLng(
                    Double.parseDouble(mData.getISLPRSN_YDNTS()),
                    Double.parseDouble(mData.getISLPRSN_XCNTS())
            );
            MarkerOptions makerOptions = new MarkerOptions();
            makerOptions
                    .position(latLng)
                    .title(mContext.getString(R.string.now_self_quarantine_location))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            mMap.addMarker(makerOptions).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Double parse Error -" + e);
        }
    }

    /**
     * Definición de diálogo de código
     * @param serviceName Nombre del servicio de código
     * @param item        Lista de resultados del servicio de código
     */
    public void codeDialog(final String serviceName, final ArrayList<HashMap<String, String>> item) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View mView = getLayoutInflater().inflate(R.layout.dialog_listview, null);
        if (serviceName.equals("sqsmc0002")) {
            mBuilder.setTitle(mContext.getString(R.string.region_select));
        } else if (serviceName.equals("sqsmc0003")) {
            mBuilder.setTitle(mContext.getString(R.string.provincia_select));
        } else if (serviceName.equals("sqsmc0004")) {
            mBuilder.setTitle(mContext.getString(R.string.distrito_select));
        }
        final ListView lv_list = mView.findViewById(R.id.lv_disaster_select);
        final ArrayList<String> list = new ArrayList<>();
        for (int i = 0; i < item.size(); i++) {
            Set key = item.get(i).keySet();
            Iterator mIterator = key.iterator();
            while (mIterator.hasNext()) {
                String map_key = (String) mIterator.next();
                list.add(map_key);
            }
        }
        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.select_dialog_item, list);
        lv_list.setAdapter(adapter);
        mBuilder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setView(mView);
        codeDialog = mBuilder.create();
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (serviceName.equals("sqsmc0002")) {
                    bt_jujeonbu.setText(list.get(position));
                    mInsulatorData.setISLLC_DPRTMNT_CODE(item.get(position).get(lv_list.getItemAtPosition(position)));
                } else if (serviceName.equals("sqsmc0003")) {
                    bt_sijeongu.setText(list.get(position));
                    mInsulatorData.setISLLC_PRVNCA_CODE(item.get(position).get(lv_list.getItemAtPosition(position)));
                } else if (serviceName.equals("sqsmc0004")) {
                    bt_gucheong.setText(list.get(position));
                    mInsulatorData.setISLLC_DSTRT_CODE(item.get(position).get(lv_list.getItemAtPosition(position)));
                }

                codeDialog.dismiss();
            }
        });
        codeDialog.show();
    }
}
