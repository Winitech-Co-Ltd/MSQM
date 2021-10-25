package com.peru.ncov2019.ml.cmm.util;

import java.io.UnsupportedEncodingException;
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
 * ##해당 클래스 설명 작성 필요##
 * @author Winitech (Gildong, Park)
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

}
