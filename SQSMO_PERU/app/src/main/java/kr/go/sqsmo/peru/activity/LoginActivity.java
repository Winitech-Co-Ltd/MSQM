package kr.go.sqsmo.peru.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.data.LoginServiceData;
import kr.go.sqsmo.peru.data.servicedata.UserData;
import kr.go.sqsmo.peru.network.AES256;
import kr.go.sqsmo.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {
    Button bt_login;
    EditText et_login_id, et_login_pw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        bind();
        set();
    }

    @Override
    protected void bind() {
        bt_login = findViewById(R.id.bt_login);
        et_login_id = findViewById(R.id.et_login_id);
        et_login_pw = findViewById(R.id.et_login_pw);
    }

    @Override
    protected void set() {
        bt_login.setOnClickListener(mOnClickListener);
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bt_login:
                    if ("".equals(et_login_id.getText().toString())) {
                        makeToast(mContext.getString(R.string.please_input_name));
                    } else if ("".equals(et_login_pw.getText().toString())) {
                        makeToast(mContext.getString(R.string.please_input_id));
                    } else {
                        retrofit.login_Service(mRetrofitBody.PERUO0001(et_login_id.getText().toString(), et_login_pw.getText().toString().toUpperCase(), mApplicationPERUO.getFcm_token())).enqueue(peruo0001_callback);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Autorización de la persona en autocuarentena Callback
     */
    public Callback<LoginServiceData> peruo0001_callback = new Callback<LoginServiceData>() {
        @Override
        public void onResponse(Call<LoginServiceData> call, Response<LoginServiceData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if ("100".equals(response.body().getRes_cd())) {
                            mApplicationPERUO.setLoginId(et_login_id.getText().toString());
                            try {
                                JSONArray mJSONArray = new JSONArray(response.body().getResults().toString());
                                JSONObject mJsonObject = (JSONObject) mJSONArray.get(mJSONArray.length() - 1);
                                UserData mUserData = new UserData();
                                mUserData.setMNGR_SN(jsonNullCheck(mJsonObject, "MNGR_SN"));
                                mUserData.setMNGR_NM(jsonNullCheck(mJsonObject, "MNGR_NM"));
                                mUserData.setMBTLNUM(jsonNullCheck(mJsonObject, "MBTLNUM"));
                                mUserData.setPSITN_DPRTMNT_CODE(jsonNullCheck(mJsonObject, "PSITN_DPRTMNT_CODE"));
                                mUserData.setPSITN_DPRTMNT_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_DPRTMNT_CODE_NM"));
                                mUserData.setPSITN_PRVNCA_CODE(jsonNullCheck(mJsonObject, "PSITN_PRVNCA_CODE"));
                                mUserData.setPSITN_PRVNCA_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_PRVNCA_CODE_NM"));
                                mUserData.setPSITN_DSTRT_CODE(jsonNullCheck(mJsonObject, "PSITN_DSTRT_CODE"));
                                mUserData.setPSITN_DSTRT_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_DSTRT_CODE_NM"));
                                mUserData.setPSITN_DEPT_NM(jsonNullCheck(mJsonObject, "PSITN_DEPT_NM"));
                                mUserData.setOFFM_TELNO(jsonNullCheck(mJsonObject, "OFFM_TELNO"));
                                mUserData.setSKYPE_ID(jsonNullCheck(mJsonObject, "SKYPE_ID"));
                                mUserData.setWHATSUP_ID(jsonNullCheck(mJsonObject, "WHATSUP_ID"));
                                mUserData.setPUSHID(jsonNullCheck(mJsonObject, "PUSHID"));
                                mUserData.setLOGIN_ID(jsonNullCheck(mJsonObject, "LOGIN_ID"));
                                mUserData.setOPRTR_AT(jsonNullCheck(mJsonObject, "OPRTR_AT"));
                                mApplicationPERUO.setPrivateKey(jsonNullCheck(mJsonObject, "ENCPT_DECD_KEY").substring(0, 16), jsonNullCheck(mJsonObject, "ENCPT_DECD_KEY").substring(16, 32));
                                AES256.setPrivate(mApplicationPERUO.getPrivateKey(), mApplicationPERUO.getPrivateVector());
                                mApplicationPERUO.setHeader(AES256.publicEncrypt(mUserData.getMNGR_SN()));
                                mApplicationPERUO.setmUserData(mUserData);
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                            } catch (JSONException e) {
                                Log.e(TAG, "peruo0001_callback JSON ERROR - " + e);
                            }
                        } else {
                            makeToast("Error Message- " + response.body().getRes_msg());
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

