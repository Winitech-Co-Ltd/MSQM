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
import kr.go.sqsmo.peru.activity.SelfDiagnosisActivity;
import kr.go.sqsmo.peru.data.servicedata.SelfCheckData;
import kr.go.sqsmo.peru.util.BaseActivity;

public class SelfCheckAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final String TAG = this.getClass().getSimpleName();
    private ArrayList<SelfCheckData> mSelfCheckDataArrayList = new ArrayList<>();
    Context mContext;
    BaseActivity mBaseActivity;
    String ISLPRSN_SN;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_HEADER = 2;

    public SelfCheckAdapter(String sn, Context context, BaseActivity activity) {
        mContext = context;
        mBaseActivity = activity;
        ISLPRSN_SN = sn;
    }

    /**
     * Lista de autodiagnóstico agrupada por fecha
     * @param viewType TYPE_ITEM = 1, TYPE_HEADER = 2
     * @return 1 = LIST, 2 = HEADER
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.self_check_item, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
            return new HeaderViewHolder(view);
        }
        return null;
    }

    /**
     * @param holder TYPE_ITEM, TYPE_HEADER
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder mItemViewHolder = (ItemViewHolder) holder;
            mItemViewHolder.onBind(mSelfCheckDataArrayList.get(position));
        } else if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder mHeaderViewHolder = (HeaderViewHolder) holder;
            mHeaderViewHolder.onBind(mSelfCheckDataArrayList.get(position));
        }
    }

    /**
     * @return true = TYPE_HEADER, false = TYPE_ITEM
     */
    @Override
    public int getItemViewType(int position) {
        if ("Y".equals(mSelfCheckDataArrayList.get(position).getHEADER_YN())) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mSelfCheckDataArrayList.size();
    }

    public void setItem(SelfCheckData data) {
        mSelfCheckDataArrayList.add(data);
    }

    /**
     * subView setting
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder {

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
                    mIntent.putExtra("ISLPRSN_SN", ISLPRSN_SN);
                    mContext.startActivity(mIntent);
                }
            });
            tv_check_list_date.setText(data.getSLFDGNSS_DT_F());
            if ("Y".equals(data.getCOLOR_YN())) {
                tv_check_list_date.setBackground(mContext.getDrawable(R.drawable.button_border_red));
                tv_check_list_date.setTextColor(mContext.getResources().getColor(R.color.colorWhite));
            } else {
                tv_check_list_date.setBackground(mContext.getDrawable(R.drawable.button_border));
                tv_check_list_date.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
            }

        }

    }

    /**
     * headerView setting
     */
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

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

