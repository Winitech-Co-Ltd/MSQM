package kr.go.sqsm.peru.network;


import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import kr.go.sqsm.peru.data.servicedata.SelfCheckData;
import kr.go.sqsm.peru.data.servicedata.UserData;
import kr.go.sqsm.peru.util.AES256;
import kr.go.sqsm.peru.util.BaseActivity;
import okhttp3.RequestBody;

public class RetrofitBody {
    public final String TAG = this.getClass().getSimpleName();
    BaseActivity mBaseActivity;

    public RetrofitBody(BaseActivity baseActivity) {
        mBaseActivity = baseActivity;
    }

    public RetrofitBody() {

    }

    /**
     * Definición de RequestBody
     * @param mJsonObject JSON
     */
    private RequestBody resultRequestBody(JSONObject mJsonObject) {
        mBaseActivity.startProgress("");
        return RequestBody.create(okhttp3.MediaType.parse("application/json"),
                (mJsonObject.toString()));
    }

    private RequestBody resultPollingRequestBody(JSONObject mJsonObject) {
        return RequestBody.create(okhttp3.MediaType.parse("application/json"),
                (mJsonObject.toString()));
    }

    /**
     * Autorización de la persona en autocuarentena
     * @param mInputUserData Datos de la persona en autocuarentena
     */
    public RequestBody PERU0012(UserData mInputUserData) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0012");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_NM", mInputUserData.getISLPRSN_NM());
            parms.put("BRTHDY", mInputUserData.getBRTHDY());
            parms.put("SEXDSTN_CODE", mInputUserData.getSEXDSTN_CODE());
            parms.put("NLTY_CODE", mInputUserData.getNLTY_CODE());
            parms.put("TELNO", mInputUserData.getTELNO());
            parms.put("EMGNC_TELNO", mInputUserData.getEMGNC_TELNO());
            parms.put("PSPRNBR", mInputUserData.getPSPRNBR());
            parms.put("TRMNL_KND_CODE", "00401");
            parms.put("TRMNL_NM", Build.MODEL);
            parms.put("PUSHID", mBaseActivity.mApplicationPERU.getFcm_token());
            parms.put("USE_LANG", "PE");
            parms.put("CRTFC_NO", mBaseActivity.mApplicationPERU.getManagerId());
            parms.put("ISLLC_DPRTMNT_CODE",mInputUserData.getISLLC_DPRTMNT_CODE());
            parms.put("ISLLC_PRVNCA_CODE",mInputUserData.getISLLC_PRVNCA_CODE());
            parms.put("ISLLC_DSTRT_CODE",mInputUserData.getISLLC_DSTRT_CODE());
            parms.put("ISLLC_ETC_ADRES", mInputUserData.getISLLC_ETC_ADRES());
            parms.put("ISLLC_XCNTS", mInputUserData.getISLLC_XCNTS());
            parms.put("ISLLC_YDNTS", mInputUserData.getISLLC_YDNTS());
            parms.put("ECSHG_MNGR_SN", mInputUserData.getECSHG_MNGR_SN());
            parms.put("INHT_ID",mInputUserData.getINHT_ID());
            result.put("PARM", AES256.publicEncrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PEUR_Service PERU0012 ERROR - " + e);
        }
        return resultRequestBody(result);
    }


    /**
     * Región
     */
    public RequestBody PERUC0002() {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUC0002");
            JSONObject parms = new JSONObject();
            result.put("PARM", AES256.publicEncrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUC0002 JSON ERROR - " + e);
        }
        return resultRequestBody(result);
    }

    /**
     * Provincia
     * @param DPRTMNT_CODE Código de Región
     */
    public RequestBody PERUC0003(String DPRTMNT_CODE) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUC0003");
            JSONObject parms = new JSONObject();
            parms.put("DPRTMNT_CODE", DPRTMNT_CODE);
            result.put("PARM", AES256.publicEncrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUC0003 JSON ERROR - " + e);
        }
        return resultRequestBody(result);
    }

    /**
     * Distrito
     * @param DPRTMNT_CODE Código de Región
     * @param PRVNCA_CODE Código de Provincia
     */
    public RequestBody PERUC0004(String DPRTMNT_CODE, String PRVNCA_CODE) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUC0004");
            JSONObject parms = new JSONObject();
            parms.put("DPRTMNT_CODE", DPRTMNT_CODE);
            parms.put("PRVNCA_CODE", PRVNCA_CODE);
            result.put("PARM", AES256.publicEncrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUC0004 JSON ERROR - " + e);
        }
        return resultRequestBody(result);
    }

    /**
     * Consulta de información registrada de la persona en autocuarentena
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param TRMNL_SN Número de serie del terminal de usuario
     */
    public RequestBody PERU0004(String ISLPRSN_SN, String TRMNL_SN) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0004");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("TRMNL_SN", TRMNL_SN);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0004 ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Modificar información de la persona en autocuarentena
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param TRMNL_SN Número de serie del terminal de usuario
     * @param mData Datos de la persona en autocuarentena
     */
    public RequestBody PERU0005(String ISLPRSN_SN, String TRMNL_SN, UserData mData) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0005");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("TRMNL_SN", TRMNL_SN);
            parms.put("TELNO", mData.getTELNO());
            parms.put("ISLLC_XCNTS", mData.getISLLC_XCNTS());
            parms.put("ISLLC_YDNTS", mData.getISLLC_YDNTS());
            parms.put("ISLLC_ETC_ADRES", mData.getISLLC_ETC_ADRES());
            parms.put("PSPRNBR", mData.getPSPRNBR());
            parms.put("EMGNC_TELNO", mData.getEMGNC_TELNO());
            parms.put("TRMNL_KND_CODE", "00401");
            parms.put("ISLLC_DPRTMNT_CODE", mData.getISLLC_DPRTMNT_CODE());
            parms.put("ISLLC_PRVNCA_CODE", mData.getISLLC_PRVNCA_CODE());
            parms.put("ISLLC_DSTRT_CODE", mData.getISLLC_DSTRT_CODE());
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0005 ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Enviar información de ubicación
     * @param TRMNL_SN Número de serie del terminal de usuario
     * @param GPS_ONOFF GPS ON/OFF
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param ISLPRSN_XCNTS Ubicación actual de la persona en cuarentena X
     * @param ISLPRSN_YDNTS Ubicación actual de la persona en cuarentena Y
     */
    public RequestBody PERU0006(String ISLPRSN_SN, String TRMNL_SN, String ISLPRSN_XCNTS, String ISLPRSN_YDNTS, String GPS_ONOFF) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0006");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("TRMNL_SN", TRMNL_SN);
            parms.put("ISLPRSN_XCNTS", ISLPRSN_XCNTS);
            parms.put("ISLPRSN_YDNTS", ISLPRSN_YDNTS);
            parms.put("GPS_ONOFF", GPS_ONOFF);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0006 ERROR - " + e);
        }
        return resultPollingRequestBody(result);
    }

    /**
     * Consulta de información de registro de la persona en autocuarentena
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param TRMNL_SN Número de serie del terminal de usuario
     * @param page page
     */
    public RequestBody PERU0007(String ISLPRSN_SN, String page, String TRMNL_SN) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0007");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("PAGE", page);
            parms.put("TRMNL_SN", TRMNL_SN);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0007 ERROR - " + e);
        }

        return resultRequestBody(result);
    }


    /**
     * Servicio para llamar fecha y hora del último autodiagnóstico
     * @param TRMNL_SN Número de serie del terminal de usuario
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     */
    public RequestBody PERU0008(String ISLPRSN_SN, String TRMNL_SN) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0008");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("TRMNL_SN", TRMNL_SN);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0008 ERROR - " + e);
        }

        return resultRequestBody(result);
    }


    /**
     * Registrar información de autodiagnóstico
     * @param TRMNL_SN Número de serie del terminal de usuario
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param data Datos de autodiagnóstico de la persona en autocuarentena
     */
    public RequestBody PERU0009(String ISLPRSN_SN, String TRMNL_SN, SelfCheckData data) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0009");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("PYRXIA_AT", data.getPYRXIA_AT());
            parms.put("COUGH_AT", data.getCOUGH_AT());
            parms.put("SORE_THROAT_AT", data.getSORE_THROAT_AT());
            parms.put("DYSPNEA_AT", data.getDYSPNEA_AT());
            parms.put("RM", data.getRM());
            parms.put("BDHEAT", data.getBDHEAT());
            parms.put("TRMNL_SN", TRMNL_SN);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0009 ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Consulta de información de autodiagnóstico
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param TRMNL_SN Número de serie del terminal de usuario
     * @param SLFDGNSS_DT Fecha y hora de autodiagnóstico
     */
    public RequestBody PERU0010(String ISLPRSN_SN, String TRMNL_SN, String SLFDGNSS_DT) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0010");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("SLFDGNSS_DT", SLFDGNSS_DT);
            parms.put("TRMNL_SN", TRMNL_SN);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0010 ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Consulta de información del encargado oficial
     * @param ECSHG_MNGR_SN Número de serie del encargado oficial
     * @param MNGR_LOGIN_ID ID de encargado oficial
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     */
    public RequestBody PERU0011(String ECSHG_MNGR_SN, String MNGR_LOGIN_ID, String ISLPRSN_SN) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0011");
            JSONObject parms = new JSONObject();
            if (!"".equals(ECSHG_MNGR_SN))
                parms.put("ECSHG_MNGR_SN", ECSHG_MNGR_SN);
            if (!"".equals(MNGR_LOGIN_ID)) {
                parms.put("MNGR_LOGIN_ID", MNGR_LOGIN_ID);
                result.put("PARM", AES256.publicEncrypt(parms.toString()));
            }
            if (!"".equals(ISLPRSN_SN)) {
                parms.put("ISLPRSN_SN", ISLPRSN_SN);
                result.put("PARM", AES256.encrypt(parms.toString()));
            }
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0011 ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Consulta de información del encargado oficial
     * @param ECSHG_MNGR_SN Número de serie del encargado oficial
     * @param MNGR_LOGIN_ID ID de encargado oficial
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     */
    public RequestBody PERU0013(String ECSHG_MNGR_SN, String MNGR_LOGIN_ID, String ISLPRSN_SN) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0013");
            JSONObject parms = new JSONObject();
            if (!"".equals(ECSHG_MNGR_SN))
                parms.put("ECSHG_MNGR_SN", ECSHG_MNGR_SN);
            if (!"".equals(MNGR_LOGIN_ID)) {
                parms.put("MNGR_LOGIN_ID", MNGR_LOGIN_ID);
                result.put("PARM", AES256.publicEncrypt(parms.toString()));
            }
            if (!"".equals(ISLPRSN_SN)) {
                parms.put("ISLPRSN_SN", ISLPRSN_SN);
                result.put("PARM", AES256.encrypt(parms.toString()));
            }
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0013 ERROR - " + e);
        }

        return resultRequestBody(result);
    }


    /**
     * Consulta de Centros de Salud
     * @param CURRENT_X Ubicación actual de la persona en cuarentena X
     * @param CURRENT_Y Ubicación actual de la persona en cuarentena Y
     */
    public RequestBody PERUPU0001(String CURRENT_X, String CURRENT_Y) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUPU0001");
            JSONObject parms = new JSONObject();
            parms.put("CURRENT_X", CURRENT_X);
            parms.put("CURRENT_Y", CURRENT_Y);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUPU0001 ERROR - " + e);
        }

        return resultRequestBody(result);
    }


    /**
     * push 응답
     * Created by rlwhd0716 on 2020-05-29
     */
    public RequestBody PERUPUSH0001(String ISLPRSN_SN, String TRMNL_SN, String MESSAGEID) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUPUSH0001");
            JSONObject parms = new JSONObject();
            parms.put("MESSAGEID", MESSAGEID);
            parms.put("TRMNL_SN", TRMNL_SN);
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUPUSH0001 ERROR - " + e);
        }
        return resultPollingRequestBody(result);
    }


    /**
     * 공개키 발급
     * Created by jongsuuu on 2020-06-22
     */
    public RequestBody PERUK0001() {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUK0001");
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUK0001 ERROR - " + e);
        }
        return resultRequestBody(result);
    }


    /**
     * 기존서비스 사용자들 비밀키 발급
     * Created by jongsuuu on 2020-06-25
     */
    public RequestBody PERUTM0001(String taget, String target2) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUTM0001");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", taget);
            parms.put("TRMNL_SN", target2);
            result.put("PARM", AES256.publicEncrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUTM0001 ERROR - " + e);
        }
        return resultRequestBody(result);
    }



}
