package com.peru.ncov2019.ml.cmm.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;

import com.peru.ncov2019.ml.sqs.dao.ScmDao;

import lombok.extern.apachecommons.CommonsLog;

/**
 * ##유틸 클래스##
 *
 */
@CommonsLog
public class MlUtil {

	public static ModelAndView transNullMav(ModelAndView mav)throws UnsupportedEncodingException{
		transNullItem(mav.getModel());
		return mav;
	}
	
	@SuppressWarnings({ "unchecked"})
	private static Object transNullItem(Object target) throws UnsupportedEncodingException{
    	if (target instanceof List){
    		if(target != null && ((List<Object>)target).size() > 0){
				
				for(int i = 0 ; i < ((List<Object>)target).size() ; i++){
					Object item = transNullItem(((List<Object>)target).get(i));
					((List<Object>)target).set(i, item);
				}
			}
    		
    	}else if(target instanceof Map){
    		Iterator<String> it = ((Map<String,Object>)target).keySet().iterator();
    		while(it.hasNext()) {
    			String key = it.next();
    			((Map<String,Object>)target).put(key, transNullItem(((Map<String,Object>)target).get(key)));
    		}
    	}else{
    		target = (target == null) ? "" : target+"";
    	}
    	
    	return target;
    }
	
	
	/**
	 * Función que verifica el valor que se debe pasar cuando el cliente llama al servicio
	 * @param map			Parámetro que se envia al servidor cuando el cliente llama al servicio
	 * @param keyAndMsg		Tipo de matríz 2-D (segunda dimensión) [key] [Msg] de valor requerido y un mensaje a entregar si no se encuentra el valor requerido
	 * @return msg	Mensaje para enviar al cliente si no hay un valor requerido
	 */
	public static String emptyValidation(Map<String,Object> map , String[][] keyAndMsg){
		String msg = "";
		for(String[] item : keyAndMsg){
			
			if(map.get(item[0]) == null || "".equals(map.get(item[0]))){
				msg = item[1]+" no se encontró."; 
				break;
			}
		}
		
		return msg;
		
	}
	
	
	/* SN */
	public static Map<String,Object> mappingSn(Map<String,Object> map, ScmDao ScmDao) throws Exception{
	
		String islprsnSn = (map.get("ISLPRSN_SN") == null) ? "" : map.get("ISLPRSN_SN")+"";
		
		if(!"".equals(islprsnSn)){
			islprsnSn = ScmDao.selectOne("PERU_USR.selectUserHashSn", map);
			if(islprsnSn != null && !"".equals(islprsnSn)){
				map.put("ISLPRSN_SN", islprsnSn);
			}
		}
		
		String mngrSn = (map.get("MNGR_SN") == null) ? "" : map.get("MNGR_SN")+"";
		if(!"".equals(mngrSn)){
			mngrSn = ScmDao.selectOne("PERU_MNGR.selectUserHashSn", map);
			if(mngrSn != null && !"".equals(mngrSn)){
				map.put("MNGR_SN", mngrSn);
			}
		}
		
		String ecshgMngrSn = (map.get("ECSHG_MNGR_SN") == null) ? "" : map.get("ECSHG_MNGR_SN")+"";
		if(!"".equals(ecshgMngrSn)){
			ecshgMngrSn = ScmDao.selectOne("PERU_MNGR.selectUserHashSn", map);
			if(ecshgMngrSn != null && !"".equals(ecshgMngrSn)){
				map.put("ECSHG_MNGR_SN", ecshgMngrSn);
			}
		}
		
		return map;
	}

	
	
	
	
	/**
	 * Capacidad para obtener un valor TIMESTAMP de 17 dígitos del sistema para utilizar valores propios en aplicaciones
	 * @param currentTime
	 * @return
	 */
	public static String getTimeStamp(long currentTime) {
        String sPattern = "yyyyMMddHHmmss";
        return getTimeStamp(currentTime, sPattern);
	}
	
	/**
	 * Capacidad para obtener un valor TIMESTAMP de 17 dígitos del sistema para utilizar valores propios en aplicaciones
	 * @param currentTime
	 * @param pattern ex) yyyyMMddHHmmss
	 * @return
	 */
    public static String getTimeStamp(long currentTime, String pattern) {

        String rtnStr = null;

        try {
            SimpleDateFormat sdfCurrent = new SimpleDateFormat(pattern, Locale.KOREA);
            Timestamp ts = new Timestamp(currentTime);

            rtnStr = sdfCurrent.format(ts.getTime());
        } catch (RuntimeException e) {
            log.info("getTimeStamp ERROR");
        }

        return rtnStr;
    }
    
	/**
	 * Función para eliminar espacios en ambos lados de un valor de cadena
	 */
	@SuppressWarnings("unchecked")
	public static void mapTrim(Map<String,Object> map){
		Iterator<String> keys = map.keySet().iterator();
		while(keys.hasNext()){
			String key = keys.next();
			Object item = map.get(key);
			if(item instanceof String){
				if(item != null){
					if("".equals(((String) item).trim())){
						map.put(key, null);
					}else{
						map.put(key, ((String) item).trim());
					}
				}
			}else if(item instanceof Map<?,?>){
				mapTrim((Map<String, Object>) item);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	public static String getSHA256(String str){

		String SHA = ""; 

		try{

			MessageDigest sh = MessageDigest.getInstance("SHA-256"); 

			sh.update(str.getBytes()); 

			byte byteData[] = sh.digest();

			StringBuffer sb = new StringBuffer(); 

			for(int i = 0 ; i < byteData.length ; i++){

				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));

			}

			SHA = sb.toString();

			

		}catch(NoSuchAlgorithmException e){

			e.printStackTrace(); 

			SHA = null; 

		}

		return SHA;

	}
	
	
	
	
	
	
	
	
	
}
