package kr.go.sqsmo.peru.network;


import android.util.Log;

import org.json.JSONObject;

import kr.go.sqsmo.peru.data.servicedata.InsulatorData;
import kr.go.sqsmo.peru.data.servicedata.LocationData;
import kr.go.sqsmo.peru.util.BaseActivity;
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
     */
    private RequestBody resultRequestBody(JSONObject mJsonObject) {
        mBaseActivity.startProgress("");
        return RequestBody.create(okhttp3.MediaType.parse("application/json"),
                (mJsonObject.toString()));
    }

    /**
     * Pantalla de inicio de sesión del usuario
     * @param LOGIN_ID Iniciar sesión ID
     * @param PASSWORD contraseña
     * @param push_id PUSHID
     */
    public RequestBody PERUO0001(String LOGIN_ID, String PASSWORD, String push_id) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUO0001");
            JSONObject parms = new JSONObject();
            parms.put("LOGIN_ID", LOGIN_ID);
            parms.put("PASSWORD", PASSWORD);
            parms.put("PUSHID", push_id);
            parms.put("TRMNL_KND_CODE", "00401");
            result.put("PARM", AES256.publicEncrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUO0001 JSON ERROR - " + e);
        }
        return resultRequestBody(result);
    }

    /**
     * Consulta de lista de personas en autocuarentena
     * @param MNGR_SN Número de serie del encargado oficial
     * @param page page
     */
    public RequestBody PERUO0002(String MNGR_SN, String page) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUO0002");
            JSONObject parms = new JSONObject();
            parms.put("MNGR_SN", MNGR_SN);
            parms.put("PAGE", page);
            parms.put("PSITN_DPRTMNT_CODE", "");
            parms.put("PSITN_PRVNCA_CODE", "");
            parms.put("PSITN_DSTRT_CODE", "");
            parms.put("ISLPRSN_NM", "");
            parms.put("SEXDSTN_CODE", "");
            parms.put("NLTY_CODE", "");
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUO0002 JSON ERROR - " + e);
        }
        return resultRequestBody(result);
    }


    /**
     * Buscar lista de autodiagnóstico (lista)
     * @param MNGR_SN Número de serie del encargado oficial
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param ISLPRSN_STTUS_CODE Código de estado de la persona en cuarentena
     */
    public RequestBody PERUO0005(String MNGR_SN, String ISLPRSN_SN, String ISLPRSN_STTUS_CODE) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUO0005");
            JSONObject parms = new JSONObject();
            parms.put("MNGR_SN", MNGR_SN);
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("ISLPRSN_STTUS_CODE", ISLPRSN_STTUS_CODE);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUO0005 JSON ERROR - " + e);
        }
        return resultRequestBody(result);
    }

    /**
     * Servicio de inicio de sesión
     * @param MNGR_SN Número de serie del encargado oficial
     * @param LOGIN_ID ID del encargado oficial
     * @param LOGIN_AT Inicio y cierre de sesión del administrador(Y / N)
     */
    public RequestBody PERUO0006(String MNGR_SN, String LOGIN_ID, String LOGIN_AT) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUO0006");
            JSONObject parms = new JSONObject();
            parms.put("MNGR_SN", MNGR_SN);
            parms.put("LOGIN_ID", LOGIN_ID);
            parms.put("LOGIN_AT", LOGIN_AT);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUO0006 JSON ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Cambiar Encargado Oficial
     * @param MNGR_SN Número de serie del encargado oficial
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     */
    public RequestBody PERUO0007(String MNGR_SN, String ISLPRSN_SN) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUO0007");
            JSONObject parms = new JSONObject();
            parms.put("MNGR_SN", MNGR_SN);
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUO0007 JSON ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Modificar información registrada de la persona en autocuarentena
     * @param mLocationData Datos de ubicación de la persona en autocuarentena
     * @param mInsulatorData Datos de la persona en autocuarentena
     */
    public RequestBody PERU0005(LocationData mLocationData, InsulatorData mInsulatorData) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0005");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", mInsulatorData.getISLPRSN_SN());
            parms.put("TRMNL_SN", mInsulatorData.getTRMNL_SN());
            parms.put("TELNO", mInsulatorData.getTELNO());
            parms.put("ISLLC_XCNTS", mLocationData.getISLLC_XCNTS());
            parms.put("ISLLC_YDNTS", mLocationData.getISLLC_YDNTS());
            parms.put("ISLLC_ETC_ADRES", mLocationData.getISLLC_ETC_ADRES());
            parms.put("ISLLC_DPRTMNT_CODE", mInsulatorData.getISLLC_DPRTMNT_CODE());
            parms.put("ISLLC_PRVNCA_CODE", mInsulatorData.getISLLC_PRVNCA_CODE());
            parms.put("ISLLC_DSTRT_CODE", mInsulatorData.getISLLC_DSTRT_CODE());
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0005 JSON ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Buscar lista de autodiagnóstico (lista)
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param page page
     */
    public RequestBody PERU0007(String ISLPRSN_SN, String page) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0007");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("PAGE", page);
            parms.put("TRMNL_SN", "");
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0007 JSON ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Búsqueda de información de autodiagnóstico
     * @param ISLPRSN_SN Número de serie de la persona en autocuarentena
     * @param SLFDGNSS_DT Fecha y hora de autodiagnóstico
     */
    public RequestBody PERU0010(String ISLPRSN_SN, String SLFDGNSS_DT) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0010");
            JSONObject parms = new JSONObject();
            parms.put("ISLPRSN_SN", ISLPRSN_SN);
            parms.put("SLFDGNSS_DT", SLFDGNSS_DT);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0010 JSON ERROR - " + e);
        }

        return resultRequestBody(result);
    }

    /**
     * Búsqueda de Encargado Oficial
     * @param MNGR_LOGIN_ID ID del encargado oficial
     */
    public RequestBody PERU0011(String MNGR_LOGIN_ID) {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERU0011");
            JSONObject parms = new JSONObject();
            if (!"".equals(MNGR_LOGIN_ID))
                parms.put("MNGR_LOGIN_ID", MNGR_LOGIN_ID);
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERU0011 JSON ERROR - " + e);
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
            result.put("PARM", AES256.encrypt(parms.toString()));
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
            result.put("PARM", AES256.encrypt(parms.toString()));
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
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUC0004 JSON ERROR - " + e);
        }
        return resultRequestBody(result);
    }

    /**
     * Código de estado de la persona en cuarentena
     */
    public RequestBody PERUC0005() {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUC0005");
            JSONObject parms = new JSONObject();
            result.put("PARM", AES256.encrypt(parms.toString()));
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUC0005 JSON ERROR - " + e);
        }
        return resultRequestBody(result);
    }
    public RequestBody PERUK0001() {
        JSONObject result = new JSONObject();
        try {
            result.put("IFID", "PERUK0001");
        } catch (Exception e) {
            Log.e(TAG, "PERU_Service PERUK0001 ERROR - " + e);
        }
        return resultRequestBody(result);
    }
}
