package kr.go.sqsmo.peru.service;

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

import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.activity.PushDialog;


/**
 * FCM Service
 */
public class MyFireBaseMessagingService extends FirebaseMessagingService {
    private final String TAG = getClass().getSimpleName();
    Intent intent;

    @Override
    public void onNewToken(@NonNull String token) {
        Log.e(TAG, "Refreshed token: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.e(TAG, "Alert Data: " + remoteMessage.getData());
        String msgBody = remoteMessage.getData().get("message");
        String msgTitle = remoteMessage.getData().get("title");
        String flag = remoteMessage.getData().get("flag");
        sendNotification(msgTitle, msgBody, flag);
    }

    private void sendNotification(String title, String messageBody, String flag) {
        if (title == null) {
            title = getResources().getString(R.string.application);
        }

        intent = new Intent(getApplicationContext(), PushDialog.class);
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
            String channelName = getApplicationContext().getString(R.string.push_alert_service);
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        startActivity(intent);
    }
}
