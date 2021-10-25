package kr.go.sqsm.peru.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.go.sqsm.peru.R;
import kr.go.sqsm.peru.adapter.SelfCheckAdapter;
import kr.go.sqsm.peru.data.RetrofitData;
import kr.go.sqsm.peru.data.servicedata.SelfCheckData;
import kr.go.sqsm.peru.util.BaseActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiagnosisListActivity extends BaseActivity {

    ArrayList<SelfCheckData> mSelfCheckDataArrayList = new ArrayList<>();
    SelfCheckData tempData = new SelfCheckData();
    RecyclerView rv_diagnosis_list;
    SelfCheckAdapter mSelfCheckAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    int page = 1, total;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_list);
        bind();
        set();
        retrofit.private_Service(mApplicationPERU.getHeader(), mRetrofitBody.PERU0007(mApplicationPERU.getUserNumber(), String.valueOf(page), mApplicationPERU.getUserSn())).enqueue(peru0007_callback);
    }

    @Override
    protected void bind() {
        rv_diagnosis_list = findViewById(R.id.rv_diagnosis_list);
        mSelfCheckAdapter = new SelfCheckAdapter(mContext, this);
        toolbar = findViewById(R.id.toolbar);
    }

    @Override
    protected void set() {
        mLayoutManager = new LinearLayoutManager(mContext);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        rv_diagnosis_list.setLayoutManager(mLayoutManager);
        rv_diagnosis_list.setAdapter(mSelfCheckAdapter);
        rv_diagnosis_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount() - 1;
                if (lastVisibleItemPosition == itemTotalCount) {
                    if (total >= page) {
                        retrofit.private_Service(mApplicationPERU.getHeader(), mRetrofitBody.PERU0007(mApplicationPERU.getUserNumber(), String.valueOf(page), mApplicationPERU.getUserSn())).enqueue(peru0007_callback);
                    }
                }
            }
        });
    }

    /**
     * Consulta de información registrada de la persona en autocuarentena  Callback
     */
    public Callback<RetrofitData> peru0007_callback = new Callback<RetrofitData>() {
        @Override
        public void onResponse(Call<RetrofitData> call, Response<RetrofitData> response) {
            stopProgress();
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    if (response.body().getResults() != null) {
                        try {
                            JSONArray mJsonArray = new JSONArray(response.body().getResults().toString());
                            for (int i = 0; i < mJsonArray.length(); i++) {
                                JSONObject mJsonObject = (JSONObject) mJsonArray.get(i);
                                SelfCheckData mSelfCheckData = new SelfCheckData();
                                mSelfCheckData.setDYSPNEA_AT(jsonNullCheck(mJsonObject, "DYSPNEA_AT"));
                                mSelfCheckData.setPYRXIA_AT(jsonNullCheck(mJsonObject, "PYRXIA_AT"));
                                mSelfCheckData.setCOUGH_AT(jsonNullCheck(mJsonObject, "COUGH_AT"));
                                mSelfCheckData.setSORE_THROAT_AT(jsonNullCheck(mJsonObject, "SORE_THROAT_AT"));
                                mSelfCheckData.setSLFDGNSS_DT(jsonNullCheck(mJsonObject, "SLFDGNSS_DT"));
                                mSelfCheckData.setSLFDGNSS_DT_F(jsonNullCheck(mJsonObject, "SLFDGNSS_DT_F"));
                                mSelfCheckData.setSLFDGNSS_D_F(jsonNullCheck(mJsonObject, "SLFDGNSS_D_F"));
                                if (mSelfCheckData.getCOUGH_AT().equals("Y") || mSelfCheckData.getPYRXIA_AT().equals("Y") || mSelfCheckData.getSORE_THROAT_AT().equals("Y") || mSelfCheckData.getDYSPNEA_AT().equals("Y")) {
                                    mSelfCheckData.setCOLOR_YN("Y");
                                } else {
                                    mSelfCheckData.setCOLOR_YN("N");
                                }
                                if (mSelfCheckDataArrayList.size() == 0) {
                                    SelfCheckData headerData = new SelfCheckData();
                                    headerData.setHEADER_YN("Y");
                                    headerData.setSLFDGNSS_D_F(mSelfCheckData.getSLFDGNSS_D_F());
                                    tempData = headerData;
                                    mSelfCheckAdapter.setItem(headerData);
                                } else {
                                    if (!tempData.getSLFDGNSS_D_F().equals(mSelfCheckData.getSLFDGNSS_D_F())) {
                                        SelfCheckData headerData = new SelfCheckData();
                                        headerData.setHEADER_YN("Y");
                                        headerData.setSLFDGNSS_D_F(mSelfCheckData.getSLFDGNSS_D_F());
                                        tempData = headerData;
                                        mSelfCheckAdapter.setItem(headerData);
                                    }
                                }

                                mSelfCheckDataArrayList.add(mSelfCheckData);
                                mSelfCheckAdapter.setItem(mSelfCheckData); // 어댑터 값 넣기
                                if (!"".equals(jsonNullCheck(mJsonObject, "TOT_PAGE"))) {
                                    total = Integer.parseInt(jsonNullCheck(mJsonObject, "TOT_PAGE"));
                                }
                            }
                            if (total >= page) {
                                page++;
                            }
                            if (mSelfCheckAdapter.getItemCount() == 0) {
                                makeToast(mContext.getString(R.string.diagnosislist_not_message));
                            }
                            mSelfCheckAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            Log.e(TAG, "peru0007_callback JSON ERROR - " + e);
                        }
                    } else {
                        Log.e(TAG, "Error Message - " + mContext.getString(R.string.network_error_message));
                        makeToast(mContext.getString(R.string.fail_data_message));
                    }
                } else {
                    makeToast(mContext.getString(R.string.not_data_message));
                }
            } else {
                makeToast(mContext.getString(R.string.network_error_message));
            }

        }

        @Override
        public void onFailure(Call<RetrofitData> call, Throwable t) {
            Log.e(TAG, "Retrofit Callback Fail - " + t);
            makeToast(mContext.getString(R.string.not_data_message));
            stopProgress();
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //Acción cuando se presiona la tecla Atrás del Toolbar (barra de herramientas)
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


}
