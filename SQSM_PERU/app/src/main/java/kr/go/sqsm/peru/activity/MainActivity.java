package kr.go.sqsm.peru.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.ManagerData;
import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.service.ForegroundService;
import kr.go.sqsm.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends BaseActivity {


    private TextView tv_main_selfcheck, tv_main_lastTime, iv_main_selfcheck, tv_main_ver;

    private Button bt_main_list, bt_main_screening_clinic, bt_main_admin, bt_main_107;
    private Button bt_main_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!isMyServiceRunning(ForegroundService.class)) {
            startLocationService();
        }
        bind();
        set();
    }


    /**
     * Verificar estado
     *
     * @param serviceClass ForegroundService
     */
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        appVersionCheck();
        mApplicationPERU.removeAllLifecycleList();
        mApplicationPERU.addLifecycleList("Main");
    }

    @Override
    protected void onResume() {
        super.onResume();
        retrofit.private_Service(mApplicationPERU.getHeader(), mRetrofitBody.PERU0008(mApplicationPERU.getUserNumber(), mApplicationPERU.getUserSn())).enqueue(peru0008_callback);

        appUpdateStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplicationPERU.removeAllLifecycleList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Update flow failed! Result code: " + resultCode);
                } else {
                    finishAffinity();
                    startActivity(new Intent(mContext, IntroActivity.class));
                    Log.e(TAG, "Update flow success!");
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Consulta de información de registro de la persona en autocuarentena Callback
     */
    public Callback<RetrofitData> peru0008_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if ("EI0002".equals(response.body().getRes_cd())) {
                        makeToast(mContext.getString(R.string.privatekey_fail));
                        Intent mIntent = new Intent(mContext, IntroActivity.class);
                        startActivity(mIntent);
                        finish();
                        stopLocationService();
                        mApplicationPERU.setPrivateKey("", "");
                        mApplicationPERU.setPublicKey("", "");
                        mApplicationPERU.setHeader("");
                    } else if (response.body().getResults() != null) {
                        try {
                            JSONArray mJsonArray = new JSONArray(response.body().getResults().toString());
                            if (mJsonArray.length() > 0) {
                                JSONObject mJsonObject = (JSONObject) mJsonArray.get(mJsonArray.length() - 1);
                                if (mJsonObject.length() > 0) {
                                    if ("Y".equals(mJsonObject.getString("SLFDGNSS_AT"))) {
                                        tv_main_selfcheck.setBackground(getResources().getDrawable(R.drawable.check_btn_blue));
                                        iv_main_selfcheck.setBackground(getResources().getDrawable(R.drawable.ic_user_check_blue));
                                        tv_main_selfcheck.setTextColor(getResources().getColor(R.color.colorPrimary));
                                    } else {
                                        tv_main_selfcheck.setBackground(getResources().getDrawable(R.drawable.check_btn_red));
                                        iv_main_selfcheck.setBackground(getResources().getDrawable(R.drawable.ic_user_check_red));
                                        tv_main_selfcheck.setTextColor(getResources().getColor(R.color.colorRed));
                                    }
                                    if ("".equals(jsonNullCheck(mJsonObject, "SLFDGNSS_DT"))) {
                                        tv_main_lastTime.setText("-");
                                    } else {
                                        tv_main_lastTime.setText(jsonNullCheck(mJsonObject, "SLFDGNSS_DT_F"));
                                    }
                                } else {
                                    makeToast(mContext.getString(R.string.not_last_list_msg));
                                }
                            } else {
                                makeToast(mContext.getString(R.string.not_last_list_msg));
                            }

                        } catch (JSONException e) {
                            Log.e(TAG, "peru0008_callback JSON ERROR - " + e);
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
    protected void bind() {
        tv_main_ver = findViewById(R.id.tv_main_ver);

        bt_main_info = findViewById(R.id.bt_main_info);
        tv_main_selfcheck = findViewById(R.id.tv_main_selfcheck);
        tv_main_lastTime = findViewById(R.id.tv_main_lastTime);

        bt_main_list = findViewById(R.id.bt_main_list);
        bt_main_screening_clinic = findViewById(R.id.bt_main_screening_clinic);
        bt_main_admin = findViewById(R.id.bt_main_admin);
        bt_main_107 = findViewById(R.id.bt_main_107);

        iv_main_selfcheck = findViewById(R.id.iv_main_selfcheck);

    }

    @Override
    protected void set() {
        bt_main_info.setOnClickListener(mOnClickListener);
        tv_main_selfcheck.setOnClickListener(mOnClickListener);
        bt_main_list.setOnClickListener(mOnClickListener);
        bt_main_list.setTextSize(15);
        bt_main_screening_clinic.setOnClickListener(mOnClickListener);
        bt_main_admin.setOnClickListener(mOnClickListener);
        bt_main_107.setOnClickListener(mOnClickListener);
        tv_main_ver.setText("Ver " + mApplicationPERU.getVersion());
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_main_selfcheck:
                    startActivity(new Intent(mContext, SelfDiagnosisActivity.class));
                    break;
                case R.id.bt_main_list:
                    startActivity(new Intent(mContext, DiagnosisListActivity.class));
                    break;
                case R.id.bt_main_info:
                    startActivity(new Intent(mContext, ModifyActivity.class));
                    break;
                case R.id.bt_main_admin:
                    retrofit.private_Service(mApplicationPERU.getHeader(), mRetrofitBody.PERU0011("", "", mApplicationPERU.getUserNumber())).enqueue(peru0011_callback);
                    break;
                case R.id.bt_main_107:
                    startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:107")));
                    break;
                case R.id.bt_main_screening_clinic:
                    startActivity(new Intent(mContext, ScreeningClinicActivity.class));
                default:
                    break;
            }
        }
    };


    /**
     * Consulta de información del encargado oficial Callback
     */
    public Callback<RetrofitData> peru0011_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getResults() != null) {
                        try {
                            JSONArray mJsonArray = new JSONArray(response.body().getResults().toString());
                            JSONObject mJsonObject = (JSONObject) mJsonArray.get(mJsonArray.length() - 1);
                            ManagerData managerData = new ManagerData();
                            managerData.setMNGR_NM(jsonNullCheck(mJsonObject, "MNGR_NM"));
                            managerData.setMBTLNUM(jsonNullCheck(mJsonObject, "MBTLNUM"));
                            managerData.setOFFM_TELNO(jsonNullCheck(mJsonObject, "OFFM_TELNO"));
                            managerData.setPSITN_DPRTMNT_CODE(jsonNullCheck(mJsonObject, "PSITN_DPRTMNT_CODE"));
                            managerData.setPSITN_DPRTMNT_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_DPRTMNT_CODE_NM"));
                            managerData.setPSITN_PRVNCA_CODE(jsonNullCheck(mJsonObject, "PSITN_PRVNCA_CODE"));
                            managerData.setPSITN_PRVNCA_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_PRVNCA_CODE_NM"));
                            managerData.setPSITN_DSTRT_CODE(jsonNullCheck(mJsonObject, "PSITN_DSTRT_CODE"));
                            managerData.setPSITN_DSTRT_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_DSTRT_CODE_NM"));
                            managerData.setPSITN_DEPT_NM(jsonNullCheck(mJsonObject, "PSITN_DEPT_NM"));
                            managerData.setMNGR_SN(jsonNullCheck(mJsonObject, "MNGR_SN"));
                            managerData.setLOGIN_ID(jsonNullCheck(mJsonObject, "LOGIN_ID"));
                            managerData.setSKYPE_ID(jsonNullCheck(mJsonObject, "SKYPE_ID"));
                            managerData.setWHATSUP_ID(jsonNullCheck(mJsonObject, "WHATSUP_ID"));
                            managerDialog(managerData);
                        } catch (JSONException e) {
                            Log.e(TAG, "peru0011_callback JSON ERROR - " + e);
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

    /**
     * Siempre verificar
     * Location Service Start
     */
    private void startLocationService() {
        Intent mIntent = new Intent(mContext, ForegroundService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(mIntent);
        } else {
            startService(mIntent);
        }
    }

    /**
     * Siempre verificar
     * Location Service Stop
     */
    private void stopLocationService() {
        Intent mIntent = new Intent(mContext, ForegroundService.class);
        stopService(mIntent);
    }

    /**
     * Consulta de información del encargado oficial Dialog
     *
     * @param data
     */
    private void managerDialog(final ManagerData data) {
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View menuView = inflater.inflate(R.layout.dialog_manager_info, nullParent);
        TextView tv_manager_name, tv_manager_phone, tv_manager_rank, tv_manager_hose, tv_manager_part;
        tv_manager_part = menuView.findViewById(R.id.tv_manager_part);
        tv_manager_name = menuView.findViewById(R.id.tv_manager_name);
        tv_manager_phone = menuView.findViewById(R.id.tv_manager_phone);
        tv_manager_hose = menuView.findViewById(R.id.tv_manager_hose);

        tv_manager_part.setText("".equals(data.getPSITN_DEPT_NM()) ? "-" : data.getPSITN_DEPT_NM());
        tv_manager_name.setText("".equals(data.getMNGR_NM()) ? "-" : data.getMNGR_NM());
        tv_manager_phone.setText(Html.fromHtml("<u>" + data.getMBTLNUM() + "</u>"));
        tv_manager_hose.setText(Html.fromHtml("<u>" + data.getOFFM_TELNO() + "</u>"));
        tv_manager_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + data.getMBTLNUM())));
            }
        });
        tv_manager_hose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + data.getOFFM_TELNO())));
            }
        });
        menuView.findViewById(R.id.ll_manager_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(menuView);
        menuDialog = builder.create();
        menuDialog.getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_dialog));
        menuDialog.show();
    }
}
