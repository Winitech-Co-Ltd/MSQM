package kr.go.sqsmo.peru;

import android.app.Application;
import android.app.Service;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import kr.go.sqsmo.peru.data.CountryComparator;
import kr.go.sqsmo.peru.data.CountryData;
import kr.go.sqsmo.peru.data.servicedata.UserData;

public class ApplicationPERUO extends Application {
    private final String TAG = this.getClass().getSimpleName();
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;

    /**
     * ID del administrador
     */
    private String loginId;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    /**
     * Datos del administrador que ha ingresado (login)
     */
    UserData mUserData = new UserData();

    public UserData getmUserData() {
        return mUserData;
    }

    public void setmUserData(UserData mUserData) {
        this.mUserData = mUserData;
    }

    public String header;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        mSharedPreferences = getSharedPreferences(getString(R.string.application), Service.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.commit();
        setCountry();
        addLifecycleList("App");
        setPushAlert();
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

    public String getPrivateKey() {
        return mSharedPreferences.getString("PRIVATE_KEY", "");
    }

    public String getPrivateVector() {
        return mSharedPreferences.getString("PRIVATE_VECTOR", "");
    }

    /**
     * LifeCycle Save List
     * Determina si la aplicación está operando o está completamente finalizado
     */
    private ArrayList<String> lifeCycleList = new ArrayList<>();

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

    /**
     * PUSH ID
     */
    private String fcm_token;

    public String getFcm_token() {
        return fcm_token;
    }

    /**
     * Registro de FCM y generación de tokens
     */
    public void setPushAlert() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId - ", task.getException());
                            return;
                        }
                        fcm_token = task.getResult().getToken();
                        Log.e(TAG, "FCM - " + fcm_token);
                    }
                });
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
     * Eliminar datos solo cuando se usa explícitamente la función de cierre de sesión
     */
    public void logout() {
        mEditor.remove("USER_DATA");
        mEditor.remove("PRIVATE_KEY");
        mEditor.remove("PRIVATE_VECTOR");
        setHeader("");
        mEditor.commit();
    }

    /**
     * Lista de nacionalidades
     */
    private ArrayList<CountryData> countryList = new ArrayList<CountryData>();

    public ArrayList<CountryData> getCountry() {
        return countryList;
    }

    public void setCountry() {
        ArrayList<CountryData> arrayList = new ArrayList<CountryData>();

        String[] isoCountries = Locale.getISOCountries();
        for (String country : isoCountries) {
            Locale locale = new Locale("es", country);
            String iso = locale.getISO3Country();
            String code = locale.getCountry();
            String name = locale.getDisplayCountry(Locale.US);

            if (!"".equals(iso) && !"".equals(code) && !"".equals(name)) {
                CountryData mData = new CountryData();
                mData.iso = iso;
                mData.code = code;
                mData.name = name;

                if (code.equals("PE")) {
                    arrayList.add(mData);
                } else {
                    countryList.add(mData);
                }
            }
        }

        Collections.sort(countryList, new CountryComparator());
        countryList.add(arrayList.get(0));

        setCountryMap();
    }

    /**
     * clave = código de nacionalidad
     * valor = nacionalidad
     */
    private HashMap<String, String> countryMap = new HashMap<>();

    public HashMap<String, String> getCountryMap() {
        return countryMap;
    }

    public void setCountryMap() {
        for (CountryData country : getCountry()) {
            countryMap.put(country.getCode(), country.getName());
        }
    }
}
