package kr.go.sqsm.peru.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import kr.go.sqsm.peru.activity.IntroActivity;

public class AppAutoRun extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent mIntent1 = new Intent(context, IntroActivity.class);
            mIntent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent1);
        }
    }

}
