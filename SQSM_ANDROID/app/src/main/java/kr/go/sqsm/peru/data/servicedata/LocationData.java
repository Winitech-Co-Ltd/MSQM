package kr.go.sqsm.peru.data.servicedata;

import java.io.Serializable;

/**
 * Datos de la persona en autocuarentena
 */
public class LocationData implements Serializable {
    String ISLLC_XCNTS;
    String ISLLC_YDNTS;

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
}
