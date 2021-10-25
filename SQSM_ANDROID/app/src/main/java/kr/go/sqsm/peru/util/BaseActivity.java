package kr.go.sqsm.peru.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import kr.go.sqsm.peru.ApplicationPERU;
import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.network.RetrofitApi;
import kr.go.sqsm.peru.network.RetrofitBody;
import kr.go.sqsm.peru.network.RetrofitService;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;


/**
 * Definición de BASE ACTIVITY
 */
public abstract class BaseActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    public Context mContext;
    public ApplicationPERU mApplicationPERU;
    public RetrofitApi mRetrofitApi;
    public RetrofitService retrofit;
    public BackPressCloseHandler mBackPressCloseHandler;
    public AlertDialog menuDialog;
    public RetrofitBody mRetrofitBody;
    public LocationDevice mLocationDevice;
    Toast mToast;
    private ProgressDialog mProgressDialog;
    private AppUpdateManager appUpdateManager;
    public Task<AppUpdateInfo> appUpdateInfoTask;
    public final int MY_REQUEST_CODE = 1000;


    /**
     * Android API 26~27 Screen Orientation Error
     */
    @Override
    protected void onStart() {
        super.onStart();
        try {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (IllegalStateException e) {
            Log.e(TAG, "setRequestedOrientation - " + e);
        }
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            super.setRequestedOrientation(requestedOrientation);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        mApplicationPERU = (ApplicationPERU) getApplicationContext();
        mRetrofitApi = new RetrofitApi();
        retrofit = mRetrofitApi.getService();
        mBackPressCloseHandler = new BackPressCloseHandler(this);
        mRetrofitBody = new RetrofitBody(this);
        mLocationDevice = new LocationDevice(mContext);
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        AES256.setPublic(mApplicationPERU.getPublicKey(), mApplicationPERU.getPublicVector());
        AES256.setPrivate(mApplicationPERU.getPrivateKey(), mApplicationPERU.getPrivateVector());
    }

    /**
     * Verificación de la versión de la aplicación y actualización automática
     */
    public void appVersionCheck() {
        appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, (Activity) mContext, MY_REQUEST_CODE);
                    } catch (Exception e) {
                        Log.e(TAG, "SendIntentException - " + e);
                    }
                }
            }
        });

    }

    /**
     * Progreso de actualización de la aplicación
     */
    public void appUpdateStatus() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, IMMEDIATE, (Activity) mContext, MY_REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        Log.e(TAG, "SendIntentException - " + e);
                    }
                }
            }
        });
    }

    /**
     * Obtener el número de teléfono del dispositivo
     */
    public String getPhoneNumber() {
        String phoneNum = "";
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "No phone number inquiry PERMISSION.");
            }
        }

        if (telephonyManager != null) {
            phoneNum = telephonyManager.getLine1Number();
        } else {
            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            phoneNum = telephonyManager.getLine1Number();
        }

        return phoneNum;
    }

    protected abstract void bind();

    protected abstract void set();

    public void exitDialog(String title, String contents) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);

        dialog.setTitle(title);
        dialog.setMessage(contents)
                .setCancelable(false)
                .setNegativeButton(mContext.getString(R.string.exit), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                });

        AlertDialog real = dialog.create();
        real.show();
    }

    public void logE(String msg) {
        Log.e(TAG, msg);
    }

    /**
     * Personalización de Toast
     */
    public void makeToast(String message) {
        mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        LinearLayout mLinearLayout = (LinearLayout) mToast.getView();
        TextView mTextView = (TextView) mLinearLayout.getChildAt(0);

        mToast.setGravity(Gravity.CENTER, 0, 0);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextSize(20);

        mToast.show();
    }

    public void closeToast() {
        mToast.cancel();
    }


    /**
     * @param message
     */
    public void startProgress(String message) {
        if (mContext != null && !((Activity) mContext).isFinishing()) {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }

            mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            if ("".equals(message)) {
                mProgressDialog.setMessage(mContext.getString(R.string.please_wait));
            } else {
                mProgressDialog.setMessage(message);
            }

            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void stopProgress() {
        try {
            if (mProgressDialog != null && !this.isFinishing()) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "StopProgress - " + e);
        } catch (Exception e) {
            Log.e(TAG, "StopProgress - " + e);
        }
    }

    /**
     * JSON Null Check
     */
    public String jsonNullCheck(JSONObject json, String s) throws JSONException {
        String result = "";
        if (json.has(s)) {
            if (!json.isNull(s)) {
                result = json.getString(s);
            }
        }
        return result;
    }

    /**
     * Códigos específicos del idioma (minúsculas) Español (es), Inglés (en)
     */
    public void changeConfiguration(String code) {
        Locale mLocale = new Locale(code);
        Configuration config = new Configuration();
        config.locale = mLocale;
        getResources().updateConfiguration(config, null);

    }
}
