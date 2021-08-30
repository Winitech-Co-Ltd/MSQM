package kr.go.sqsmo.peru.activity;

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

import java.util.ArrayList;

import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.data.KeyData;
import kr.go.sqsmo.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class IntroActivity extends BaseActivity {

    private final String[] Q_USES_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION};
    private final String[] O_USES_PERMISSIONS = {Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION};
    private final String[] USES_PERMISSIONS = {Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
    private final int OVERLAY_PERMISSION_ALLOW = 3333;

    TextView tv_intro_ver;
    private Handler nextHandler = new Handler();
    private int delay = 1500;

    /**
     * Main Runnable
     */
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
                startActivity(new Intent(mContext, LoginActivity.class));
                finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        bind();
        set();
        if ("".equals(mApplicationPERUO.getPublicKey()) || "".equals(mApplicationPERUO.getPublicVector())) {
            retrofit.key_Service(mRetrofitBody.PERUK0001()).enqueue(PERUK0001_callback);
        } else {
            startOverlayWindowService();
        }
        if (mApplicationPERUO.getLifecycleList() != null) {
            mApplicationPERUO.removeAllLifecycleList();
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
                            mApplicationPERUO.setPublicKey(response.body().getResults().substring(0, 16), response.body().getResults().substring(16, 32));
                            startOverlayWindowService();
                        } else {
                            makeToast(mContext.getString(R.string.network_error_message));
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
        tv_intro_ver.setText("Ver " + mApplicationPERUO.getVersion());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mApplicationPERUO.addLifecycleList("Intro");
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApplicationPERUO.removeLifecycleList("Intro");
    }

    /**
     * Verificar permiso
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
            case OVERLAY_PERMISSION_ALLOW:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (Settings.canDrawOverlays(this)) {
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
                    } else {
                        exitDialog("Permissoins", mContext.getString(R.string.app_permission_close));
                    }
                }
                break;
        }

    }

    /**
     * Verificar el permiso de dibujar en la aplicación
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
                        startActivityForResult(intent, OVERLAY_PERMISSION_ALLOW);
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
                    exitDialog("Permissoins", mContext.getString(R.string.permission_contents));
                }
            }
        }
    }

    /**
     * Diálogo de configuración de permisos de la App.
     */
    private void PermissionSetDialog(String s) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(mContext.getString(R.string.please_allow_permission_app_info));
        mBuilder.setMessage(s);
        mBuilder.setPositiveButton(mContext.getString(R.string.PIN_AGREE), new DialogInterface.OnClickListener() {
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

}

