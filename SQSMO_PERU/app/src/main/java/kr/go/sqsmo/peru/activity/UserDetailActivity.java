package kr.go.sqsmo.peru.activity;

import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import kr.go.sqsmo.peru.data.servicedata.UserData;
import kr.go.sqsmo.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailActivity extends BaseActivity {

    Toolbar toolbar;
    TextView tv_item_user_name,tv_item_birth,tv_item_self_check_1,tv_item_self_check_2, tv_item_connect_status, tv_item_sex, tv_item_nationality, tv_item_status, tv_item_phone, tv_item_phone2, tv_user_addr;
    TextView tv_item_diagnosis_list, tv_item_phone_call, tv_item_location, tv_item_stats_change, tv_manager_change;

    InsulatorData mInsulatorData;

    private AlertDialog menuDialog;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        mInsulatorData = (InsulatorData) getIntent().getSerializableExtra("DATA");
        bind();
        set();
    }

    @Override
    protected void bind() {
        toolbar = findViewById(R.id.toolbar);
        tv_item_user_name = findViewById(R.id.tv_item_user_name);
        tv_item_birth = findViewById(R.id.tv_item_birth);
        tv_item_self_check_1 = findViewById(R.id.tv_item_self_check_1);
        tv_item_self_check_2 = findViewById(R.id.tv_item_self_check_2);
        tv_item_connect_status = findViewById(R.id.tv_item_connect_status);
        tv_item_sex = findViewById(R.id.tv_item_sex);
        tv_item_nationality = findViewById(R.id.tv_item_nationality);
        tv_item_status = findViewById(R.id.tv_item_status);
        tv_item_phone = findViewById(R.id.tv_item_phone);
        tv_item_phone2 = findViewById(R.id.tv_item_phone2);
        tv_user_addr = findViewById(R.id.tv_user_addr);
        tv_item_diagnosis_list = findViewById(R.id.tv_item_diagnosis_list);
        tv_item_phone_call = findViewById(R.id.tv_item_phone_call);
        tv_item_location = findViewById(R.id.tv_item_location);
        tv_item_stats_change = findViewById(R.id.tv_item_stats_change);
        tv_manager_change = findViewById(R.id.tv_manager_change);
    }

    @Override
    protected void set() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tv_item_user_name.setText(mInsulatorData.getISLPRSN_NM());
        tv_item_birth.setText(mInsulatorData.getBRTHDY_F());
        tv_item_self_check_1.setText(mInsulatorData.getSLFDGNSS_AM_CODE_NM());
        tv_item_self_check_2.setText(mInsulatorData.getSLFDGNSS_PM_CODE_NM());
        tv_item_connect_status.setText(mInsulatorData.getISLPRSN_NTW_STTUS_CODE_NM());
        tv_item_sex.setText(mInsulatorData.getSEXDSTN_CODE_NM());
        tv_item_nationality.setText(getNational(mInsulatorData.getNLTY_CODE()));
        tv_item_status.setText(mInsulatorData.getISLPRSN_STTUS_CODE_NM());
        tv_item_phone.setText(mInsulatorData.getTELNO());
        tv_item_phone2.setText(mInsulatorData.getEMGNC_TELNO());
        tv_user_addr.setText(mInsulatorData.getADDR());
        tv_item_diagnosis_list.setOnClickListener(mOnClickListener);
        tv_item_phone_call.setOnClickListener(mOnClickListener);
        tv_item_location.setOnClickListener(mOnClickListener);
        tv_item_stats_change.setOnClickListener(mOnClickListener);
        tv_manager_change.setOnClickListener(mOnClickListener);

    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_item_diagnosis_list:
                    Intent diagnosis_listIntent = new Intent(mContext, DiagnosisListActivity.class);
                    diagnosis_listIntent.putExtra("ISLPRSN_SN", mInsulatorData.getISLPRSN_SN());
                    startActivity(diagnosis_listIntent);
                    break;
                case R.id.tv_item_phone_call:
                    userPhoneDialog(mInsulatorData.getTELNO(), mInsulatorData.getEMGNC_TELNO());
                    break;
                case R.id.tv_item_location:
                    mContext.startActivity(new Intent(mContext, GoogleMapActivity.class).putExtra("InsulatorData", mInsulatorData));
                    break;
                case R.id.tv_item_stats_change:
                    retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERUC0005()).enqueue(peruc0005_callback);
                    break;
                case R.id.tv_manager_change:
                    managerCheckDialog();
                    break;
                default:
                    break;
            }
        }
    };

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
     * Consulta de información del encargado oficial Callback
     */
    public Callback<RetrofitData> peru0011_callback = new Callback<RetrofitData>() {
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
                                menuDialog.dismiss();
                                UserData mUserData = new UserData();
                                mUserData.setMNGR_NM(jsonNullCheck(mJsonObject, "MNGR_NM"));
                                mUserData.setMBTLNUM(jsonNullCheck(mJsonObject, "MBTLNUM"));
                                mUserData.setOFFM_TELNO(jsonNullCheck(mJsonObject, "OFFM_TELNO"));
                                mUserData.setPSITN_DPRTMNT_CODE(jsonNullCheck(mJsonObject, "PSITN_DPRTMNT_CODE"));
                                mUserData.setPSITN_DPRTMNT_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_DPRTMNT_CODE_NM"));
                                mUserData.setPSITN_PRVNCA_CODE(jsonNullCheck(mJsonObject, "PSITN_PRVNCA_CODE"));
                                mUserData.setPSITN_PRVNCA_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_PRVNCA_CODE_NM"));
                                mUserData.setPSITN_DSTRT_CODE(jsonNullCheck(mJsonObject, "PSITN_DSTRT_CODE"));
                                mUserData.setPSITN_DSTRT_CODE_NM(jsonNullCheck(mJsonObject, "PSITN_DSTRT_CODE_NM"));
                                mUserData.setPSITN_DEPT_NM(jsonNullCheck(mJsonObject, "PSITN_DEPT_NM"));
                                mUserData.setMNGR_SN(jsonNullCheck(mJsonObject, "MNGR_SN"));
                                mUserData.setLOGIN_ID(jsonNullCheck(mJsonObject, "LOGIN_ID"));
                                mUserData.setSKYPE_ID(jsonNullCheck(mJsonObject, "SKYPE_ID"));
                                mUserData.setWHATSUP_ID(jsonNullCheck(mJsonObject, "WHATSUP_ID"));
                                managerDialog(mUserData);
                            } catch (JSONException e) {
                                Log.e(TAG, "peru0011_callback JSON ERROR - " + e);
                            }
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
     * Consulta de información del encargado oficial Dialog
     * @param mUserData Datos del administrador
     */
    public void managerDialog(final UserData mUserData) {
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View menuView = inflater.inflate(R.layout.dialog_manager_info2, nullParent);
        TextView tv_manager_name, tv_manager_phone, tv_manager_hose, tv_manager_part;
        tv_manager_part = menuView.findViewById(R.id.tv_manager_part);
        tv_manager_name = menuView.findViewById(R.id.tv_manager_name);
        tv_manager_phone = menuView.findViewById(R.id.tv_manager_id);
        tv_manager_hose = menuView.findViewById(R.id.tv_manager_phone);
        tv_manager_part.setText(mUserData.getPSITN_DEPT_NM());
        tv_manager_name.setText(mUserData.getMNGR_NM());
        tv_manager_phone.setText(mUserData.getLOGIN_ID());
        tv_manager_hose.setText(Html.fromHtml("<u>" + mUserData.getMBTLNUM() + "</u>"));
        menuView.findViewById(R.id.ll_manager_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
            }
        });
        menuView.findViewById(R.id.ll_manager_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERUO0007(mUserData.getMNGR_SN(), mInsulatorData.getISLPRSN_SN())).enqueue(peruo0007_callback);
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(menuView);
        menuDialog = builder.create();
        menuDialog.getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_dialog));
        menuDialog.show();
    }


    /**
     * Diálogo de verificación de encargado oficial Dialog
     */
    public void managerCheckDialog() {
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View menuView = inflater.inflate(R.layout.dialog_manager_check, nullParent);
        final EditText et_manager_id;

        et_manager_id = menuView.findViewById(R.id.et_manager_id);
        menuView.findViewById(R.id.ll_pin_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(et_manager_id.getText().toString())) {
                    retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERU0011(et_manager_id.getText().toString())).enqueue(peru0011_callback);
                } else {
                    makeToast(mContext.getString(R.string.please_input_manager_id));
                }
            }
        });
        menuView.findViewById(R.id.ll_pin_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuDialog.dismiss();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(menuView);
        menuDialog = builder.create();
        menuDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg_dialog));

        menuDialog.show();
    }

    /**
     * Definición de diálogo de realizar llamadas
     * @param tel1 Número de teléfono de la persona en autocuarentena
     * @param tel2 Número de teléfono del tutor
     */
    private void userPhoneDialog(final String tel1, final String tel2) {
        LayoutInflater inflater = getLayoutInflater();
        final ViewGroup nullParent = null;
        View menuView = inflater.inflate(R.layout.dialog_user_phone, nullParent);
        final TextView tv_user_emergency_phone, tv_user_phone;
        tv_user_phone = menuView.findViewById(R.id.tv_user_phone);
        tv_user_emergency_phone = menuView.findViewById(R.id.tv_user_emergency_phone);
        tv_user_phone.setText(tel1);
        tv_user_emergency_phone.setText(tel2);
        tv_user_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + tel1)));
            }
        });
        tv_user_emergency_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent("android.intent.action.DIAL", Uri.parse("tel:" + tel2)));
            }
        });

        menuView.findViewById(R.id.ll_user_close).setOnClickListener(new View.OnClickListener() {
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


    /**
     * Definición de diálogo de modificación de estado
     * @param item Lista de código de estado
     */
    public void statusChangeDialog(final ArrayList<HashMap<String, String>> item) {
        final String[] code = {""};
        final android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(mContext);
        View mView = getLayoutInflater().inflate(R.layout.dialog_listview2, null);
        TextView tv_dialog_title = mView.findViewById(R.id.tv_dialog_title);
        LinearLayout ll_dialog_close = mView.findViewById(R.id.ll_dialog_close);
        tv_dialog_title.setText(mContext.getString(R.string.status_change));
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
        mView.findViewById(R.id.ll_dialog_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"".equals(code[0])) {
                    retrofit.peruo_Service(mApplicationPERUO.getHeader(), mRetrofitBody.PERUO0005(mInsulatorData.getMNGR_SN(), mInsulatorData.getISLPRSN_SN(), code[0])).enqueue(peruo0005_allback);
                    dialog.dismiss();
                } else {
                    makeToast(mContext.getString(R.string.status_change_message));
                }
            }
        });
        mView.findViewById(R.id.ll_dialog_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ArrayAdapter adapter = new ArrayAdapter(mContext, android.R.layout.select_dialog_item, list);
        lv_list.setAdapter(adapter);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_dialog));

        ll_dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        lv_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                code[0] = item.get(position).get(lv_list.getItemAtPosition(position));
            }
        });
        dialog.show();
    }

    /**
     * Modificar persona en autocuarentena Callback
     */
    public Callback<RetrofitData> peruo0005_allback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            dialog.dismiss();
                            Intent mIntent = new Intent("stats_change");
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                            finish();
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
     * Código de estado Callback
     */
    public Callback<RetrofitData> peruc0005_callback = new Callback<RetrofitData>() {
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
                                    HashMap<String, String> tempMap = new HashMap<>();
                                    tempMap.put(name, code);
                                    statusCode.add(tempMap);
                                }
                                statusChangeDialog(statusCode);
                            } catch (JSONException e) {
                                Log.e(TAG, "peruc0005_callback JSON ERROR - " + e);
                            }

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
     * Cambiar de encargado oficial Callback
     */
    public Callback<RetrofitData> peruo0007_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            menuDialog.dismiss();
                            Intent mIntent = new Intent("stats_change");
                            LocalBroadcastManager.getInstance(mContext).sendBroadcast(mIntent);
                            finish();
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


}
