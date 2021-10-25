package kr.go.sqsm.peru.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.appcompat.widget.Toolbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.data.servicedata.SelfCheckData;
import kr.go.sqsm.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfDiagnosisActivity extends BaseActivity {

    SelfCheckData mSelfCheckData;
    RadioGroup rg_diag_temp, rg_diag_cough, rg_diag_sore_throat, rg_diag_dyspnea;

    ToggleButton rb_diag_tempYes, rb_diag_coughYes, rb_diag_tempNo, rb_diag_coughNo, rb_diag_sore_throatYes, rb_diag_sore_throatNo, rb_diag_DyspneaYes, rb_diag_DyspneaNo;
    EditText et_diag_bdheat1, et_diag_bdheat2, et_diag_etc;
    TextView tv_diag_bdheat, tv_diagnosis_guide, tv_diagnosis_title;
    Button bt_diagnosis_submit;
    Toolbar toolbar;
    View view_selfcheck;

    LinearLayout ll_diag_bdheat;
    private InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_diagnosis);
        bind();
        set();

    }

    @Override
    protected void bind() {
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
        toolbar = findViewById(R.id.toolbar);

        et_diag_bdheat1 = findViewById(R.id.et_diag_bdheat1);
        et_diag_bdheat2 = findViewById(R.id.et_diag_bdheat2);
        tv_diag_bdheat = findViewById(R.id.tv_diag_bdheat);
        et_diag_etc = findViewById(R.id.et_diag_etc);
        ll_diag_bdheat = findViewById(R.id.ll_diag_bdheat);
        tv_diagnosis_guide = findViewById(R.id.tv_diagnosis_guide);
        tv_diagnosis_title = findViewById(R.id.tv_diagnosis_title);
        view_selfcheck = findViewById(R.id.view_selfcheck);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void set() {
        mSelfCheckData = new SelfCheckData();
        imm.hideSoftInputFromWindow(et_diag_bdheat1.getWindowToken(), 0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if (!"".equals(getIntent().getStringExtra("DATE")) && getIntent().getStringExtra("DATE") != null) {
            tv_diagnosis_title.setText(mContext.getString(R.string.selftcheck_title));
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
            et_diag_etc.setEnabled(false);
            et_diag_etc.setBackgroundResource(R.drawable.bg_blue_dash_border);
            view_selfcheck.setVisibility(View.GONE);
            tv_diag_bdheat.setVisibility(View.VISIBLE);
            ll_diag_bdheat.setVisibility(View.GONE);
            bt_diagnosis_submit.setVisibility(View.GONE);
            tv_diagnosis_guide.setVisibility(View.GONE);
            retrofit.private_Service(mApplicationPERU.getHeader(),mRetrofitBody.PERU0010(mApplicationPERU.getUserNumber(), mApplicationPERU.getUserSn(), getIntent().getStringExtra("DATE"))).enqueue(peru0010_callback);
        } else {
            rb_diag_tempYes.setOnClickListener(mOnClickListener);
            rb_diag_tempNo.setOnClickListener(mOnClickListener);
            rb_diag_coughYes.setOnClickListener(mOnClickListener);
            rb_diag_coughNo.setOnClickListener(mOnClickListener);
            rb_diag_sore_throatYes.setOnClickListener(mOnClickListener);
            rb_diag_sore_throatNo.setOnClickListener(mOnClickListener);
            rb_diag_DyspneaYes.setOnClickListener(mOnClickListener);
            rb_diag_DyspneaNo.setOnClickListener(mOnClickListener);
            bt_diagnosis_submit.setVisibility(View.VISIBLE);
            et_diag_bdheat1.addTextChangedListener(mTextWatcher);
            bt_diagnosis_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("".equals(mSelfCheckData.getPYRXIA_AT())) {
                        makeToast(mContext.getString(R.string.not_PYRXIA_AT_message));
                    } else if ("".equals(mSelfCheckData.getCOUGH_AT())) {
                        makeToast(mContext.getString(R.string.not_COUGH_AT_message));
                    } else if ("".equals(mSelfCheckData.getSORE_THROAT_AT())) {
                        makeToast(mContext.getString(R.string.not_SORE_THROAT_AT_message));
                    } else if ("".equals(mSelfCheckData.getDYSPNEA_AT())) {
                        makeToast(mContext.getString(R.string.not_DYSPNEA_AT_message));
                    } else if ("".equals(et_diag_bdheat1.getText().toString())) {
                        makeToast(mContext.getString(R.string.not_TEMPERATURE_message));
                    } else if ("".equals(et_diag_bdheat2.getText().toString())) {
                        makeToast(mContext.getString(R.string.not_TEMPERATURE_message));
                    } else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext)
                                .setTitle(mContext.getString(R.string.selfcheck_title))
                                .setMessage(mContext.getString(R.string.selfcheck_submit))
                                .setCancelable(false)
                                .setPositiveButton(mContext.getString(R.string.check), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        String bdheat = et_diag_bdheat1.getText().toString() + "." + et_diag_bdheat2.getText().toString();
                                        String rm = et_diag_etc.getText().toString();
                                        if (".".equals(bdheat)) {
                                            mSelfCheckData.setBDHEAT("");
                                        } else {
                                            mSelfCheckData.setBDHEAT(bdheat);
                                        }
                                        if ("".equals(rm)) {
                                            mSelfCheckData.setRM("");
                                        } else {
                                            mSelfCheckData.setRM(rm);
                                        }
                                        retrofit.private_Service(mApplicationPERU.getHeader(),mRetrofitBody.PERU0009(mApplicationPERU.getUserNumber(), mApplicationPERU.getUserSn(), mSelfCheckData)).enqueue(peru0009_callback);
                                    }
                                })
                                .setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });

                        AlertDialog real = dialog.create();
                        real.show();
                    }
                }
            });
        }

    }

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

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (editable.toString().getBytes().length == 2) {
                et_diag_bdheat2.requestFocus();
            }
        }
    };

    /**
     * Consulta de información de autodiagnóstic Callback
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
                            mSelfCheckData.setDYSPNEA_AT(jsonNullCheck(mJsonObject, "DYSPNEA_AT"));
                            mSelfCheckData.setSORE_THROAT_AT(jsonNullCheck(mJsonObject, "SORE_THROAT_AT"));
                            mSelfCheckData.setCOUGH_AT(jsonNullCheck(mJsonObject, "COUGH_AT"));
                            mSelfCheckData.setPYRXIA_AT(jsonNullCheck(mJsonObject, "PYRXIA_AT"));
                            mSelfCheckData.setSLFDGNSS_DT(jsonNullCheck(mJsonObject, "SLFDGNSS_DT"));
                            mSelfCheckData.setSLFDGNSS_DT_F(jsonNullCheck(mJsonObject, "SLFDGNSS_DT_F"));
                            mSelfCheckData.setRM(jsonNullCheck(mJsonObject, "RM"));
                            mSelfCheckData.setBDHEAT(jsonNullCheck(mJsonObject, "BDHEAT"));
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
                        logE("Error Message - " + mContext.getString(R.string.network_error_message));
                        makeToast(mContext.getString(R.string.network_error_message));
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
     * Registrar información de autodiagnóstico Callback
     */
    public Callback<RetrofitData> peru0009_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getResults() != null) {
                        makeToast(mContext.getString(R.string.success_slf_diagnosis_message));
                        finish();
                    } else {
                        logE("Error Message - " + mContext.getString(R.string.network_error_message));
                        makeToast(mContext.getString(R.string.network_error_message));
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

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rb_diag_tempYes:
                    if (rb_diag_tempYes.isChecked()) {
                        mSelfCheckData.setPYRXIA_AT("Y");
                        rb_diag_tempNo.setChecked(false);
                    } else {
                        mSelfCheckData.setPYRXIA_AT("");
                    }
                    break;
                case R.id.rb_diag_tempNo:
                    if (rb_diag_tempNo.isChecked()) {
                        mSelfCheckData.setPYRXIA_AT("N");
                        rb_diag_tempYes.setChecked(false);
                    } else {
                        mSelfCheckData.setPYRXIA_AT("");
                    }
                    break;
                case R.id.rb_diag_coughYes:
                    if (rb_diag_coughYes.isChecked()) {
                        mSelfCheckData.setCOUGH_AT("Y");
                        rb_diag_coughNo.setChecked(false);
                    } else {
                        mSelfCheckData.setCOUGH_AT("");
                    }
                    break;
                case R.id.rb_diag_coughNo:
                    if (rb_diag_coughNo.isChecked()) {
                        mSelfCheckData.setCOUGH_AT("N");
                        rb_diag_coughYes.setChecked(false);
                    } else {
                        mSelfCheckData.setCOUGH_AT("");
                    }
                    break;
                case R.id.rb_diag_sore_throatYes:
                    if (rb_diag_sore_throatYes.isChecked()) {
                        mSelfCheckData.setSORE_THROAT_AT("Y");
                        rb_diag_sore_throatNo.setChecked(false);
                    } else {
                        mSelfCheckData.setSORE_THROAT_AT("");
                    }
                    break;
                case R.id.rb_diag_sore_throatNo:
                    if (rb_diag_sore_throatNo.isChecked()) {
                        mSelfCheckData.setSORE_THROAT_AT("N");
                        rb_diag_sore_throatYes.setChecked(false);
                    } else {
                        mSelfCheckData.setSORE_THROAT_AT("");
                    }
                    break;
                case R.id.rb_diag_DyspneaYes:
                    if (rb_diag_DyspneaYes.isChecked()) {
                        mSelfCheckData.setDYSPNEA_AT("Y");
                        rb_diag_DyspneaNo.setChecked(false);
                    } else {
                        mSelfCheckData.setDYSPNEA_AT("");
                    }
                    break;
                case R.id.rb_diag_DyspneaNo:
                    if (rb_diag_DyspneaNo.isChecked()) {
                        mSelfCheckData.setDYSPNEA_AT("N");
                        rb_diag_DyspneaYes.setChecked(false);
                    } else {
                        mSelfCheckData.setDYSPNEA_AT("");
                    }
                    break;
            }
        }
    };

}
