package kr.go.sqsmo.peru.data;


import com.google.gson.annotations.SerializedName;

public class KeyData {

    @SerializedName("RES")
    String results;
    @SerializedName("RES_CD")
    String res_cd;
    @SerializedName("RES_MSG")
    String res_msg;

    public String getResults() {
        return results;
    }

    public String getRes_cd() {
        return res_cd;
    }

    public String getRes_msg() {
        return res_msg;
    }
}
