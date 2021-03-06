package kr.go.sqsmo.peru.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.adapter.PushAdapter;
import kr.go.sqsmo.peru.util.BaseActivity;
import kr.go.sqsmo.peru.util.BeepManager;
public class PushDialog extends BaseActivity {
    TextView tv_breakaway_ok;
    String title, msg, flag, date;
    PushAdapter mAdapter;
    RecyclerView rv_dialog;

    /**
     * Intent Reiniciar
     *
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        set();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_break_away);
        this.getWindow().setBackgroundDrawable(mContext.getDrawable(R.drawable.bg_dialog));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        bind();
        set();
    }


    private void setRecyclerView() {//PUSHLIST
        rv_dialog = findViewById(R.id.rv_dialog);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_dialog.setLayoutManager(layoutManager);
        rv_dialog.setHasFixedSize(true);
        rv_dialog.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                scrollEvent(recyclerView);
            }
        });
    }


    @Override
    protected void bind() {
        tv_breakaway_ok = findViewById(R.id.tv_breakaway_ok);
        tv_breakaway_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.remove();
                if (mApplicationPERUO.getLifecycleList().size() < 1) {
                    startActivity(new Intent(mContext, IntroActivity.class));
                    finish();
                } else if ("App".equals(mApplicationPERUO.getLifecycleList().get(0))) {
                    startActivity(new Intent(mContext, IntroActivity.class));
                    finish();
                } else {
                    finish();
                }
            }
        });
        mAdapter = new PushAdapter(mContext);
    }

    private void scrollEvent(RecyclerView mRecyclerView) {

        if (((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition() >= (mRecyclerView.getAdapter().getItemCount() - 1)) {
            tv_breakaway_ok.setBackgroundResource(R.drawable.bg_button_red);
            tv_breakaway_ok.setEnabled(true);
        } else {
            if(mRecyclerView.getAdapter().getItemCount() == 1){
                tv_breakaway_ok.setBackgroundResource(R.drawable.bg_button_red);
                tv_breakaway_ok.setEnabled(true);
            }else {
                tv_breakaway_ok.setBackgroundResource(R.drawable.bg_button_gray);
                tv_breakaway_ok.setEnabled(false);
            }
        }
    }


    @Override
    protected void set() {
        if (getIntent() != null) {
            title = getIntent().getStringExtra("title");
            msg = getIntent().getStringExtra("messageBody");
            flag = getIntent().getStringExtra("flag");
            date = getIntent().getStringExtra("date");
            mAdapter.addItem(msg); //PUSHLIST
            mAdapter.addDate(date);
        }
        setBeep();
        setRecyclerView();
        mAdapter.notifyDataSetChanged(); //PUSHLIST
        rv_dialog.setAdapter(mAdapter); //PUSHLIST
    }


    /**
     * Configuraci??n y ejecuci??n del sonido de alarma
     */
    private void setBeep() {
        try {
            BeepManager beepManager = new BeepManager(this);
            beepManager.playBeepSoundAndVibrate();
        } catch (Exception e) {
            Log.e(TAG, "Beep Error - " + e);
        }
    }

    /**
     * Prevenci??n de tocar alrededor de la ventana de alarma
     *
     * @param ev MotionEvent
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect bounds = new Rect();
        getWindow().getDecorView().getHitRect(bounds);
        if (!bounds.contains((int) ev.getX(), (int) ev.getY())) {
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
    }
}
