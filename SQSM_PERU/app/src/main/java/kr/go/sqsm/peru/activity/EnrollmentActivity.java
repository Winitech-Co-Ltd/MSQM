package kr.go.sqsm.peru.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.servicedata.LocationData;
import kr.go.sqsm.peru.data.servicedata.LoginServiceData;
import kr.go.sqsm.peru.data.servicedata.UserData;
import kr.go.sqsm.peru.util.AES256;
import kr.go.sqsm.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EnrollmentActivity extends BaseActivity {
    private final int LOCATION_EDIT = 1000;
    TextView tv_birth, tv_toolbar_title, tv_enroll_national;
    TextView tv_pass_port, tv_enroll_tel, tv_enroll_emerTel, tv_enroll_locationSave;
    EditText et_enroll_name, et_enroll_emerTel, et_pass_port, et_enroll_newLocation, et_enroll_tel, et_id;
    ToggleButton rb_enroll_man, rb_enroll_woman;
    Button bt_sigungu, bt_sido, bt_dong, bt_enroll_regist, bt_add_enroll_location;
    Toolbar toolbar;
    UserData mInputUserData = new UserData();
    AlertDialog dialog;
    /**
     * Expresión regular (estándar internacional)
     */
    Pattern pattern = Pattern.compile("^\\+?[0-9]{9,15}$");

    boolean isLocationData = false;
    LocationData mLocationData;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);
        mInputUserData.setNLTY_CODE(getIntent().getStringExtra("national"));
        bind();
        set();
    }

    @Override
    protected void bind() {
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        et_enroll_name = findViewById(R.id.et_enroll_name);
        rb_enroll_man = findViewById(R.id.rb_enroll_man);
        rb_enroll_woman = findViewById(R.id.rb_enroll_woman);
        et_enroll_tel = findViewById(R.id.et_enroll_tel);
        et_enroll_emerTel = findViewById(R.id.et_enroll_emerTel);
        tv_birth = findViewById(R.id.tv_birth);
        bt_add_enroll_location = findViewById(R.id.bt_add_enroll_location);
        bt_enroll_regist = findViewById(R.id.bt_enroll_regist);
        et_enroll_newLocation = findViewById(R.id.et_enroll_newLocation);
        tv_enroll_national = findViewById(R.id.tv_enroll_national);
        toolbar = findViewById(R.id.toolbar);
        bt_sido = findViewById(R.id.bt_sido);
        bt_sigungu = findViewById(R.id.bt_sigungu);
        bt_dong = findViewById(R.id.bt_dong);
        tv_pass_port = findViewById(R.id.tv_pass_port);
        et_pass_port = findViewById(R.id.et_pass_port);
        tv_enroll_tel = findViewById(R.id.tv_enroll_tel);
        tv_enroll_emerTel = findViewById(R.id.tv_enroll_emerTel);
        tv_enroll_locationSave = findViewById(R.id.tv_enroll_locationSave);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        et_id = findViewById(R.id.et_id);
    }

    @Override
    protected void set() {
        tv_toolbar_title.setText(mContext.getString(R.string.enrollment));
        bt_add_enroll_location.setOnClickListener(mOnClickListener);
        bt_add_enroll_location.setText(mContext.getString(R.string.add_location));
        tv_birth.setOnClickListener(mOnClickListener);
        bt_enroll_regist.setOnClickListener(mOnClickListener);
        rb_enroll_man.setOnClickListener(mOnClickListener);
        rb_enroll_woman.setOnClickListener(mOnClickListener);
        bt_sido.setOnClickListener(mOnClickListener);
        bt_sigungu.setOnClickListener(mOnClickListener);
        bt_dong.setOnClickListener(mOnClickListener);
        if (!"PE".equals(mInputUserData.getNLTY_CODE())) {
            tv_pass_port.setVisibility(View.VISIBLE);
            et_pass_port.setVisibility(View.VISIBLE);
        }
        mInputUserData.setECSHG_MNGR_SN(mApplicationPERU.getManagerNumber());
        if (getPhoneNumber() != null) {
            if (!"".equals(getPhoneNumber())) {
                et_enroll_tel.setText(getPhoneNumber());
            } else {
                et_enroll_tel.setBackgroundResource(R.drawable.edittext_border);
                et_enroll_tel.setEnabled(true);
            }
        } else {
            et_enroll_tel.setBackgroundResource(R.drawable.edittext_border);
            et_enroll_tel.setEnabled(true);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imm.hideSoftInputFromWindow(et_enroll_name.getWindowToken(), 0);

        for (int i = 0; i < mApplicationPERU.getCountryList2().size(); i++) {
            if (mApplicationPERU.getCountryList2().get(i).get(mInputUserData.getNLTY_CODE()) != null) {
                tv_enroll_national.setText(mApplicationPERU.getCountryList2().get(i).get(mInputUserData.getNLTY_CODE()));
                break;
            }
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_enroll_regist:
                    if ("".equals(et_enroll_name.getText().toString())) {
                        makeToast(mContext.getString(R.string.not_name_message));
                    } else if ("".equals(mInputUserData.getBRTHDY())) {
                        makeToast(mContext.getString(R.string.not_birthday_message));
                    } else if ("".equals(mInputUserData.getSEXDSTN_CODE())) {
                        makeToast(mContext.getString(R.string.not_sex_message));
                    } else if ("PE".equals(mInputUserData.getNLTY_CODE()) && "".equals(et_id.getText().toString())) {
                        makeToast(mContext.getString(R.string.id_hint));
                    } else if (!"PE".equals(mInputUserData.getNLTY_CODE()) && "".equals(et_id.getText().toString()) && "".equals(et_pass_port.getText().toString())) {
                        makeToast(mContext.getString(R.string.not_PASSPORT_message));
                    } else if ("".equals(et_enroll_tel.getText().toString())) {
                        makeToast(mContext.getString(R.string.not_TEL_message));
                    } else if (et_enroll_tel.getText().toString().getBytes().length < 9) {
                        makeToast(mContext.getString(R.string.PHONE_NUMBER_MAXSIMUM));
                    } else if (et_enroll_tel.isEnabled() && !pattern.matcher(et_enroll_tel.getText().toString()).find()) {
                        makeToast(mContext.getString(R.string.wrong_TEL_message));
                    } else if ("".equals(et_enroll_emerTel.getText().toString())) {
                        makeToast(mContext.getString(R.string.not_EMER_TEL_message));
                    } else if (et_enroll_emerTel.getText().toString().getBytes().length < 9) {
                        makeToast(mContext.getString(R.string.PHONE_NUMBER_MAXSIMUM));
                    } else if (!pattern.matcher(et_enroll_emerTel.getText().toString()).find()) {
                        makeToast(mContext.getString(R.string.wrong_EMER_TEL_message));
                    } else if ("".equals(mInputUserData.getISLLC_XCNTS()) && "".equals(mInputUserData.getISLLC_YDNTS())) {
                        makeToast(mContext.getString(R.string.please_input_location_xy));
                    } else {
                        mInputUserData.setISLPRSN_NM(et_enroll_name.getText().toString());
                        mInputUserData.setPSPRNBR(et_pass_port.getText().toString());
                        mInputUserData.setEMGNC_TELNO(et_enroll_emerTel.getText().toString());
                        mInputUserData.setTELNO(et_enroll_tel.getText().toString());
                        mInputUserData.setISLLC_ETC_ADRES(et_enroll_newLocation.getText().toString());
                        mInputUserData.setINHT_ID(et_id.getText().toString());
                        retrofit.login_Service(mRetrofitBody.PERU0012(mInputUserData)).enqueue(peru0012_callback);
                    }
                    break;
                case R.id.bt_add_enroll_location:
                    if (!isLocationData) {
                        startActivityForResult(new Intent(mContext, EnrollmentMapActivity.class).putExtra("isUser", false), LOCATION_EDIT);
                    } else {
                        locationDialog(mContext.getString(R.string.add_location), mContext.getString(R.string.already_location_save));
                    }
                    break;
                case R.id.tv_birth:
                    dateDialog();
                    break;
                case R.id.rb_enroll_woman:
                    rb_enroll_woman.setChecked(true);
                    rb_enroll_man.setChecked(false);
                    mInputUserData.setSEXDSTN_CODE("00102");
                    break;
                case R.id.rb_enroll_man:
                    rb_enroll_woman.setChecked(false);
                    rb_enroll_man.setChecked(true);
                    mInputUserData.setSEXDSTN_CODE("00101");
                    break;

                case R.id.bt_sido:
                    bt_sido.setText("");
                    bt_sigungu.setText("");
                    bt_dong.setText("");

                    mInputUserData.setISLLC_DPRTMNT_CODE("");
                    mInputUserData.setISLLC_PRVNCA_CODE("");
                    mInputUserData.setISLLC_DSTRT_CODE("");
                    retrofit.login_Service(mRetrofitBody.PERUC0002()).enqueue(peruc0002_callback);
                    break;
                case R.id.bt_sigungu:
                    if (!"".equals(mInputUserData.getISLLC_DPRTMNT_CODE())) {
                        bt_sigungu.setText("");
                        bt_dong.setText("");
                        mInputUserData.setISLLC_PRVNCA_CODE("");
                        mInputUserData.setISLLC_DSTRT_CODE("");
                        retrofit.login_Service(mRetrofitBody.PERUC0003(mInputUserData.getISLLC_DPRTMNT_CODE())).enqueue(peruc0003_callback);
                    } else {
                        makeToast(mContext.getString(R.string.region_not_message));
                    }
                    break;
                case R.id.bt_dong:
                    if (!"".equals(mInputUserData.getISLLC_DPRTMNT_CODE())) {
                        if (!"".equals(mInputUserData.getISLLC_PRVNCA_CODE())) {
                            bt_dong.setText("");
                            mInputUserData.setISLLC_DSTRT_CODE("");
                            retrofit.login_Service(mRetrofitBody.PERUC0004(mInputUserData.getISLLC_DPRTMNT_CODE(), mInputUserData.getISLLC_PRVNCA_CODE())).enqueue(peruc0004_callback);
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
     * Autorización de la persona en autocuarentena  Callback
     */
    public Callback<LoginServiceData> peru0012_callback = new Callback<LoginServiceData>() {
        @Override
        public void onResponse(Call<LoginServiceData> call, Response<LoginServiceData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            try {
                                JSONArray array = new JSONArray(response.body().getResults().toString());
                                JSONObject mJsonObject = (JSONObject) array.get(array.length() - 1);
                                mApplicationPERU.setUserNumber(jsonNullCheck(mJsonObject, "ISLPRSN_SN"), jsonNullCheck(mJsonObject, "TRMNL_SN"));
                                UserData mUserData = new UserData();
                                mUserData.setISLPRSN_SN(jsonNullCheck(mJsonObject, "ISLPRSN_SN"));
                                mUserData.setISLPRSN_NM(jsonNullCheck(mJsonObject, "ISLPRSN_NM"));
                                mUserData.setSEXDSTN(jsonNullCheck(mJsonObject, "SEXDSTN"));
                                mUserData.setSEXDSTN_CODE(jsonNullCheck(mJsonObject, "SEXDSTN_CODE"));
                                mUserData.setNLTY_CODE(jsonNullCheck(mJsonObject, "NLTY_CODE"));
                                mUserData.setISLLC_XCNTS(jsonNullCheck(mJsonObject, "ISLLC_XCNTS"));
                                mUserData.setISLLC_YDNTS(jsonNullCheck(mJsonObject, "ISLLC_YDNTS"));
                                mUserData.setECSHG_MNGR_SN(jsonNullCheck(mJsonObject, "ECSHG_MNGR_SN"));
                                mUserData.setTELNO(jsonNullCheck(mJsonObject, "TELNO"));
                                mUserData.setEMGNC_TELNO(jsonNullCheck(mJsonObject, "EMGNC_TELNO"));
                                mUserData.setTRMNL_SN(jsonNullCheck(mJsonObject, "TRMNL_SN"));
                                mUserData.setDISTANCE(jsonNullCheck(mJsonObject, "DISTANCE"));
                                mUserData.setPSPRNBR(jsonNullCheck(mJsonObject, "PSPRNBR"));
                                mUserData.setBRTHDY(jsonNullCheck(mJsonObject, "BRTHDY"));
                                mUserData.setBRTHDY_F(jsonNullCheck(mJsonObject, "BRTHDY_F"));
                                mUserData.setISL_SE_CODE(jsonNullCheck(mJsonObject, "ISL_SE_CODE"));
                                mUserData.setISL_SE_CODE_NM(jsonNullCheck(mJsonObject, "ISL_SE_CODE_NM"));
                                mUserData.setMNGR_LOGIN_ID(jsonNullCheck(mJsonObject, "MNGR_LOGIN_ID"));
                                mUserData.setINHT_ID(jsonNullCheck(mJsonObject, "INHT_ID"));
                                mUserData.setISLLC_DPRTMNT_CODE(jsonNullCheck(mJsonObject, "ISLLC_DPRTMNT_CODE"));
                                mUserData.setISLLC_DPRTMNT_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_DPRTMNT_CODE_NM"));
                                mUserData.setISLLC_PRVNCA_CODE(jsonNullCheck(mJsonObject, "ISLLC_PRVNCA_CODE"));
                                mUserData.setISLLC_PRVNCA_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_PRVNCA_CODE_NM"));
                                mUserData.setISLLC_DSTRT_CODE(jsonNullCheck(mJsonObject, "ISLLC_DSTRT_CODE"));
                                mUserData.setISLLC_DSTRT_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_DSTRT_CODE_NM"));
                                mUserData.setISLLC_ETC_ADRES(jsonNullCheck(mJsonObject, "ISLLC_ETC_ADRES"));
                                mUserData.setADDR(jsonNullCheck(mJsonObject, "ADDR"));
                                mApplicationPERU.setPrivateKey(jsonNullCheck(mJsonObject,"ENCPT_DECD_KEY").substring(0,16),jsonNullCheck(mJsonObject,"ENCPT_DECD_KEY").substring(16,32));
                                AES256.setPrivate(mApplicationPERU.getPrivateKey(),mApplicationPERU.getPrivateVector());
                                mApplicationPERU.setHeader(AES256.publicEncrypt(mUserData.getISLPRSN_SN()));
                                mApplicationPERU.setUserData(mUserData);
                                makeToast(mContext.getString(R.string.user_certification_success));
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                                //      pinDialog(mJsonObject);

                            } catch (JSONException e) {
                                Log.e(TAG, "peru0012_callback JSON ERROR - " + e);
                                makeToast(mContext.getString(R.string.network_error_message));
                            }
                        } else {
                            makeToast(mContext.getString(R.string.network_error_message));
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
        public void onFailure(Call<LoginServiceData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "requestCode - " + requestCode + ", resultCode - " + resultCode);
        if (resultCode == RESULT_OK) {
            if (requestCode == LOCATION_EDIT) {
                if (data != null) {
                    mLocationData = (LocationData) data.getSerializableExtra("location");
                    Log.e(TAG, "x - " + mLocationData.getISLLC_XCNTS() + ", y - " + mLocationData.getISLLC_YDNTS());
                    mInputUserData.setISLLC_XCNTS(mLocationData.getISLLC_XCNTS());
                    mInputUserData.setISLLC_YDNTS(mLocationData.getISLLC_YDNTS());
                    tv_enroll_locationSave.setVisibility(View.VISIBLE);

                    isLocationData = true;
                }
            }
        }
    }

    /**
     * Definición de diálogo de código
     * @param serviceName Nombre del servicio de código
     * @param item Lista de resultados del servicio de código
     */
    public void codeDialog(final String serviceName, final ArrayList<HashMap<String, String>> item) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View mView = getLayoutInflater().inflate(R.layout.dialog_listview, null);
        if (serviceName.equals("peruc0002")) {
            mBuilder.setTitle(mContext.getString(R.string.region_select));
        } else if (serviceName.equals("peruc0003")) {
            mBuilder.setTitle(mContext.getString(R.string.provincia_select));
        } else if (serviceName.equals("peruc0004")) {
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
        dialog = mBuilder.create();
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (serviceName.equals("peruc0002")) {
                    bt_sido.setText(list.get(position));
                    mInputUserData.setISLLC_DPRTMNT_CODE(item.get(position).get(lv_list.getItemAtPosition(position)));
                } else if (serviceName.equals("peruc0003")) {
                    bt_sigungu.setText(list.get(position));
                    mInputUserData.setISLLC_PRVNCA_CODE(item.get(position).get(lv_list.getItemAtPosition(position)));
                } else if (serviceName.equals("peruc0004")) {
                    bt_dong.setText(list.get(position));
                    mInputUserData.setISLLC_DSTRT_CODE(item.get(position).get(lv_list.getItemAtPosition(position)));
                }

                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     * fecha de nacimiento
     */
    private void dateDialog() {
        DatePickerDialog mDatePickerDialog = new DatePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String msg = String.format(Locale.US,"%02d/%02d/%d", dayOfMonth, month + 1, year);
                        mInputUserData.setBRTHDY(String.format(Locale.KOREAN, "%d%02d%02d", year, month + 1, dayOfMonth));
                        tv_birth.setText(msg);
                    }
                }, 1970, 0, 1);
        mDatePickerDialog.getDatePicker().setCalendarViewShown(false);
        mDatePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDatePickerDialog.show();
    }

    /**
     * Diálogo de verificación para guardar ubicación
     * @param title
     * @param msg
     */
    private void locationDialog(String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton(mContext.getString(R.string.check), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivityForResult(new Intent(mContext, EnrollmentMapActivity.class).putExtra("isUser", false), LOCATION_EDIT);
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
     * Región Callback
     */
    public Callback<LoginServiceData> peruc0002_callback = new Callback<LoginServiceData>() {
        @Override
        public void onResponse(Call<LoginServiceData> call, Response<LoginServiceData> response) {
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
                                codeDialog("peruc0002", statusCode);
                            } catch (JSONException e) {
                                Log.e(TAG, "peruc0002_callback JSON ERROR - " + e);
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
        public void onFailure(Call<LoginServiceData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    /**
     * Provincia Callback
     */
    public Callback<LoginServiceData> peruc0003_callback = new Callback<LoginServiceData>() {
        @Override
        public void onResponse(Call<LoginServiceData> call, Response<LoginServiceData> response) {
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
                                codeDialog("peruc0003", statusCode);
                            } catch (JSONException e) {
                                Log.e(TAG, "peruc0003_callback JSON ERROR - " + e);
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
        public void onFailure(Call<LoginServiceData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    /**
     * Distrito Callback
     */
    public Callback<LoginServiceData> peruc0004_callback = new Callback<LoginServiceData>() {
        @Override
        public void onResponse(Call<LoginServiceData> call, Response<LoginServiceData> response) {
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
                                codeDialog("peruc0004", statusCode);
                            } catch (JSONException e) {
                                Log.e(TAG, "peruc0004_callback JSON ERROR - " + e);
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
        public void onFailure(Call<LoginServiceData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };
}
