package kr.go.sqsm.peru.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.go.sqsm.peru.ApplicationPERU;
import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.network.RetrofitApi;
import kr.go.sqsm.peru.network.RetrofitBody;
import kr.go.sqsm.peru.network.RetrofitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkManager extends Worker {
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;
    ApplicationPERU mApplicationPERU = (ApplicationPERU) getApplicationContext();
    public RetrofitApi mRetrofitApi;
    public RetrofitService retrofit;
    public RetrofitBody mRetrofitBody;

    public WorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        mContext = context;
        mRetrofitApi = new RetrofitApi();
        retrofit = mRetrofitApi.getService();
        mRetrofitBody = new RetrofitBody();
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            retrofit.private_Service(mApplicationPERU.getHeader(),
                    mRetrofitBody.PERU0006(mApplicationPERU.getUserNumber(),
                            mApplicationPERU.getUserSn(),
                            String.valueOf(mApplicationPERU.getmLocationDevice().get().getLongitude()),
                            String.valueOf(mApplicationPERU.getmLocationDevice().get().getLatitude()),
                            mApplicationPERU.getmLocationDevice().checkGPS() ? mApplicationPERU.getmLocationDevice().isFromMockProvider() ? "FAKE" : "ON" : "OFF"
                    )).enqueue(peru0006_callback);
        } catch (Exception e) {
            Log.e(TAG, "Exception - " + e);
        }

        return Result.success();
    }

    /**
     * Enviar información de ubicación  Callback
     */
    public Callback<RetrofitData> peru0006_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (!response.body().getRes_cd().equals("100")) {
                            mApplicationPERU.makeToast(mContext, mContext.getString(R.string.network_error_message));
                        } else {
                            try {
                                JSONArray mArray = new JSONArray(response.body().getResults());
                                JSONObject mJsonObject = (JSONObject) mArray.get(mArray.length() - 1);

                            } catch (JSONException e) {
                                Log.e(TAG, "peru0006_callback  JSON ERROR - " + e);
                            }
                        }
                    } else {
                        mApplicationPERU.makeToast(mContext, mContext.getString(R.string.network_error_message));
                    }
                } else {
                    mApplicationPERU.makeToast(mContext, mContext.getString(R.string.network_error_message));
                }
            } else {
                mApplicationPERU.makeToast(mContext, mContext.getString(R.string.not_data_message));
            }
        }

        @Override
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            mApplicationPERU.makeToast(mContext, mContext.getString(R.string.not_data_message));
        }
    };
}
