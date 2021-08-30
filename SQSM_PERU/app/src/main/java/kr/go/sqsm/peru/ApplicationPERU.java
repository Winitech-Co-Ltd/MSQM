package kr.go.sqsm.peru;

import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.PeriodicWorkRequest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import kr.go.sqsm.peru.data.CountryComparator;
import kr.go.sqsm.peru.data.servicedata.UserData;
import kr.go.sqsm.peru.util.LocationDevice;
import kr.go.sqsm.peru.util.WorkManager;


/**
 * Application Definición
 */
public class ApplicationPERU extends Application {
    private final String TAG = this.getClass().getSimpleName();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    Toast mToast;

    private ArrayList<HashMap<String, String>> countryList = new ArrayList<>(), countryList2 = new ArrayList<>();

    private ArrayList<String> lifeCycleList = new ArrayList<>();

    Handler mHandler = new Handler(Looper.getMainLooper());
    PeriodicWorkRequest pollingRequest = new PeriodicWorkRequest.Builder(WorkManager.class,15, TimeUnit.MINUTES)
            .build();
    LocationDevice mLocationDevice;


    public PeriodicWorkRequest getPollingRequest() {
        return pollingRequest;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public LocationDevice getmLocationDevice() {
        return mLocationDevice;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(getString(R.string.application), Service.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mLocationDevice = new LocationDevice(this);
        mEditor.commit();

        setPushAlert();
    }

    public String getHeader() {
        return mSharedPreferences.getString("HEADER", "");
    }

    public void setHeader(String header) {
        mEditor.putString("HEADER", header);
        mEditor.commit();
    }


    public void setPublicKey(String key, String vector) {
        mEditor.putString("PUBLIC_KEY", key);
        mEditor.putString("PUBLIC_VECTOR", vector);
        mEditor.commit();
    }

    public String getPublicKey() {
        return mSharedPreferences.getString("PUBLIC_KEY", "");
    }

    public String getPublicVector() {
        return mSharedPreferences.getString("PUBLIC_VECTOR", "");
    }


    public void setPrivateKey(String key, String vector) {
        mEditor.putString("PRIVATE_KEY", key);
        mEditor.putString("PRIVATE_VECTOR", vector);
        mEditor.commit();
    }

    public String getPrivateVector() {
        return mSharedPreferences.getString("PRIVATE_VECTOR", "");
    }


    public String getPrivateKey() {
        return mSharedPreferences.getString("PRIVATE_KEY", "");
    }


    /**
     * LifeCycle List save
     * Determina si la aplicación está operando o está completamente finalizado
     */
    public ArrayList<String> getLifecycleList() {
        return lifeCycleList;
    }

    public void addLifecycleList(String mLifecycle) {
        lifeCycleList.add(mLifecycle);
    }

    public void removeLifecycleList(String mLifecycle) {
        lifeCycleList.remove(mLifecycle);
    }

    public void removeAllLifecycleList() {
        lifeCycleList.clear();
    }

    public void setLanguage(String s) {
        mEditor.putString("LOCAL_LANG", s);
        mEditor.commit();
    }

    public String getLanguage() {
        return mSharedPreferences.getString("LOCAL_LANG", "");
    }


    public void setManager(String managerNumber, String managerId) {
        mEditor.putString("MANAGER_NUMBER", managerNumber);
        mEditor.putString("MANAGER_ID", managerId);
        mEditor.commit();
    }

    public String getManagerNumber() {
        return mSharedPreferences.getString("MANAGER_NUMBER", "");
    }

    public String getManagerId() {
        return mSharedPreferences.getString("MANAGER_ID", "");
    }

    public void setUserNumber(String number, String TRMNL_SN) {
        mEditor.putString("USER_NUMBER", number);
        mEditor.putString("TRMNL_SN", TRMNL_SN);
        mEditor.commit();
    }

    public String getUserNumber() {
        return mSharedPreferences.getString("USER_NUMBER", "");
    }

    public String getUserSn() {
        return mSharedPreferences.getString("TRMNL_SN", "");
    }

    UserData mUserData;

    public void setUserData(UserData data) {
        mUserData = data;
    }

    public UserData getUserData() {
        return mUserData;
    }

    /**
     * Push Id
     */
    public String fcm_token;

    /**
     * Registro de FCM y generación de tokens
     */
    private void setPushAlert() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId - ", task.getException());
                            return;
                        }

                        fcm_token = task.getResult().getToken();
                        Log.e(TAG, "FCM Token: " + fcm_token);
                    }
                });
    }

    public String getFcm_token() {
        return fcm_token;
    }

    /**
     * Información de la versión de la aplicación
     */
    public String getVersion() {
        String mVersion = "0.0.0";
        try {
            PackageManager mPackageManager = this.getPackageManager();
            mVersion = mPackageManager.getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "PackageManger error - " + e);
        }
        return mVersion;
    }

    /**
     * Dos se excluyen debido a errores al cargar.
     */
    public void setCountry() {
        String[] isoCountries = Locale.getISOCountries();
        for (String country : isoCountries) {
            Locale locale = new Locale("es", country);
            String iso = locale.getISO3Country();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry(Locale.US);

            if (!"".equals(iso) && !"".equals(code) && !"".equals(name)) {
                HashMap<String, String> temp = new HashMap<>(), temp2 = new HashMap<>();
                temp.put(name, code);
                temp2.put(code, name);
                countryList2.add(temp2);
                if (code.equals("SP") || code.equals("ZG") || code.equals("PE")) {
                    Log.e(TAG, "SP AND ZG AND PE");
                } else {
                    countryList.add(temp);
                }
            }
        }
        Collections.sort(countryList, new CountryComparator());
    }

    public ArrayList<HashMap<String, String>> getCountryList() {
        return countryList;
    }

    public ArrayList<HashMap<String, String>> getCountryList2() {
        return countryList2;
    }

    /**
     * Personalización de Toast
     */
    public void makeToast(Context mContext, String message) {
        mToast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        LinearLayout mLinearLayout = (LinearLayout) mToast.getView();
        TextView mTextView = (TextView) mLinearLayout.getChildAt(0);

        mToast.setGravity(Gravity.CENTER, 0, 0);
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextSize(20);

        mToast.show();
    }

}
