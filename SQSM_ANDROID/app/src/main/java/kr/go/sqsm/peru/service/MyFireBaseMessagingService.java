package kr.go.sqsm.peru.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;

import kr.go.sqsm.peru.ApplicationPERU;
import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.activity.BreakAwayDialog;
import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.network.RetrofitApi;
import kr.go.sqsm.peru.network.RetrofitBody;
import kr.go.sqsm.peru.network.RetrofitService;
import kr.go.sqsm.peru.util.AES256;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * FCM
 */
public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private final String TAG = getClass().getSimpleName();
    Intent intent;
    Context mContext;
    ApplicationPERU mApplicationPERU;
    public RetrofitService retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationPERU = (ApplicationPERU) getApplication();
        mContext = this;
        retrofit = new RetrofitApi().getService();
        mRetrofitBody = new RetrofitBody();
    }

    public RetrofitBody mRetrofitBody;
    @Override
    public void onNewToken(@NonNull String token) {
        Log.e(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "Alert Data: " + remoteMessage.getData());
        if(remoteMessage.getData().size() != 0) {
            String msgBody = remoteMessage.getData().get("message");
            String msgTitle = remoteMessage.getData().get("title");
            String flag = remoteMessage.getData().get("flag");
            sendNotification(msgTitle, msgBody, flag);
        }else{
            AES256.setPublic(mApplicationPERU.getPublicKey(), mApplicationPERU.getPublicVector());
            AES256.setPrivate(mApplicationPERU.getPrivateKey(), mApplicationPERU.getPrivateVector());
            retrofit.private_Service(mApplicationPERU.getHeader(),mRetrofitBody.PERUPUSH0001(mApplicationPERU.getUserNumber(), mApplicationPERU.getUserSn(), remoteMessage.getMessageId())).enqueue(PERUPUSH0001_callback);
        }
    }


    /**
     * PUSH Result Callback
     */
    public Callback<RetrofitData> PERUPUSH0001_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getRes_cd() != null) {
                        if (response.body().getRes_cd().equals("100")) {
                            Log.e(TAG, "PUSH Success");
                        } else {
                            mApplicationPERU.makeToast(mContext, mContext.getString(R.string.network_error_message));
                        }
                    } else {
                        mApplicationPERU.makeToast(mContext, mContext.getString(R.string.network_error_message));
                    }
                } else {
                    mApplicationPERU.makeToast(mContext, mContext.getString(R.string.network_error_message));
                }
            }
        }

        @Override
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            mApplicationPERU.makeToast(mContext, mContext.getString(R.string.not_data_message));
        }
    };

    /**
     * Solo la columna 'Datos' debe enviarse desde el servidor para mostrar la ventana de notificación con 'Actividad'.
     */
    private void sendNotification(String title, String messageBody, String flag) {
        if (title == null) {
            title = getResources().getString(R.string.application);
        }

        intent = new Intent(this, BreakAwayDialog.class);
        intent.putExtra("title", title);
        intent.putExtra("messageBody", messageBody);
        intent.putExtra("flag", flag);
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String gettime = sdf.format(date);
        intent.putExtra("date",gettime);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        String channelId = "Push Alert";

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "Push Alert Service";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        startActivity(intent);
    }
}
