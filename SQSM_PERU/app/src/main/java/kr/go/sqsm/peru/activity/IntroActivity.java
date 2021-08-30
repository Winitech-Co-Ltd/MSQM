package kr.go.sqsm.peru.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.data.servicedata.KeyData;
import kr.go.sqsm.peru.data.servicedata.LoginServiceData;
import kr.go.sqsm.peru.data.servicedata.UserData;
import kr.go.sqsm.peru.util.AES256;
import kr.go.sqsm.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IntroActivity extends BaseActivity {

    private final String[] Q_USES_PERMISSIONS = {Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    private final String[] O_USES_PERMISSIONS = {Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private final String[] USES_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private Handler nextHandler = new Handler();

    private final int PERMSSION_GRANTED_CODE = 3333;
    private final int SELECT_LANGUAGE_CODE = 2222;

    private int delay = 1500;
    TextView tv_intro_ver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        bind();
        set();

        if (!"".equals(mApplicationPERU.getLanguage())) {
            if ("".equals(mApplicationPERU.getPublicKey()) || "".equals(mApplicationPERU.getPublicVector())) {
                retrofit.key_Service(mRetrofitBody.PERUK0001()).enqueue(PERUK0001_callback);
            } else {
                changeConfiguration(mApplicationPERU.getLanguage());
                startOverlayWindowService();
            }
        } else {
            startActivityForResult(new Intent(mContext, SelectLanguageActivity.class), SELECT_LANGUAGE_CODE);
        }


        if (mApplicationPERU.getLifecycleList() != null) {
            mApplicationPERU.removeAllLifecycleList();
        }
    }


    /**
     * 공개키 Callback
     * Created by jongsuuu on 2020-06-15
     */
    public Callback<KeyData> PERUK0001_callback = new Callback<KeyData>() {
        @Override
        public void onResponse(Call<KeyData> call, Response<KeyData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            mApplicationPERU.setPublicKey(response.body().getResults().substring(0, 16), response.body().getResults().substring(16, 32));
                            AES256.setPublic(mApplicationPERU.getPublicKey(), mApplicationPERU.getPublicVector());
                            changeConfiguration(mApplicationPERU.getLanguage());
                            startOverlayWindowService();
                        } else {
                            makeToast(mContext.getString(R.string.publickey_fail));
                            finish();
                        }
                    } else {
                        makeToast(mContext.getString(R.string.network_error_message));
                        finish();
                    }
                } else {
                    makeToast(mContext.getString(R.string.network_error_message));
                    finish();
                }
            } else {
                makeToast(mContext.getString(R.string.not_data_message));
                finish();
            }
        }

        @Override
        public void onFailure(Call<KeyData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
            finish();
        }
    };

    @Override
    protected void bind() {
        tv_intro_ver = findViewById(R.id.tv_intro_ver);
    }

    @Override
    protected void set() {
        tv_intro_ver.setText("Ver " + mApplicationPERU.getVersion());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mApplicationPERU.addLifecycleList("Intro");
        logE(mApplicationPERU.getLifecycleList().toString());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApplicationPERU.removeLifecycleList("Intro");
        logE(mApplicationPERU.getLifecycleList().toString());
    }

    /**
     * Verificar permiso
     *
     * @param permissions Permission List
     */
    private boolean checkPermissions(String[] permissions) {
        boolean isGranted = false;
        ArrayList<String> notGrantedPermissions = new ArrayList<>();
        for (String permission : permissions) {
            int result = ActivityCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                notGrantedPermissions.add(permission);
            }
        }
        if (notGrantedPermissions.size() < 1) {
            isGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, notGrantedPermissions.toArray(new String[notGrantedPermissions.size()]), 1);

        }
        return isGranted;
    }


    /**
     * Verificar el permiso de dibujar en la aplicación
     */
    public void startOverlayWindowService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(mContext)) {
            permissionDialog();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (checkPermissions(Q_USES_PERMISSIONS)) {
                    nextHandler.postDelayed(mRunnable, delay);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (checkPermissions(O_USES_PERMISSIONS)) {
                    nextHandler.postDelayed(mRunnable, delay);
                }
            } else {
                if (checkPermissions(USES_PERMISSIONS)) {
                    nextHandler.postDelayed(mRunnable, delay);
                }
            }

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PERMSSION_GRANTED_CODE:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            if (checkPermissions(Q_USES_PERMISSIONS)) {
                                nextHandler.postDelayed(mRunnable, delay);
                                Log.e(TAG, "SDK_INT : Q 29");
                            }
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            if (checkPermissions(O_USES_PERMISSIONS)) {
                                nextHandler.postDelayed(mRunnable, delay);
                                Log.e(TAG, "SDK_INT : O 26");
                            }
                        } else {
                            if (checkPermissions(USES_PERMISSIONS)) {
                                nextHandler.postDelayed(mRunnable, delay);
                                Log.e(TAG, "SDK_INT : default");
                            }
                        }
                    } else {
                        exitDialog("Permissoins", mContext.getString(R.string.app_permission_close));
                    }
                }
                break;
            case SELECT_LANGUAGE_CODE:
                if ("".equals(mApplicationPERU.getPublicKey()) || "".equals(mApplicationPERU.getPublicVector())) {
                    retrofit.key_Service(mRetrofitBody.PERUK0001()).enqueue(PERUK0001_callback);
                } else {
                    changeConfiguration(mApplicationPERU.getLanguage());
                    startOverlayWindowService();
                }
                break;
        }
    }

    /**
     * Diálogo de configuración de permisos de la App.
     */
    public void permissionDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setTitle(mContext.getString(R.string.permission_title));
        dialog.setMessage(mContext.getString(R.string.permission_message))
                .setCancelable(false)
                .setPositiveButton(mContext.getString(R.string.permission_success), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, PERMSSION_GRANTED_CODE);
                    }
                })
                .setNegativeButton(mContext.getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });

        AlertDialog real = dialog.create();
        real.show();
    }

    /**
     * Verificar permiso
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            ArrayList<String> userNotAllowdPermissions = new ArrayList<>();
            boolean isGranted = false;
            if (grantResults.length > 0) {
                isGranted = true;
                int index = 0;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        isGranted = false;
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[index])) {
                            userNotAllowdPermissions.add(permissions[index]);
                        }
                    }
                    index++;
                }
            }
            if (isGranted) {
                nextHandler.postDelayed(mRunnable, delay);
            } else {
                if (userNotAllowdPermissions.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (String permission : userNotAllowdPermissions) {
                        builder.append("\n").append(permission);
                    }
                    PermissionSetDialog(builder.toString());
                } else {
                    exitDialog("Permissoins", mContext.getString(R.string.app_permission_close));
                }
            }
        }
    }


    /**
     * Diálogo de configuración de permisos de la App.
     *
     * @param s
     */
    private void PermissionSetDialog(String s) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(mContext.getString(R.string.app_permission));
        mBuilder.setMessage(s);
        mBuilder.setPositiveButton(mContext.getString(R.string.check), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent app = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                app.addCategory(Intent.CATEGORY_DEFAULT);
                app.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(app);
                finish();
            }
        });
        mBuilder.setNegativeButton(mContext.getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        mBuilder.create().show();
    }

    /**
     * Main Runnable
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (!"".equals(mApplicationPERU.getManagerId())) {
                if (!"".equals(mApplicationPERU.getUserNumber())) {
                    if (!"".equals(mApplicationPERU.getPrivateKey())) {
                        retrofit.private_Service(mApplicationPERU.getHeader(), mRetrofitBody.PERU0004(mApplicationPERU.getUserNumber(), mApplicationPERU.getUserSn())).enqueue(peru0004_callback);
                    } else {
                        retrofit.login2_Service(AES256.publicEncrypt(mApplicationPERU.getUserNumber()), mRetrofitBody.PERUTM0001(mApplicationPERU.getUserNumber(), mApplicationPERU.getUserSn())).enqueue(PERUTM0001_callback);
                    }
                } else {
                    startActivity(new Intent(mContext, AgreeActivity.class));
                    finish();
                }
            } else {
                startActivity(new Intent(mContext, AgreeActivity.class));
                finish();
            }
        }
    };
    /**
     * Consulta de información registrada de la persona en autocuarentena Callback
     */
    public Callback<LoginServiceData> PERUTM0001_callback = new Callback<LoginServiceData>() {
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
                                mApplicationPERU.setPrivateKey(jsonNullCheck(mJsonObject, "ENCPT_DECD_KEY").substring(0, 16), jsonNullCheck(mJsonObject, "ENCPT_DECD_KEY").substring(16, 32));
                                AES256.setPrivate(mApplicationPERU.getPrivateKey(), mApplicationPERU.getPrivateVector());
                                mApplicationPERU.setHeader(AES256.publicEncrypt(mUserData.getISLPRSN_SN()));
                                mApplicationPERU.setUserData(mUserData);
                                makeToast(mContext.getString(R.string.user_certification_success));
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                            } catch (JSONException e) {
                                Log.e(TAG, "PERUTM0001_callback JSON ERROR - " + e);
                            }
                        } else {
                            makeToast(mContext.getString(R.string.network_error_message));
                            startActivity(new Intent(mContext, AgreeActivity.class));
                            finish();
                        }
                    } else {
                        makeToast(mContext.getString(R.string.network_error_message));
                        finish();
                    }
                } else {
                    makeToast(mContext.getString(R.string.network_error_message));
                    finish();
                }
            } else {
                makeToast(mContext.getString(R.string.not_data_message));
                finish();
            }
        }

        @Override
        public void onFailure(Call<LoginServiceData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
            finish();
        }
    };


    /**
     * Consulta de información registrada de la persona en autocuarentena Callback
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
                                mApplicationPERU.setUserData(mUserData);
                                makeToast(mContext.getString(R.string.user_certification_success));
                                startActivity(new Intent(mContext, MainActivity.class));
                                finish();
                            } catch (JSONException e) {
                                Log.e(TAG, "peru0004_callback JSON ERROR - " + e);
                            }
                        } else {
                            makeToast(mContext.getString(R.string.network_error_message));
                            startActivity(new Intent(mContext, AgreeActivity.class));
                            finish();
                        }
                    } else {
                        makeToast(mContext.getString(R.string.network_error_message));
                        finish();
                    }
                } else {
                    makeToast(mContext.getString(R.string.network_error_message));
                    finish();
                }
            } else {
                makeToast(mContext.getString(R.string.not_data_message));
                finish();
            }
        }

        @Override
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
            finish();
        }
    };


}
