package kr.go.sqsmo.peru.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.go.sqsmo.peru.R;

public class PushAdapter extends RecyclerView.Adapter<PushAdapter.ItemViewHolder> {
    private ArrayList<String> mArrayList = new ArrayList<>();
    private ArrayList<String> dateList = new ArrayList<>();
    private Context mContext;

    public PushAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public PushAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item_push, parent, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.item_tv_breakaway.setText(mArrayList.get(position));
        holder.item_tv_breakaway_date.setText(dateList.get(position));
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public void addItem(String data) {
        mArrayList.add(data);
    }
    public void addDate(String date){
        dateList.add(date);
    }


    public void remove() {
        mArrayList.clear();
        dateList.clear();
    }


    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView item_tv_breakaway,item_tv_breakaway_date;

        ItemViewHolder(View convertView) {
            super(convertView);
            item_tv_breakaway = convertView.findViewById(R.id.item_tv_breakaway);
            item_tv_breakaway_date = convertView.findViewById(R.id.item_tv_breakaway_date);
        }
    }
}

