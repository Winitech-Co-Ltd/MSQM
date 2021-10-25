package kr.go.sqsm.peru.adapter;

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
import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.activity.SelfDiagnosisActivity;
import kr.go.sqsm.peru.data.servicedata.SelfCheckData;
import kr.go.sqsm.peru.util.BaseActivity;

public class SelfCheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private ArrayList<SelfCheckData> mUserList = new ArrayList<>();
    Context mContext;
    BaseActivity mBaseActivity;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    public SelfCheckAdapter(Context context,BaseActivity activity) {
        mContext = context;
        mBaseActivity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.self_check_item, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder mItemViewHolder = (ItemViewHolder) holder;
            mItemViewHolder.onBind(mUserList.get(position));
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder mHeaderViewHolder = (HeaderViewHolder) holder;
            mHeaderViewHolder.onBind(mUserList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public void setItem(SelfCheckData data) {
        mUserList.add(data);
    }

    @Override
    public int getItemViewType(int position) {
        if("Y".equals(mUserList.get(position).getHEADER_YN())){
            return TYPE_FOOTER;
        }else{
            return TYPE_ITEM;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ll_check_list;
        TextView tv_check_list_date;

        ItemViewHolder(View convertView) {
            super(convertView);
            ll_check_list = convertView.findViewById(R.id.ll_check_list);
            tv_check_list_date = convertView.findViewById(R.id.tv_check_list_date);
        }

        void onBind(final SelfCheckData data) {
            ll_check_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(mContext, SelfDiagnosisActivity.class);
                    mIntent.putExtra("DATE", data.getSLFDGNSS_DT());
                    mContext.startActivity(mIntent);
                }
            });
            tv_check_list_date.setText(data.getSLFDGNSS_DT_F());
            if("Y".equals(data.getCOLOR_YN())) {
                tv_check_list_date.setBackground(mContext.getDrawable(R.drawable.button_border_red));
                tv_check_list_date.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            }else{
                tv_check_list_date.setBackground(mContext.getDrawable(R.drawable.button_border));
                tv_check_list_date.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            }

        }

    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        TextView tv_header_date;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_header_date = itemView.findViewById(R.id.tv_header_date);
        }

        void onBind(final SelfCheckData data) {
            tv_header_date.setText(data.getSLFDGNSS_D_F());
        }
    }

}

