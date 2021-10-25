package kr.go.sqsm.peru.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.util.BaseActivity;

public class SelectLanguageActivity extends BaseActivity {

    TextView tv_select_espanol, tv_select_english;
    String lang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language);
        bind();
        set();
    }

    @Override
    protected void bind() {
        tv_select_espanol = findViewById(R.id.tv_select_espanol);
        tv_select_english = findViewById(R.id.tv_select_english);
    }

    @Override
    protected void set() {
        tv_select_espanol.setOnClickListener(mOnClickListener);
        tv_select_english.setOnClickListener(mOnClickListener);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_select_espanol:
                    lang = "es";
                    break;
                case R.id.tv_select_english:
                    lang = "en";
                    break;
                default:
                    lang = "es";
                    break;
            }
            changeConfiguration(lang);

            AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
            dialog.setCancelable(false);
            dialog.setTitle(mContext.getString(R.string.select_language));
            dialog.setMessage(mContext.getString(R.string.select_lang));
            dialog.setPositiveButton(mContext.getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mApplicationPERU.setLanguage(lang);
                    setResult(RESULT_OK);
                    finish();
                }
            });
            dialog.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            AlertDialog real = dialog.create();
            real.show();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }
}
