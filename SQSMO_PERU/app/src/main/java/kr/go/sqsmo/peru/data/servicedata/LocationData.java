package kr.go.sqsmo.peru.data.servicedata;

import java.io.Serializable;

/**
 * Datos de ubicación de la persona en autocuarentena
 */
public class LocationData implements Serializable {
    String ISLLC_XCNTS;
    String ISLLC_YDNTS;
    String ISLLC_ETC_ADRES;

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

    public String getISLLC_ETC_ADRES() {
        return ISLLC_ETC_ADRES;
    }

    public void setISLLC_ETC_ADRES(String ISLLC_ETC_ADRES) {
        this.ISLLC_ETC_ADRES = ISLLC_ETC_ADRES;
    }
}
