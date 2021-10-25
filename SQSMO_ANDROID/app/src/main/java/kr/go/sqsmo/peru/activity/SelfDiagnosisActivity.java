package kr.go.sqsmo.peru.activity;


import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.data.RetrofitData;
import kr.go.sqsmo.peru.data.servicedata.SelfCheckData;
import kr.go.sqsmo.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfDiagnosisActivity extends BaseActivity {

    CheckBox cb_self_not_symptom;
    SelfCheckData mSelfCheckData;
    RadioGroup rg_diag_temp, rg_diag_cough, rg_diag_sore_throat, rg_diag_dyspnea;

    TextView tv_diagnosis_date, tv_diag_bdheat, et_diag_etc;
    ToggleButton rb_diag_tempYes, rb_diag_coughYes, rb_diag_tempNo, rb_diag_coughNo, rb_diag_sore_throatYes, rb_diag_sore_throatNo, rb_diag_DyspneaYes, rb_diag_DyspneaNo;

    Button bt_diagnosis_submit;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_diagnosis);
        bind();
        set();

    }

    @Override
    protected void bind() {
        cb_self_not_symptom = findViewById(R.id.cb_self_not_symptom);
        rg_diag_temp = findViewById(R.id.rg_diag_temp);
        rb_diag_tempYes = findViewById(R.id.rb_diag_tempYes);
        rb_diag_tempNo = findViewById(R.id.rb_diag_tempNo);
        rg_diag_cough = findViewById(R.id.rg_diag_cough);
        rb_diag_coughYes = findViewById(R.id.rb_diag_coughYes);
        rb_diag_coughNo = findViewById(R.id.rb_diag_coughNo);
        rg_diag_sore_throat = findViewById(R.id.rg_diag_sore_throat);
        rb_diag_sore_throatYes = findViewById(R.id.rb_diag_sore_throatYes);
        rb_diag_sore_throatNo = findViewById(R.id.rb_diag_sore_throatNo);
        rg_diag_dyspnea = findViewById(R.id.rg_diag_dyspnea);
        rb_diag_DyspneaYes = findViewById(R.id.rb_diag_DyspneaYes);
        rb_diag_DyspneaNo = findViewById(R.id.rb_diag_DyspneaNo);
        bt_diagnosis_submit = findViewById(R.id.bt_diagnosis_submit);
        tv_diagnosis_date = findViewById(R.id.tv_diagnosis_date);
        toolbar = findViewById(R.id.toolbar);


        tv_diag_bdheat = findViewById(R.id.tv_diag_bdheat);
        et_diag_etc = findViewById(R.id.et_diag_etc);
    }

    @Override
    protected void set() {
        mSelfCheckData = new SelfCheckData();
        if (!"".equals(getIntent().getStringExtra("DATE")) && getIntent().getStringExtra("DATE") != null) {
            cb_self_not_symptom.setEnabled(false);
            rb_diag_tempYes.setEnabled(false);
            rb_diag_tempNo.setEnabled(false);
            rb_diag_coughYes.setEnabled(false);
            rb_diag_coughNo.setEnabled(false);
            rg_diag_sore_throat.setEnabled(false);
            rb_diag_sore_throatYes.setEnabled(false);
            rb_diag_sore_throatNo.setEnabled(false);
            rg_diag_dyspnea.setEnabled(false);
            rb_diag_DyspneaYes.setEnabled(false);
            rb_diag_DyspneaNo.setEnabled(false);
            bt_diagnosis_submit.setVisibility(View.GONE);
            retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERU0010(getIntent().getStringExtra("ISLPRSN_SN"), getIntent().getStringExtra("DATE"))).enqueue(peru0010_callback);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Consulta de información de autodiagnóstico Callback
     */
    public Callback<RetrofitData> peru0010_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getResults() != null) {
                        try {
                            JSONArray mJsonArray = new JSONArray(response.body().getResults().toString());
                            JSONObject mJsonObject = (JSONObject) mJsonArray.get(mJsonArray.length() - 1);
                            mSelfCheckData.setSLFDGNSS_DT(jsonNullCheck(mJsonObject, "SLFDGNSS_DT"));
                            mSelfCheckData.setSLFDGNSS_DT_F(jsonNullCheck(mJsonObject, "SLFDGNSS_DT_F"));
                            mSelfCheckData.setPYRXIA_AT(jsonNullCheck(mJsonObject, "PYRXIA_AT"));
                            mSelfCheckData.setCOUGH_AT(jsonNullCheck(mJsonObject, "COUGH_AT"));
                            mSelfCheckData.setSORE_THROAT_AT(jsonNullCheck(mJsonObject, "SORE_THROAT_AT"));
                            mSelfCheckData.setDYSPNEA_AT(jsonNullCheck(mJsonObject, "DYSPNEA_AT"));
                            mSelfCheckData.setRM(jsonNullCheck(mJsonObject, "RM"));
                            mSelfCheckData.setBDHEAT(jsonNullCheck(mJsonObject, "BDHEAT"));

                            String date = mContext.getString(R.string.today_symptom)+" : " + mSelfCheckData.getSLFDGNSS_DT_F();
                            tv_diagnosis_date.setText(date);

                            if ("Y".equals(mSelfCheckData.getPYRXIA_AT())) {
                                rb_diag_tempYes.setChecked(true);
                            } else {
                                rb_diag_tempNo.setChecked(true);
                            }
                            if ("Y".equals(mSelfCheckData.getSORE_THROAT_AT())) {
                                rb_diag_sore_throatYes.setChecked(true);
                            } else {
                                rb_diag_sore_throatNo.setChecked(true);
                            }
                            if ("Y".equals(mSelfCheckData.getCOUGH_AT())) {
                                rb_diag_coughYes.setChecked(true);
                            } else {
                                rb_diag_coughNo.setChecked(true);
                            }
                            if ("Y".equals(mSelfCheckData.getDYSPNEA_AT())) {
                                rb_diag_DyspneaYes.setChecked(true);
                            } else {
                                rb_diag_DyspneaNo.setChecked(true);
                            }
                            if (mSelfCheckData.getCOUGH_AT().equals("N") &&
                                    mSelfCheckData.getDYSPNEA_AT().equals("N") &&
                                    mSelfCheckData.getPYRXIA_AT().equals("N") &&
                                    mSelfCheckData.getSORE_THROAT_AT().equals("N")) {
                                cb_self_not_symptom.setChecked(true);
                            } else {
                                cb_self_not_symptom.setChecked(false);
                            }
                            if ("".equals(mSelfCheckData.getBDHEAT())) {
                                tv_diag_bdheat.setText("-");
                            } else {
                                tv_diag_bdheat.setText(mSelfCheckData.getBDHEAT());
                            }
                            if ("".equals(mSelfCheckData.getRM())) {
                                et_diag_etc.setText("-");
                            } else {
                                et_diag_etc.setText(mSelfCheckData.getRM());
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "peru0010_callback JSON ERROR - " + e);
                        }
                    } else {
                        logE("Error Message - " + response.body().getRes_msg());
                        makeToast(mContext.getString(R.string.fail_data_message));
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
}
