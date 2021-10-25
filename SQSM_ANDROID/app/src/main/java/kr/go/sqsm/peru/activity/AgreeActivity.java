package kr.go.sqsm.peru.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.pdfview.PDFView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.servicedata.LoginServiceData;
import kr.go.sqsm.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgreeActivity extends BaseActivity {
    CheckBox tb_agree_all;
    LinearLayout ll_access_user_agree, ll_access_user_not_agree;
    ToggleButton rb_agree_peru, rb_agree_etc;
    TextView tv_agree_national;
    String national = "";
    PDFView pdf_viewer;

    /**
     * Consulta de información del encargado oficial Callback
     */
    public Callback<LoginServiceData> peru0013_callback = new Callback<LoginServiceData>() {
        @Override
        public void onResponse(Call<LoginServiceData> call, Response<LoginServiceData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            try {
                                JSONArray array = new JSONArray(response.body().getResults());
                                JSONObject mJsonObject = (JSONObject) array.get(array.length() - 1);
                                Intent mIntent = new Intent(mContext, EnrollmentActivity.class);
                                mIntent.putExtra("national", national);
                                mApplicationPERU.setManager(jsonNullCheck(mJsonObject, "MNGR_SN"), jsonNullCheck(mJsonObject, "LOGIN_ID"));
                                makeToast(mContext.getString(R.string.user_certification_success));
                                mContext.startActivity(mIntent);
                                menuDialog.dismiss();
                                finish();
                            } catch (JSONException e) {
                                Log.e(TAG, "peru0013_callback JSON ERROR - " + e);
                            }
                        } else {
//                            makeToast(mContext.getString(R.string.network_error_message));
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
        public void onFailure(Call<LoginServiceData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };
    AlertDialog dialog;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tb_agree_all:
                    if (!tb_agree_all.isChecked()) {
                        makeToast(mContext.getString(R.string.agree_cancel));
                    }
                    break;
                case R.id.ll_access_user_agree:
                    if ("".equals(national)) {
                        makeToast(mContext.getString(R.string.not_NLTY_CODE_message));
                    } else if (!tb_agree_all.isChecked()) {
                        makeToast(mContext.getString(R.string.agree_cancel));
                    } else {
                        managerCheckDialog();
                    }
                    break;
                case R.id.ll_access_user_not_agree:
                    mBackPressCloseHandler.onBackPressed();
                    break;

                case R.id.rb_agree_peru:
                    rb_agree_peru.setChecked(true);
                    rb_agree_etc.setChecked(false);
                    tv_agree_national.setVisibility(View.GONE);
                    tv_agree_national.setText("");

                    tb_agree_all.setChecked(false);

                    national = "PE";
                    break;
                case R.id.rb_agree_etc:
                    rb_agree_peru.setChecked(false);
                    rb_agree_etc.setChecked(true);
                    tv_agree_national.setVisibility(View.VISIBLE);

                    tb_agree_all.setChecked(false);

                    national = "";
                    break;
                case R.id.tv_agree_national:
                    codeDialog("country", mApplicationPERU.getCountryList());
                    break;
                default:
                    break;

            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agree);
        if (mApplicationPERU.getCountryList().size() == 0) {
            mApplicationPERU.setCountry();
        }
        bind();
        set();
    }

    @Override
    protected void bind() {
        tb_agree_all = findViewById(R.id.tb_agree_all);
        ll_access_user_agree = findViewById(R.id.ll_access_user_agree);
        ll_access_user_not_agree = findViewById(R.id.ll_access_user_not_agree);

        rb_agree_peru = findViewById(R.id.rb_agree_peru);
        rb_agree_etc = findViewById(R.id.rb_agree_etc);
        tv_agree_national = findViewById(R.id.tv_agree_national);
        pdf_viewer = findViewById(R.id.pdf_viewer);
    }

    @Override
    protected void set() {
        pdf_viewer.fromAsset("Política-de-Privacidad.pdf").show();

        tb_agree_all.setOnClickListener(mOnClickListener);
        ll_access_user_agree.setOnClickListener(mOnClickListener);
        ll_access_user_not_agree.setOnClickListener(mOnClickListener);

        rb_agree_peru.setOnClickListener(mOnClickListener);
        rb_agree_etc.setOnClickListener(mOnClickListener);
        tv_agree_national.setOnClickListener(mOnClickListener);
    }


    /**
     * Definición de diálogo de código
     * @param serviceName Nombre del servicio de código
     * @param item Lista de resultados del servicio de código
     */
    private void codeDialog(final String serviceName, final ArrayList<HashMap<String, String>> item) {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        View mView = getLayoutInflater().inflate(R.layout.dialog_listview, null);
        if (serviceName.equals("country")) {
            mBuilder.setTitle(mContext.getString(R.string.gender_national_ex));
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
                if (serviceName.equals("country")) {
                    tv_agree_national.setText(list.get(position));
                    national = item.get(position).get(lv_list.getItemAtPosition(position));
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mBackPressCloseHandler.onBackPressed();
        }
        return false;
    }

    /**
     * Manager Check Dialog
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
                    retrofit.login_Service(mRetrofitBody.PERU0013("", et_manager_id.getText().toString(), "")).enqueue(peru0013_callback);
                } else {
                    makeToast(mContext.getString(R.string.not_manager_message));
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
        builder.setCancelable(false); // dialog 밖 터치 종료 x
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        mBackPressCloseHandler.onBackPressedDialog(menuDialog);
                        return true;
                    }
                }
                return false;
            }
        });
        menuDialog = builder.create();
        menuDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.bg_dialog));

        menuDialog.show();
    }
}
