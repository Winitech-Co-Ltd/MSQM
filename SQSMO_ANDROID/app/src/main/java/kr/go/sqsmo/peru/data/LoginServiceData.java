package kr.go.sqsmo.peru.data;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import kr.go.sqsmo.peru.network.AES256;

public class LoginServiceData {
    @SerializedName("RES_DATA")
    String results;
    @SerializedName("RES_CD")
    String res_cd;
    @SerializedName("RES_MSG")
    String res_msg;

    public String getResults() {
        String s = "";
        try {
            s = AES256.publicDecrypt(results);
        } catch (Exception e) {
            Log.e("RetrofitData", "DECRYPT ERROR - " + e);
        }
        return s;


    }

    public String getRes_cd() {
        return res_cd;
    }

    public String getRes_msg() {
        return res_msg;
    }
}
