package kr.go.sqsmo.peru.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.go.sqsmo.peru.R;
import kr.go.sqsmo.peru.activity.UserDetailActivity;
import kr.go.sqsmo.peru.data.servicedata.InsulatorData;
import kr.go.sqsmo.peru.data.servicedata.UserData;
import kr.go.sqsmo.peru.util.BaseActivity;

public class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    Context mContext;
    BaseActivity mBaseActivity;
    UserData mUserData;
    private ArrayList<InsulatorData> mUserList = new ArrayList<>();

    public UserAdapter(Context context, BaseActivity baseActivity, UserData userData) {
        mContext = context;
        mBaseActivity = baseActivity;
        mUserData = userData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder mItemViewHolder = (ItemViewHolder) holder;
        mItemViewHolder.onBind(mUserList.get(position));

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void setItem(InsulatorData data) {
        mUserList.add(data);
    }
    public void removeAll(){
        mUserList = new ArrayList<>();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_item_summary;
        TextView tv_item_connect_status, tv_item_self_check_yn, tv_item_user_name;

        ItemViewHolder(View convertView) {
            super(convertView);
            ll_item_summary = convertView.findViewById(R.id.ll_item_summary);
            tv_item_user_name = convertView.findViewById(R.id.tv_item_user_name);
            tv_item_self_check_yn = convertView.findViewById(R.id.tv_item_self_check_yn);
            tv_item_connect_status = convertView.findViewById(R.id.tv_item_connect_status);

        }

        void backgroundSetting(int color, int draw1, int draw2) {
            tv_item_user_name.setTextColor(mContext.getResources().getColor(color));
            tv_item_self_check_yn.setTextColor(mContext.getResources().getColor(color));
            tv_item_connect_status.setTextColor(mContext.getResources().getColor(color));

            ll_item_summary.setBackground(mContext.getDrawable(draw1));
        }

        void onBind(final InsulatorData data) {
            ll_item_summary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(mContext, UserDetailActivity.class);
                    mIntent.putExtra("DATA", data);
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(mIntent);
                }
            });

            if ("00301".equals(data.getISLPRSN_STTUS_CODE())) {
                if ("N".equals(data.getSLFDGNSS_GUBN_AT())) {
                    switch (data.getISLPRSN_NTW_STTUS_CODE()) {
                        case "4":
                            backgroundSetting(R.color.colorBlack, R.drawable.bg_item_white_summary, 0);
                            break;
                        default:
                            backgroundSetting(R.color.colorWhite, R.drawable.bg_item_red_summary, 0);
                            break;
                    }
                } else {
                    backgroundSetting(R.color.colorWhite, R.drawable.bg_item_red_summary, 0);
                }
            } else {
                backgroundSetting(R.color.colorWhite, R.drawable.bg_item_darkgray_summary, 0);
            }

            tv_item_user_name.setText(data.getISLPRSN_NM());
            tv_item_self_check_yn.setText(data.getSLFDGNSS_AM_CODE_NM() + " / " + data.getSLFDGNSS_PM_CODE_NM());
            tv_item_connect_status.setText(data.getISLPRSN_NTW_STTUS_CODE_NM());
        }

    }

}

