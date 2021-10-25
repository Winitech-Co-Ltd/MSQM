package kr.go.sqsm.peru.data.servicedata;

import java.io.Serializable;

/**
 * Datos de usuario (persona en autocuarentena)
 */
public class UserData implements Serializable {
    String ISLPRSN_SN;
    String ISLPRSN_NM;
    String SEXDSTN;
    String SEXDSTN_CODE;
    String NLTY_CODE;
    String ISLLC_XCNTS;
    String ISLLC_YDNTS;
    String ECSHG_MNGR_SN;
    String TELNO;
    String EMGNC_TELNO;
    String TRMNL_SN;
    String DISTANCE;
    String PSPRNBR;
    String BRTHDY;
    String ISL_SE_CODE;
    String ISL_SE_CODE_NM;
    String MNGR_LOGIN_ID;
    String ISLLC_DPRTMNT_CODE;
    String ISLLC_DPRTMNT_CODE_NM;
    String ISLLC_PRVNCA_CODE;
    String ISLLC_PRVNCA_CODE_NM;
    String ISLLC_DSTRT_CODE;
    String ISLLC_DSTRT_CODE_NM;
    String ISLLC_ETC_ADRES;
    String INHT_ID;
    String ADDR;
    String BRTHDY_F;

    public UserData() {
        ISLPRSN_SN = "";
        ISLPRSN_NM = "";
        SEXDSTN = "";
        SEXDSTN_CODE = "";
        NLTY_CODE = "";
        ISLLC_XCNTS = "";
        ISLLC_YDNTS = "";
        ECSHG_MNGR_SN = "";
        TELNO = "";
        EMGNC_TELNO = "";
        TRMNL_SN = "";
        DISTANCE = "";
        PSPRNBR = "";
        BRTHDY = "";
        BRTHDY_F = "";
        ISL_SE_CODE = "";
        ISL_SE_CODE_NM = "";
        MNGR_LOGIN_ID = "";
        ISLLC_DPRTMNT_CODE = "";
        ISLLC_DPRTMNT_CODE_NM = "";
        ISLLC_PRVNCA_CODE = "";
        ISLLC_PRVNCA_CODE_NM = "";
        ISLLC_DSTRT_CODE = "";
        ISLLC_DSTRT_CODE_NM = "";
        ISLLC_ETC_ADRES = "";
        INHT_ID = "";
        ADDR = "";
    }

    public String getINHT_ID() {
        return INHT_ID;
    }

    public void setINHT_ID(String INHT_ID) {
        this.INHT_ID = INHT_ID;
    }

    public String getISLPRSN_SN() {
        return ISLPRSN_SN;
    }

    public void setISLPRSN_SN(String ISLPRSN_SN) {
        this.ISLPRSN_SN = ISLPRSN_SN;
    }

    public String getISLPRSN_NM() {
        return ISLPRSN_NM;
    }

    public void setISLPRSN_NM(String ISLPRSN_NM) {
        this.ISLPRSN_NM = ISLPRSN_NM;
    }

    public String getSEXDSTN() {
        return SEXDSTN;
    }

    public void setSEXDSTN(String SEXDSTN) {
        this.SEXDSTN = SEXDSTN;
    }

    public String getSEXDSTN_CODE() {
        return SEXDSTN_CODE;
    }

    public void setSEXDSTN_CODE(String SEXDSTN_CODE) {
        this.SEXDSTN_CODE = SEXDSTN_CODE;
    }

    public String getNLTY_CODE() {
        return NLTY_CODE;
    }

    public void setNLTY_CODE(String NLTY_CODE) {
        this.NLTY_CODE = NLTY_CODE;
    }

    public String getISLLC_XCNTS() {
        return ISLLC_XCNTS;
    }

    public void setISLLC_XCNTS(String ISLLC_XCNTS) {
        this.ISLLC_XCNTS = ISLLC_XCNTS;
    }

    public String getISLLC_YDNTS() {
        return ISLLC_YDNTS;
    }

    public void setISLLC_YDNTS(String ISLLC_YDNTS) {
        this.ISLLC_YDNTS = ISLLC_YDNTS;
    }

    public String getECSHG_MNGR_SN() {
        return ECSHG_MNGR_SN;
    }

    public void setECSHG_MNGR_SN(String ECSHG_MNGR_SN) {
        this.ECSHG_MNGR_SN = ECSHG_MNGR_SN;
    }

    public String getTELNO() {
        return TELNO;
    }

    public void setTELNO(String TELNO) {
        this.TELNO = TELNO;
    }

    public String getEMGNC_TELNO() {
        return EMGNC_TELNO;
    }

    public void setEMGNC_TELNO(String EMGNC_TELNO) {
        this.EMGNC_TELNO = EMGNC_TELNO;
    }

    public String getTRMNL_SN() {
        return TRMNL_SN;
    }

    public void setTRMNL_SN(String TRMNL_SN) {
        this.TRMNL_SN = TRMNL_SN;
    }

    public String getDISTANCE() {
        return DISTANCE;
    }

    public void setDISTANCE(String DISTANCE) {
        this.DISTANCE = DISTANCE;
    }

    public String getPSPRNBR() {
        return PSPRNBR;
    }

    public void setPSPRNBR(String PSPRNBR) {
        this.PSPRNBR = PSPRNBR;
    }

    public String getBRTHDY() {
        return BRTHDY;
    }

    public void setBRTHDY(String BRTHDY) {
        this.BRTHDY = BRTHDY;
    }

    public String getBRTHDY_F() {
        return BRTHDY_F;
    }

    public void setBRTHDY_F(String BRTHDY_F) {
        this.BRTHDY_F = BRTHDY_F;
    }

    public String getISL_SE_CODE() {
        return ISL_SE_CODE;
    }

    public void setISL_SE_CODE(String ISL_SE_CODE) {
        this.ISL_SE_CODE = ISL_SE_CODE;
    }

    public String getISL_SE_CODE_NM() {
        return ISL_SE_CODE_NM;
    }

    public void setISL_SE_CODE_NM(String ISL_SE_CODE_NM) {
        this.ISL_SE_CODE_NM = ISL_SE_CODE_NM;
    }

    public String getMNGR_LOGIN_ID() {
        return MNGR_LOGIN_ID;
    }

    public void setMNGR_LOGIN_ID(String MNGR_LOGIN_ID) {
        this.MNGR_LOGIN_ID = MNGR_LOGIN_ID;
    }
        public String getISLLC_DPRTMNT_CODE() {
        return ISLLC_DPRTMNT_CODE;
    }

    public void setISLLC_DPRTMNT_CODE(String ISLLC_DPRTMNT_CODE) {
        this.ISLLC_DPRTMNT_CODE = ISLLC_DPRTMNT_CODE;
    }

    public String getISLLC_DPRTMNT_CODE_NM() {
        return ISLLC_DPRTMNT_CODE_NM;
    }

    public void setISLLC_DPRTMNT_CODE_NM(String ISLLC_DPRTMNT_CODE_NM) {
        this.ISLLC_DPRTMNT_CODE_NM = ISLLC_DPRTMNT_CODE_NM;
    }

    public String getISLLC_PRVNCA_CODE() {
        return ISLLC_PRVNCA_CODE;
    }

    public void setISLLC_PRVNCA_CODE(String ISLLC_PRVNCA_CODE) {
        this.ISLLC_PRVNCA_CODE = ISLLC_PRVNCA_CODE;
    }

    public String getISLLC_PRVNCA_CODE_NM() {
        return ISLLC_PRVNCA_CODE_NM;
    }

    public void setISLLC_PRVNCA_CODE_NM(String ISLLC_PRVNCA_CODE_NM) {
        this.ISLLC_PRVNCA_CODE_NM = ISLLC_PRVNCA_CODE_NM;
    }

    public String getISLLC_DSTRT_CODE() {
        return ISLLC_DSTRT_CODE;
    }

    public void setISLLC_DSTRT_CODE(String ISLLC_DSTRT_CODE) {
        this.ISLLC_DSTRT_CODE = ISLLC_DSTRT_CODE;
    }

    public String getISLLC_DSTRT_CODE_NM() {
        return ISLLC_DSTRT_CODE_NM;
    }

    public void setISLLC_DSTRT_CODE_NM(String ISLLC_DSTRT_CODE_NM) {
        this.ISLLC_DSTRT_CODE_NM = ISLLC_DSTRT_CODE_NM;
    }

    public String getISLLC_ETC_ADRES() {
        return ISLLC_ETC_ADRES;
    }

    public void setISLLC_ETC_ADRES(String ISLLC_ETC_ADRES) {
        this.ISLLC_ETC_ADRES = ISLLC_ETC_ADRES;
    }

    public String getADDR() {
        return ADDR;
    }

    public void setADDR(String ADDR) {
        this.ADDR = ADDR;
    }
}
