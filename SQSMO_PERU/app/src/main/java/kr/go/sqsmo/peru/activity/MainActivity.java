package kr.go.sqsmo.peru.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.adapter.UserAdapter;
import kr.go.sqsmo.peru.data.RetrofitData;
import kr.go.sqsmo.peru.data.servicedata.InsulatorData;
import kr.go.sqsmo.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {
    RecyclerView rv_supervise_list;
    UserAdapter mUserAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    int page = 1, total;
    TextView tv_insulator_ver, tv_insulator_count, tv_insulator_refresh;
    LinearLayout ll_logout, ll_id_check;
    TextView tv_more;
    boolean user0002_flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sigungu_list);
        if (mApplicationPERUO.getmUserData() != null) {

            bind();
            set();
            if (mApplicationPERUO.getLifecycleList().size() > 0) {
                mApplicationPERUO.removeAllLifecycleList();
            }
            mApplicationPERUO.addLifecycleList("Main");
            logE("LifeCycle - " + mApplicationPERUO.getLifecycleList().toString());
            mUserAdapter = new UserAdapter(mContext, this, mApplicationPERUO.getmUserData());
            rv_supervise_list.setAdapter(mUserAdapter);
        } else {
            makeToast(mContext.getString(R.string.login_error_message));
            startActivity(new Intent(mContext, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        appVersionCheck();
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateStatus();
        mUserAdapter = new UserAdapter(mContext, this, mApplicationPERUO.getmUserData());
        rv_supervise_list.setAdapter(mUserAdapter);
        page = 1;
        if (!user0002_flag) {
            user0002_flag = true;
            retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERUO0002(mApplicationPERUO.getmUserData().getMNGR_SN(), String.valueOf(page))).enqueue(peruo0002_callback);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MY_REQUEST_CODE:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Update Fail.\nResult code: " + resultCode);
                } else {
                    finishAffinity();
                    startActivity(new Intent(mContext, IntroActivity.class));
                    Log.e(TAG, "Update Success.");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mLocalBroadcastReceiver);
        mApplicationPERUO.removeAllLifecycleList();
    }

    @Override
    protected void bind() {
        rv_supervise_list = findViewById(R.id.rv_supervise_list);
        ll_logout = findViewById(R.id.ll_logout);
        ll_id_check = findViewById(R.id.ll_id_check);

        mUserAdapter = new UserAdapter(mContext, this, mApplicationPERUO.getmUserData());
        mLayoutManager = new LinearLayoutManager(mContext);

        tv_insulator_ver = findViewById(R.id.tv_insulator_ver);
        tv_insulator_count = findViewById(R.id.tv_insulator_count);
        tv_insulator_refresh = findViewById(R.id.tv_insulator_refresh);
        tv_more = findViewById(R.id.tv_more);
    }

    @Override
    protected void set() {
        ll_logout.setOnClickListener(mOnClickListener);
        ll_id_check.setOnClickListener(mOnClickListener);
        rv_supervise_list.setLayoutManager(mLayoutManager);
        rv_supervise_list.setAdapter(mUserAdapter);
        tv_insulator_ver.setText("Ver " + mApplicationPERUO.getVersion());
        tv_insulator_refresh.setOnClickListener(mOnClickListener);
        tv_more.setOnClickListener(mOnClickListener);

        LocalBroadcastManager.getInstance(mContext).registerReceiver(mLocalBroadcastReceiver, new IntentFilter("stats_change"));
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_logout:
                    retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERUO0006(mApplicationPERUO.getmUserData().getMNGR_SN(), mApplicationPERUO.getLoginId(), "N")).enqueue(peruo0006_callback);
                    break;
                case R.id.ll_id_check:
                    managerDialog();
                    break;
                case R.id.tv_insulator_refresh:
                    insulatorRefresh();
                    break;

                case R.id.tv_more:
                    if (total >= page) {
                        if (!user0002_flag) {
                            user0002_flag = true;
                            retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERUO0002(mApplicationPERUO.getmUserData().getMNGR_SN(), String.valueOf(page))).enqueue(peruo0002_callback);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Actualizar la lista de personas en autocuarentena
     */
    private void insulatorRefresh() {
        page = 1;
        mUserAdapter = new UserAdapter(mContext, this, mApplicationPERUO.getmUserData());
        rv_supervise_list.setAdapter(mUserAdapter);
        if (!user0002_flag) {
            user0002_flag = true;
            retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERUO0002(mApplicationPERUO.getmUserData().getMNGR_SN(), String.valueOf(page))).enqueue(peruo0002_callback);
        }
        tv_insulator_refresh.setEnabled(false);

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_insulator_refresh.setText(mContext.getString(R.string.refresh) + " " + ((millisUntilFinished / 1000L) + 1));
            }

            @Override
            public void onFinish() {
                tv_insulator_refresh.setEnabled(true);
                tv_insulator_refresh.setText(mContext.getString(R.string.refresh));
            }
        }.start();
    }

    /**
     * LocalBroadcastReceiver Definición
     */
    private BroadcastReceiver mLocalBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case "stats_change":
                    page = 1;
                    if (!user0002_flag) {
                        user0002_flag = true;
                        retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERUO0002(mApplicationPERUO.getmUserData().getMNGR_SN(), String.valueOf(page))).enqueue(peruo0002_callback);
                    }
                    break;
            }

        }
    };


    /**
     * Login(ingresar)/Logout(salir) callback
     */
    public Callback<RetrofitData> peruo0006_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            mApplicationPERUO.logout();

                            makeToast(mContext.getString(R.string.logout_success));
                            finish();
                            startActivity(new Intent(mContext, LoginActivity.class));
                        } else {
                            makeToast(response.body().getRes_msg());
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
     * Consulta de lista de personas en autocuarentena Callback
     */
    public Callback<RetrofitData> peruo0002_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            user0002_flag = false;
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getResults() != null) {
                        try {
                            JSONArray mJsonArray = new JSONArray(response.body().getResults().toString());
                            if (mJsonArray.length() > 0) {
                                for (int i = 0; i < mJsonArray.length(); i++) {
                                    JSONObject mJsonObject = (JSONObject) mJsonArray.get(i);
                                    InsulatorData mInsulatorData = new InsulatorData();
                                    mInsulatorData.setISLPRSN_SN(jsonNullCheck(mJsonObject, "ISLPRSN_SN"));
                                    mInsulatorData.setISLPRSN_NM(jsonNullCheck(mJsonObject, "ISLPRSN_NM"));
                                    mInsulatorData.setSEXDSTN_CODE(jsonNullCheck(mJsonObject, "SEXDSTN_CODE"));
                                    mInsulatorData.setSEXDSTN_CODE_NM(jsonNullCheck(mJsonObject, "SEXDSTN_CODE_NM"));
                                    mInsulatorData.setNLTY_CODE(jsonNullCheck(mJsonObject, "NLTY_CODE"));
                                    mInsulatorData.setPSPRNBR(jsonNullCheck(mJsonObject, "PSPRNBR"));
                                    mInsulatorData.setINHT_ID(jsonNullCheck(mJsonObject, "INHT_ID"));
                                    mInsulatorData.setISLLC_XCNTS(jsonNullCheck(mJsonObject, "ISLLC_XCNTS"));
                                    mInsulatorData.setISLLC_YDNTS(jsonNullCheck(mJsonObject, "ISLLC_YDNTS"));
                                    mInsulatorData.setMNGR_SN(jsonNullCheck(mJsonObject, "MNGR_SN"));
                                    mInsulatorData.setTELNO(jsonNullCheck(mJsonObject, "TELNO"));
                                    mInsulatorData.setEMGNC_TELNO(jsonNullCheck(mJsonObject, "EMGNC_TELNO"));
                                    mInsulatorData.setSLFDGNSS_AM_CODE(jsonNullCheck(mJsonObject, "SLFDGNSS_AM_CODE"));
                                    mInsulatorData.setSLFDGNSS_PM_CODE(jsonNullCheck(mJsonObject, "SLFDGNSS_PM_CODE"));
                                    mInsulatorData.setSLFDGNSS_AM_CODE_NM(jsonNullCheck(mJsonObject, "SLFDGNSS_AM_CODE_NM"));
                                    mInsulatorData.setSLFDGNSS_PM_CODE_NM(jsonNullCheck(mJsonObject, "SLFDGNSS_PM_CODE_NM"));
                                    mInsulatorData.setSLFDGNSS_GUBN_AT(jsonNullCheck(mJsonObject, "SLFDGNSS_GUBN_AT"));
                                    mInsulatorData.setISLPRSN_STTUS_CODE(jsonNullCheck(mJsonObject, "ISLPRSN_STTUS_CODE"));
                                    mInsulatorData.setISLPRSN_STTUS_CODE_NM(jsonNullCheck(mJsonObject, "ISLPRSN_STTUS_CODE_NM"));
                                    mInsulatorData.setISLPRSN_XCNTS(jsonNullCheck(mJsonObject, "ISLPRSN_XCNTS"));
                                    mInsulatorData.setISLPRSN_YDNTS(jsonNullCheck(mJsonObject, "ISLPRSN_YDNTS"));
                                    mInsulatorData.setTRMNL_SN(jsonNullCheck(mJsonObject, "TRMNL_SN"));
                                    mInsulatorData.setDISTANCE(jsonNullCheck(mJsonObject, "DISTANCE"));
                                    mInsulatorData.setISLPRSN_NTW_STTUS_CODE(jsonNullCheck(mJsonObject, "ISLPRSN_NTW_STTUS_CODE"));
                                    mInsulatorData.setISLPRSN_NTW_STTUS_CODE_NM(jsonNullCheck(mJsonObject, "ISLPRSN_NTW_STTUS_CODE_NM"));
                                    mInsulatorData.setITEM_OPEN_AT(jsonNullCheck(mJsonObject, "ITEM_OPEN_AT"));
                                    mInsulatorData.setISLLC_DPRTMNT_CODE(jsonNullCheck(mJsonObject, "ISLLC_DPRTMNT_CODE"));
                                    mInsulatorData.setISLLC_DPRTMNT_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_DPRTMNT_CODE_NM"));
                                    mInsulatorData.setISLLC_DSTRT_CODE(jsonNullCheck(mJsonObject, "ISLLC_DSTRT_CODE"));
                                    mInsulatorData.setISLLC_DSTRT_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_DSTRT_CODE_NM"));
                                    mInsulatorData.setISLLC_PRVNCA_CODE(jsonNullCheck(mJsonObject, "ISLLC_PRVNCA_CODE"));
                                    mInsulatorData.setISLLC_PRVNCA_CODE_NM(jsonNullCheck(mJsonObject, "ISLLC_PRVNCA_CODE_NM"));
                                    mInsulatorData.setISLLC_ETC_ADRES(jsonNullCheck(mJsonObject, "ISLLC_ETC_ADRES"));
                                    mInsulatorData.setLC_TRNSMIS_USE_AT(jsonNullCheck(mJsonObject, "LC_TRNSMIS_USE_AT"));
                                    mInsulatorData.setBRTHDY(jsonNullCheck(mJsonObject, "BRTHDY"));
                                    mInsulatorData.setADDR(jsonNullCheck(mJsonObject, "ADDR"));
                                    mInsulatorData.setBRTHDY_F(jsonNullCheck(mJsonObject, "BRTHDY_F"));
                                    mUserAdapter.setItem(mInsulatorData);
                                    if (!"".equals(jsonNullCheck(mJsonObject, "TOT_PAGE"))) {
                                        total = Integer.parseInt(jsonNullCheck(mJsonObject, "TOT_PAGE"));
                                    }
                                    if (!"".equals(jsonNullCheck(mJsonObject, "TOT_CNT"))) {
                                        if ("0".equals(jsonNullCheck(mJsonObject, "TOT_CNT"))) {
                                            tv_insulator_count.setText(mContext.getString(R.string.designated_insulator) + " : 0");
                                        } else {
                                            tv_insulator_count.setText(mContext.getString(R.string.designated_insulator) + " : " + jsonNullCheck(mJsonObject, "TOT_CNT"));
                                        }
                                    } else {
                                        tv_insulator_count.setText(mContext.getString(R.string.designated_insulator) + " : 0");
                                    }

                                }
                                if (total > page) {
                                    tv_more.setVisibility(View.VISIBLE);
                                    page++;
                                } else if (total == page) {
                                    tv_more.setVisibility(View.GONE);
                                    page++;
                                }
                                mUserAdapter.notifyDataSetChanged();
                            } else {
                                tv_insulator_count.setText(mContext.getString(R.string.designated_insulator) + " : 0");
                                mUserAdapter.notifyDataSetChanged();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "peruo0002_callback JSON ERROR - " + e);
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
            user0002_flag = false;
        }
    };

    /**
     * Diálogo de consulta de información de encargado oficial
     */
    private void managerDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View menuView = inflater.inflate(R.layout.dialog_manager_info, nullParent);
        TextView tv_manager_name, tv_manager_id, tv_manager_hose, tv_manager_part;
        tv_manager_part = menuView.findViewById(R.id.tv_manager_part);
        tv_manager_name = menuView.findViewById(R.id.tv_manager_name);
        tv_manager_id = menuView.findViewById(R.id.tv_manager_id);
        tv_manager_hose = menuView.findViewById(R.id.tv_manager_phone);
        tv_manager_part.setText("".equals(mApplicationPERUO.getmUserData().getPSITN_DEPT_NM()) ? "-" : mApplicationPERUO.getmUserData().getPSITN_DEPT_NM());
        tv_manager_name.setText("".equals(mApplicationPERUO.getmUserData().getMNGR_NM()) ? "-" : mApplicationPERUO.getmUserData().getMNGR_NM());
        tv_manager_id.setText(mApplicationPERUO.getLoginId());
        tv_manager_hose.setText(Html.fromHtml("<u>" + mApplicationPERUO.getmUserData().getMBTLNUM() + "</u>"));
        tv_manager_hose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + mApplicationPERUO.getmUserData().getMBTLNUM())));
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mBackPressCloseHandler.onBackPressed();
        }
        return false;
    }
}
