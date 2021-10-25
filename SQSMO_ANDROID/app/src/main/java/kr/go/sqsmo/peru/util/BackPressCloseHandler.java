package kr.go.sqsmo.peru.util;

import android.app.AlertDialog;

import kr.go.sqsmo.peru.R;

/**
 * Si hace clic el botón Atrás dos veces en el dispositivo en 2 segundos, la aplicación se cerrará
 */
public class BackPressCloseHandler {
    private final String TAG = this.getClass().getSimpleName();

    private BaseActivity mActivity;
    private long mBackKeyPressedTime = 0;

    public BackPressCloseHandler(BaseActivity activity) {
        mActivity = activity;
    }

    public void onBackPressed() {
        if(System.currentTimeMillis() > mBackKeyPressedTime + 2000) {
            mBackKeyPressedTime = System.currentTimeMillis();
            mActivity.makeToast(mActivity.getString(R.string.exit_message));
            return;
        }

        if(System.currentTimeMillis() <= mBackKeyPressedTime + 2000) {
            mActivity.closeToast();
            mActivity.finishAffinity();
        }
    }

    public void onBackPressedDialog(AlertDialog dialog) {
        if(System.currentTimeMillis() > mBackKeyPressedTime + 2000) {
            mBackKeyPressedTime = System.currentTimeMillis();
            mActivity.makeToast(mActivity.getString(R.string.exit_message));
            return;
        }

        if(System.currentTimeMillis() <= mBackKeyPressedTime + 2000) {
            dialog.dismiss();
            mActivity.closeToast();
            mActivity.finishAffinity();
        }
    }


}
