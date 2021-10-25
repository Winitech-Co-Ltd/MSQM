package kr.go.sqsm.peru.data.servicedata;

/**
 * Datos de autodiagnóstico de la persona en autocuarentena
 */
public class SelfCheckData {
    String SLFDGNSS_DT;
    String SLFDGNSS_DT_F;
    String PYRXIA_AT;
    String COUGH_AT;
    String SORE_THROAT_AT;
    String DYSPNEA_AT;
    String SLFDGNSS_D_F;
    String HEADER_YN;
    String COLOR_YN;
    String RM;
    String BDHEAT;

    public SelfCheckData() {
        this.SLFDGNSS_DT = "";
        this.SLFDGNSS_DT_F = "";
        this.PYRXIA_AT = "";
        this.COUGH_AT = "";
        this.SORE_THROAT_AT = "";
        this.DYSPNEA_AT = "";
        this.RM = "";
        this.BDHEAT = "";
    }

    public String getRM() {
        return RM;
    }

    public void setRM(String RM) {
        this.RM = RM;
    }

    public String getBDHEAT() {
        return BDHEAT;
    }

    public void setBDHEAT(String BDHEAT) {
        this.BDHEAT = BDHEAT;
    }

    public String getSLFDGNSS_D_F() {
        return SLFDGNSS_D_F;
    }

    public void setSLFDGNSS_D_F(String SLFDGNSS_D_F) {
        this.SLFDGNSS_D_F = SLFDGNSS_D_F;
    }

    public String getHEADER_YN() {
        return HEADER_YN;
    }

    public void setHEADER_YN(String HEADER_YN) {
        this.HEADER_YN = HEADER_YN;
    }

    public String getCOLOR_YN() {
        return COLOR_YN;
    }

    public void setCOLOR_YN(String COLOR_YN) {
        this.COLOR_YN = COLOR_YN;
    }

    public String getSLFDGNSS_DT() {
        return SLFDGNSS_DT;
    }

    public void setSLFDGNSS_DT(String SLFDGNSS_DT) {
        this.SLFDGNSS_DT = SLFDGNSS_DT;
    }

    public String getSLFDGNSS_DT_F() {
        return SLFDGNSS_DT_F;
    }

    public void setSLFDGNSS_DT_F(String SLFDGNSS_DT_F) {
        this.SLFDGNSS_DT_F = SLFDGNSS_DT_F;
    }

    public String getPYRXIA_AT() {
        return PYRXIA_AT;
    }

    public void setPYRXIA_AT(String PYRXIA_AT) {
        this.PYRXIA_AT = PYRXIA_AT;
    }

    public String getCOUGH_AT() {
        return COUGH_AT;
    }

    public void setCOUGH_AT(String COUGH_AT) {
        this.COUGH_AT = COUGH_AT;
    }

    public String getSORE_THROAT_AT() {
        return SORE_THROAT_AT;
    }

    public void setSORE_THROAT_AT(String SORE_THROAT_AT) {
        this.SORE_THROAT_AT = SORE_THROAT_AT;
    }

    public String getDYSPNEA_AT() {
        return DYSPNEA_AT;
    }

    public void setDYSPNEA_AT(String DYSPNEA_AT) {
        this.DYSPNEA_AT = DYSPNEA_AT;
    }
}
