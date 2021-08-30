package kr.go.sqsmo.peru.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
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

import java.util.HashMap;

import kr.go.sqsmo.peru.ApplicationPERUO;
import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.network.AES256;
import kr.go.sqsmo.peru.network.RetrofitApi;
import kr.go.sqsmo.peru.network.RetrofitBody;
import kr.go.sqsmo.peru.network.RetrofitService;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;


/**
 * Definición de BASE ACTIVITY
 */
public abstract class BaseActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    public Context mContext;
    public ApplicationPERUO mApplicationPERUO;
    Toast mToast;
    private ProgressDialog mProgressDialog;
    public RetrofitApi mRetrofitApi;
    public RetrofitService retrofit;
    public BackPressCloseHandler mBackPressCloseHandler;
    public AlertDialog menuDialog;
    public RetrofitBody mRetrofitBody;
    public final int MY_REQUEST_CODE = 1000;
    private AppUpdateManager appUpdateManager;
    public Task<AppUpdateInfo> appUpdateInfoTask;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        mContext = this;
        mApplicationPERUO = (ApplicationPERUO) getApplicationContext();
        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        mRetrofitApi = new RetrofitApi();
        retrofit = mRetrofitApi.getService();
        mBackPressCloseHandler = new BackPressCloseHandler(this);
        mRetrofitBody = new RetrofitBody(this);
        AES256.setPublic(mApplicationPERUO.getPublicKey(), mApplicationPERUO.getPublicVector());
        AES256.setPrivate(mApplicationPERUO.getPrivateKey(), mApplicationPERUO.getPrivateVector());
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

    public String getNational(String national) {
        HashMap<String, String> country = mApplicationPERUO.getCountryMap();
        return country.get(national);
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
                    } catch (IntentSender.SendIntentException e) {
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
                mProgressDialog.setMessage(mContext.getString(R.string.plz_wait));
            } else {
                mProgressDialog.setMessage(message);
            }

            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
        }
    }

    public void stopProgress() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing() && !this.isFinishing()) {
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * JSON Null Cheque
     */
    public String jsonNullCheck(JSONObject json, String s) {
        String result = "";
        try {
            if (json.has(s)) {
                if (!json.isNull(s)) {
                    result = json.getString(s);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, "JSON ERROR - " + e);
        }
        return result;
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            super.setRequestedOrientation(requestedOrientation);
        }
    }

}
