package kr.go.sqsm.peru.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.data.servicedata.UserData;
import kr.go.sqsm.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModifyActivity extends BaseActivity {
    TextView tv_enroll_national, tv_toolbar_title;
    TextView tv_birth, tv_full_addr, tv_id;
    TextView tv_pass_port, tv_enroll_emerTel, tv_enroll_tel;
    EditText et_enroll_name, et_enroll_emerTel, et_pass_port, et_enroll_newLocation, et_enroll_tel, et_id;
    ToggleButton rb_enroll_man, rb_enroll_woman;
    Button bt_sigungu, bt_sido, bt_dong, bt_enroll_regist, bt_add_enroll_location;
    Toolbar toolbar;
    private InputMethodManager imm;
    UserData mUserData = new UserData();

    /**
     * Expresión regular (estándar internacional)
     */
    Pattern pattern = Pattern.compile("^\\+?[0-9]{9,15}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        if (mApplicationPERU.getCountryList().size() == 0) {
            mApplicationPERU.setCountry();
        }

        bind();
        set();
        retrofit.private_Service(mApplicationPERU.getHeader(), mRetrofitBody.PERU0004(mApplicationPERU.getUserNumber(), mApplicationPERU.getUserSn())).enqueue(peru0004_callback);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_enroll_regist:
                    if ("".equals(et_enroll_emerTel.getText().toString())
                            || !PhoneNumberUtils.isGlobalPhoneNumber(et_enroll_emerTel.getText().toString())) {
                        makeToast(mContext.getString(R.string.not_EMER_TEL_message));
                    } else if (!pattern.matcher(et_enroll_emerTel.getText().toString()).find()) {
                        makeToast(mContext.getString(R.string.PHONE_NUMBER_MAXSIMUM));
                    } else {
                        mUserData.setPSPRNBR(et_pass_port.getText().toString());
                        mUserData.setEMGNC_TELNO(et_enroll_emerTel.getText().toString());
                        mUserData.setISLLC_ETC_ADRES(et_enroll_newLocation.getText().toString());
                        retrofit.private_Service(mApplicationPERU.getHeader(), mRetrofitBody.PERU0005(
                                mApplicationPERU.getUserNumber(),
                                mApplicationPERU.getUserSn(),
                                mUserData
                        )).enqueue(peru0005_callback);
                    }
                    break;
                case R.id.bt_add_enroll_location:
                    startActivity(new Intent(mContext, EnrollmentMapActivity.class)
                            .putExtra("isUser", true)
                            .putExtra("locationX",mUserData.getISLLC_XCNTS())
                            .putExtra("locationY",mUserData.getISLLC_YDNTS()));
                    break;

                default:
                    break;
            }
        }
    };

    @Override
    protected void bind() {
        toolbar = findViewById(R.id.toolbar);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        tv_toolbar_title = findViewById(R.id.tv_toolbar_title);
        et_enroll_name = findViewById(R.id.et_enroll_name);
        rb_enroll_man = findViewById(R.id.rb_enroll_man);
        rb_enroll_woman = findViewById(R.id.rb_enroll_woman);
        tv_enroll_national = findViewById(R.id.tv_enroll_national);
        et_enroll_tel = findViewById(R.id.et_enroll_tel);
        et_enroll_emerTel = findViewById(R.id.et_enroll_emerTel);
        tv_birth = findViewById(R.id.tv_birth);
        bt_add_enroll_location = findViewById(R.id.bt_add_enroll_location);
        bt_enroll_regist = findViewById(R.id.bt_enroll_regist);
        et_enroll_newLocation = findViewById(R.id.et_enroll_newLocation);
        bt_sido = findViewById(R.id.bt_sido);
        bt_sigungu = findViewById(R.id.bt_sigungu);
        bt_dong = findViewById(R.id.bt_dong);
        tv_full_addr = findViewById(R.id.tv_full_addr);

        tv_pass_port = findViewById(R.id.tv_pass_port);
        et_pass_port = findViewById(R.id.et_pass_port);
        tv_enroll_tel = findViewById(R.id.tv_enroll_tel);
        tv_enroll_emerTel = findViewById(R.id.tv_enroll_emerTel);
        et_id = findViewById(R.id.et_id);
        tv_id = findViewById(R.id.tv_id);

    }

    @Override
    protected void set() {
        tv_toolbar_title.setText(mContext.getString(R.string.modify));
        bt_add_enroll_location.setOnClickListener(mOnClickListener);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        imm.hideSoftInputFromWindow(et_enroll_emerTel.getWindowToken(), 0);
        et_enroll_name.setEnabled(false);
        et_enroll_name.setBackgroundResource(R.drawable.edittext_border_disable);
        tv_birth.setEnabled(false);
        tv_birth.setBackgroundResource(R.drawable.edittext_border_disable);
        bt_enroll_regist.setOnClickListener(mOnClickListener);
        tv_full_addr.setVisibility(View.VISIBLE);
    }

    /**
     * Acción cuando se presiona la tecla Atrás del Toolbar (barra de herramientas)
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
     * Consulta de información de registro de la persona en autocuarentena Callback
     */
    public Callback<RetrofitData> peru0004_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            try {
                                JSONArray array = new JSONArray(response.body().getResults().toString());
                                JSONObject mJsonObject = (JSONObject) array.get(array.length() - 1);
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
                                mUserData.setISLLC_DPRTMNT_CODE(jsonNullCheck(mJsonObject,"ISLLC_DPRTMNT_CODE"));
                                mUserData.setISLLC_DPRTMNT_CODE_NM(jsonNullCheck(mJsonObject,"ISLLC_DPRTMNT_CODE_NM"));
                                mUserData.setISLLC_PRVNCA_CODE(jsonNullCheck(mJsonObject,"ISLLC_PRVNCA_CODE"));
                                mUserData.setISLLC_PRVNCA_CODE_NM(jsonNullCheck(mJsonObject,"ISLLC_PRVNCA_CODE_NM"));
                                mUserData.setISLLC_DSTRT_CODE(jsonNullCheck(mJsonObject,"ISLLC_DSTRT_CODE"));
                                mUserData.setISLLC_DSTRT_CODE_NM(jsonNullCheck(mJsonObject,"ISLLC_DSTRT_CODE_NM"));
                                mUserData.setISLLC_ETC_ADRES(jsonNullCheck(mJsonObject,"ISLLC_ETC_ADRES"));
                                mUserData.setADDR(jsonNullCheck(mJsonObject,"ADDR"));
                                mApplicationPERU.setUserData(mUserData);
                            } catch (JSONException e) {
                                Log.e(TAG, "peru0004_callback JSON ERROR - " + e);
                            } finally {
                                et_enroll_name.setText(mUserData.getISLPRSN_NM());
                                if ("00101".equals(mUserData.getSEXDSTN_CODE())) {
                                    rb_enroll_man.setChecked(true);
                                    rb_enroll_woman.setChecked(false);
                                } else {
                                    rb_enroll_man.setChecked(false);
                                    rb_enroll_woman.setChecked(true);
                                }
                                tv_birth.setText(mUserData.getBRTHDY_F());
                                et_pass_port.setText(mUserData.getPSPRNBR());

                                String national = "";
                                for (int i = 0; i < mApplicationPERU.getCountryList2().size(); i++) {
                                    if (mApplicationPERU.getCountryList2().get(i).get(mUserData.getNLTY_CODE()) != null) {
                                        national = mApplicationPERU.getCountryList2().get(i).get(mUserData.getNLTY_CODE());
                                        break;
                                    }
                                }
                                tv_enroll_national.setText(national);


                                et_enroll_tel.setText(mUserData.getTELNO());
                                et_enroll_emerTel.setText(mUserData.getEMGNC_TELNO());
                                et_enroll_newLocation.setText(mUserData.getISLLC_ETC_ADRES());

                                if (!"".equals(mUserData.getINHT_ID())){
                                    et_id.setText(mUserData.getINHT_ID());
                                    et_id.setEnabled(false);
                                    et_id.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border_disable));
                                } else {
                                    tv_id.setVisibility(View.GONE);
                                    et_id.setVisibility(View.GONE);
                                }

                                if (!"".equals(mUserData.getPSPRNBR())) {
                                    tv_pass_port.setVisibility(View.VISIBLE);
                                    et_pass_port.setVisibility(View.VISIBLE);
                                    et_pass_port.setText(mUserData.getPSPRNBR());
                                    et_pass_port.setEnabled(false);
                                    et_pass_port.setBackground(mContext.getResources().getDrawable(R.drawable.edittext_border_disable));
                                }


                                rb_enroll_man.setEnabled(false);
                                rb_enroll_woman.setEnabled(false);
                                et_enroll_tel.setText(mUserData.getTELNO());
                                Log.e(TAG, "id - " + mUserData.getINHT_ID());


                                bt_sido.setEnabled(false);
                                bt_sigungu.setEnabled(false);
                                bt_dong.setEnabled(false);
                                bt_sido.setVisibility(View.GONE);
                                bt_sigungu.setVisibility(View.GONE);
                                bt_dong.setVisibility(View.GONE);

                                tv_full_addr.setText(mUserData.getADDR());

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
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    /**
     * Modificar información de la persona en autocuarentena  Callback
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
                            mUserData.setISLLC_DPRTMNT_CODE(jsonNullCheck(mJsonObject,"ISLLC_DPRTMNT_CODE"));
                            mUserData.setISLLC_DPRTMNT_CODE_NM(jsonNullCheck(mJsonObject,"ISLLC_DPRTMNT_CODE_NM"));
                            mUserData.setISLLC_PRVNCA_CODE(jsonNullCheck(mJsonObject,"ISLLC_PRVNCA_CODE"));
                            mUserData.setISLLC_PRVNCA_CODE_NM(jsonNullCheck(mJsonObject,"ISLLC_PRVNCA_CODE_NM"));
                            mUserData.setISLLC_DSTRT_CODE(jsonNullCheck(mJsonObject,"ISLLC_DSTRT_CODE"));
                            mUserData.setISLLC_DSTRT_CODE_NM(jsonNullCheck(mJsonObject,"ISLLC_DSTRT_CODE_NM"));
                            mUserData.setISLLC_ETC_ADRES(jsonNullCheck(mJsonObject,"ISLLC_ETC_ADRES"));
                            mUserData.setINHT_ID(jsonNullCheck(mJsonObject, "INHT_ID"));
                            mUserData.setADDR(jsonNullCheck(mJsonObject,"ADDR"));
                            mApplicationPERU.setUserData(mUserData);
                            makeToast(mContext.getString(R.string.info_save_success));

                            finish();
                        } catch (JSONException e) {
                            Log.e(TAG, "adrr JSON ERROR - " + e);
                            makeToast(mContext.getString(R.string.network_error_message));
                        }
                    } else {
                        logE("Error Message - " + response.body().getRes_msg());
                        makeToast(mContext.getString(R.string.network_error_message));
                    }
                } else {
                    makeToast(mContext.getString(R.string.network_error_message));
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

}