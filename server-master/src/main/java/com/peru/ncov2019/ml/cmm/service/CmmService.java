package com.peru.ncov2019.ml.cmm.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.apachecommons.CommonsLog;
/**
 * Clase de servicio en común
 *
 */
@Service("CmmService")
public class CmmService {

	@Resource(name="configReload") 
	private configReload configReload;
	/**
	 * Recargar el archivo Config 
	 */
	public void configReload(){
		try {
			configReload.serviceCall(null, null);
			Map<String,Object> parm = new HashMap<String,Object>();
			parm.put("FILENAME", "resCdMsg.properties");
			configReload.serviceCall(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}


/**
 * Clase para guardar el archivo Config
 *
 */
@Service("configSet")
@CommonsLog
class configSetService implements TargetService {
	@Resource(name="config") 
	private Properties config;
	@Resource 
	private ApplicationContext context;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		String filename = ((vo.get("FILENAME") == null ) ? "config.properties" : (String) vo.get("FILENAME"));
		
		// Comprobar ruta de Config 
		org.springframework.core.io.Resource pathSet = context.getResource("/WEB-INF/classes/config/"+filename);
		log.info(pathSet.exists());
		File f = pathSet.getFile();
		log.info(f.exists());
		BufferedReader in = null;
		BufferedWriter out = null;
		if(f.exists()){
    		
		    try {
		        ////////////////////////////////////////////////////////////////
		        out = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
		        String s = (String) vo.get("CONFIG_DATA");
	
		        out.write(s); out.newLine();
	
		        out.flush();
		        out.close();
		        ////////////////////////////////////////////////////////////////
		        in = new BufferedReader(new FileReader(f));
		        config.load(in);
		        Properties props = PropertiesLoaderUtils.loadProperties(pathSet);
		        //config.setConfig(props);
		        
		        
		      } catch (RuntimeException e) {
		          log.error(e);
		      }	finally{
		    	  if(in != null){
						try {
							in.close();
						} catch (IOException e) {
							log.error(e);
						}
					}
		    	  
		    	  if(out != null){
						try {
							out.close();
						} catch (IOException e) {
							log.error(e);
						}
					}
		    	  
		    	  
		      }
		}
//		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		resultMap.put("config", config.toString());
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
    	resultList.add(resultMap);
    	mav.addObject("RESULT",resultList);
		return mav;
		
	}
	
	
}

/**
 * Leer archivo de Config
 *
 */
@Service("configRead")
@CommonsLog
class configRead implements TargetService{
	@Resource private ApplicationContext context;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		
		String filename = ((vo.get("FILENAME") == null ) ? "config.properties" : (String) vo.get("FILENAME"));
		
		StringBuffer sbuffer = new StringBuffer();
		org.springframework.core.io.Resource pathSet = context.getResource("/WEB-INF/classes/config/"+filename);
		log.info(pathSet.exists());
		BufferedReader in = null;
		File f = null;
		if(pathSet.exists()){
			try {
				f = pathSet.getFile();
		     in = new BufferedReader(new FileReader(f));
		      String s;

		      while ((s = in.readLine()) != null) {
		        sbuffer.append(s+"\n");
		      }
		    } catch (IOException e) {
		        log.error(e);
		    } finally {
				if(in != null){
					try {
						in.close();
					} catch (IOException e) {
						log.error(e);
					}
				}
			}
		}
		String resultText = sbuffer.toString();
		mav.addObject("DATA", resultText);
		return mav;
	}
}

/**
 * Recargar el archivo Config 
 *
 */
@Service("configReload")
@CommonsLog
class configReload implements TargetService{
	@Resource(name="config") private Properties config;
	@Resource private ApplicationContext context;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		
		// Comprobar ruta de Config 
		org.springframework.core.io.Resource pathSet = context.getResource("/WEB-INF/classes/config/config.properties");
		log.info(pathSet.exists());
		File f = pathSet.getFile();
		log.info(f.exists());
		BufferedReader in = null;
		if(f.exists()){
    		
		    try {
		        in = new BufferedReader(new FileReader(f));
		        config.load(in);
		        Properties props = PropertiesLoaderUtils.loadProperties(pathSet);
		        //config.setConfig(props);
		        
		        
		      } catch (RuntimeException e) {
		          log.error(e);
		      }	finally{
		    	  if(in != null){
						try {
							in.close();
						} catch (IOException e) {
							log.error(e);
						}
					}
		    	  
		      }
		}
//		
		Map<String,Object> resultMap = new HashMap<String,Object>();
		
		resultMap.put("config", config.toString());
		
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
    	resultList.add(resultMap);
    	mav.addObject("RESULT",resultList);
		return mav;
	}
	
}