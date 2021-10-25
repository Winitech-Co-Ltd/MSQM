package kr.go.sqsm.peru.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.work.WorkManager;

import kr.go.sqsm.peru.ApplicationPERU;
import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.activity.MainActivity;
import kr.go.sqsm.peru.util.AES256;


/**
 * Service Definición
 */
public class ForegroundService extends Service {
    private final String TAG = this.getClass().getSimpleName();
    ApplicationPERU mApplicationPERU;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationPERU = (ApplicationPERU) getApplication();
        startService();
        WorkManager.getInstance(this).cancelAllWork();
        AES256.setPublic(mApplicationPERU.getPublicKey(), mApplicationPERU.getPublicVector());
        AES256.setPrivate(mApplicationPERU.getPrivateKey(), mApplicationPERU.getPrivateVector());
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("peruPolling", null, mApplicationPERU.getPollingRequest());
    }

    /**
     * ForegroundService Start
     */
    private void startService() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);

        remoteViews.setTextViewText(R.id.tv_noti, "Self-quarantine safety protection app is in execution.");


        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "CSCS_SERVICE_CHANNEL";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Self-quarantine safety Service Channel", NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setContent(remoteViews)
                .setContentIntent(pendingIntent);

        startForeground(1, builder.build());
    }
}
