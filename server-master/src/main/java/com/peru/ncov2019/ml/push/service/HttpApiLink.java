package com.peru.ncov2019.ml.push.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Clase para comunicación HTTP
 *
 */
@CommonsLog
public class HttpApiLink {

	public Map<String,Object> callApi(Properties config ,Map<String,Object> vo, boolean send){
		Map<String,Object> data = new HashMap<String,Object>();
		
		data = vo;
		// push url
		StringBuffer url = new StringBuffer();
		url.append(config.get("push.url"));
		
		// key
		String pushKey = config.getProperty("push.key");
		// senderId
		String senderId = config.getProperty("push.senderId");
		
		Map<String,Object> responseData = postHttp(url.toString(),pushKey, config.getProperty("push.send.encoding"), config.getProperty("push.get.encoding"),data);
		Map<String,Object> resultMap = new HashMap<String,Object>();

		responseData.putAll(resultMap);
		
		return responseData;
	}
	
	/**
	 * Parte común de comunicación HTTP
	 * @param httpUrl
	 * @param pushKey
	 * @param sendEncoding
	 * @param getEncoding
	 * @param vo
	 * @return
	 */
	public Map<String,Object> postHttp(String httpUrl, String pushKey, String sendEncoding, String getEncoding ,Map<String,Object> vo) {
		ObjectMapper om = new ObjectMapper();
		// Convertir Map or List Object a cadena JSON
		String jsonStr = null;
		Map<String,Object> m = null;
		
        OutputStream os = null;
        BufferedReader bufrd = null;
        String result = "";
        
        URL urlCon = null;
        HttpURLConnection httpCon = null;
        
        try {
            urlCon = new URL(httpUrl);
            httpCon = (HttpURLConnection)urlCon.openConnection();

            // Set some headers to inform server about the type of the content
            httpCon.setRequestProperty("Authorization", "key="+pushKey);
            //httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-type", "application/json");
            httpCon.setRequestProperty("Connection", "close");
            httpCon.setConnectTimeout(5 * 1000);
    		
            // Opción para pasar datos POST a OutputStream.
            httpCon.setDoOutput(true);
            // Opción para recibir una respuesta del servidor en InputStream.
            httpCon.setDoInput(true);

            os = httpCon.getOutputStream();
            jsonStr = om.writeValueAsString(vo);
            
            //String jsonEnc = URLEncoder.encode(jsonStr,sendEncoding);
            log.info("Parámetros de comunicación HTTP : "+jsonStr);
            os.write(jsonStr.getBytes(sendEncoding));
            os.flush();
        	
            bufrd = new BufferedReader(new InputStreamReader(httpCon.getInputStream(),getEncoding));
            //if(httpCon.getResponseCode() == 200){
                if(bufrd != null){
                	StringBuffer sb = new StringBuffer();
                	String thisLine = null;
                    while ((thisLine = bufrd.readLine())!=null) {
                        sb.append(thisLine);
                    }
                    result = sb.toString();
                    log.info(result);
                    try{
                    	m = om.readValue(result, new TypeReference<Map<String,Object>>(){});
                    }catch(JsonMappingException e){
                    	log.error("El formato de datos no es correcto.");
                    	log.error(result);
                    	log.error("=======================================================");
                    }
                }
            //}
        }
        catch (IOException e) {
        	log.error("Error durante la comunicación API - IOException - 1 :"+e.getLocalizedMessage());
        	m = new HashMap<String,Object>();
        	m.put("ERROR_MSG","Error durante la comunicación API "+e.getLocalizedMessage());
        	
        }
        catch (Exception e) {
        	log.error("Error durante la comunicación API - InputStream - 2 " + e.getLocalizedMessage());
        }
        finally {
        	if(os != null){
        		try {
					os.close();
				} catch (IOException e) {
					log.error("Error durante la comunicación API - OutputStream IOException - 3 :"+e.getLocalizedMessage());
				}
        	}
        	if(httpCon != null){
        		httpCon.disconnect();
        	}
            
        }

        return m;
	}

	/**
	 * Función para filtrar la información necesaria entre los valores de la lista utilizada del código API
	 * @param array
	 * @param orignalList
	 * @return
	 * @throws Exception
	 */
	public List<Map<String,Object>> selectCodeType(String[] array, List<Map<String,Object>> orignalList) throws Exception{
		
		List<Map<String,Object>> codelist = null;
		String tempStr = "";
		
		codelist = orignalList;
		if(codelist != null && codelist.size() > 0){
			
			
			for(Iterator<Map<String,Object>> it = codelist.iterator() ; it.hasNext() ;){
				
				Map<String,Object> item = it.next();
				
				tempStr = (item.get("CD_TY_VALUE") == null) ? "" : (String)item.get("CD_TY_VALUE");
				
				if(!Arrays.toString(array).contains(tempStr)){
					it.remove();
				}
			}
			
		}
		
		return codelist;
		
	}
	
}

