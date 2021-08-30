package com.peru.ncov2019.ml.cmm.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peru.ncov2019.ml.cmm.encryption.AES256Util;
import com.peru.ncov2019.ml.cmm.service.TargetService;
import com.peru.ncov2019.ml.cmm.util.MlUtil;
import com.peru.ncov2019.ml.sqs.dao.ScmDao;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@Controller
public class MlInterfaceSecController {
	
	@Resource private ApplicationContext context;
	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	private ModelAndView errorResult(Throwable e, String resultCode) {

		ModelAndView errmav = new ModelAndView();
		errmav.setViewName("jsonView");
		Map<String, Object> cmv2 = new HashMap<String, Object>();
		log.error(resultCode);
		errmav.addObject("RESULT_CODE", resultCode);
		errmav.addObject("RESULT", "ERR");
		if(e.getCause() != null && e.getCause().getMessage() != null){
			errmav.addObject("ERROR_DETAIL_MSG", "Falla"/*e.getCause().getMessage()*/);
		}else{
			errmav.addObject("ERROR_DETAIL_MSG", "Falla"/*e.getMessage()*/);
		}
		errmav.addObject("ERROR_CLASS", e.getClass().toString());
		errmav.addObject("ERROR_MSG", "Falla" /*e.getLocalizedMessage()*/);
		errmav.addObject("RES_MSG", "Ha ocurrido un error en el sistema.");
		errmav.addObject("RES_CD", resultCode);
		List<Map<String, Object>> rvo = new ArrayList<Map<String, Object>>();
		rvo.add(cmv2);

		//errmav.addObject("RESULT", rvo);

		log.error("@@END@@ " + "{ERR}");
		
		StackTraceElement[] stack = e.getStackTrace();
		for(int i=0; i< stack.length ; i++){
			log.error(stack[i].toString());
		}
		
		return errmav;

	}

    @SuppressWarnings("unchecked")
	@RequestMapping(value="/servicecall.do", method= RequestMethod.POST)
    @ResponseBody
	public ModelAndView sqsmServiceProc(@RequestBody String requestStringBody, HttpServletRequest request, HttpServletResponse response) {

    	
		String urlDecodeBody = requestStringBody;
		ModelAndView mav = new ModelAndView();
		try {
			
			String jsonString = urlDecodeBody;
			
			JSONObject json = (JSONObject) new JSONParser().parse(jsonString);

			
			HashMap<String, Object> mappingParmMap = null;

			mappingParmMap = new ObjectMapper().readValue(json.toJSONString(), HashMap.class);
			
			/********* Colocar trim en todos los valores **********************/
			MlUtil.mapTrim(mappingParmMap);
			/*********************************/
			
			/********* Buscar un servicio adecuado utilizando un nombre del servicio ***********/
			String servicenm = (String) mappingParmMap.get("IFID");
			TargetService ts = (TargetService) context.getBean(servicenm);
			/************************************************/
			/**********Extraer información de terminal para ser utilizada en común.********/

			Map<String, Object> parmMap = null;
			if("PERUK0001".equals(servicenm)){
				parmMap = new HashMap<String,Object>();
			}else{
				parmMap = (Map<String, Object>) mappingParmMap.get("PARM");
			}
			

			Map<String,Object> logMap = new HashMap<String,Object>();
			logMap.putAll(mappingParmMap);
			
			/************ Dar un parámetro y devolver el resultado. ***************/
			mav = ts.serviceCall(parmMap, request);
			/************************************************/
			
			return mav;
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return errorResult(e,"80001");
		} catch (JsonParseException e) {
			e.printStackTrace();
			return errorResult(e,"80002");
		} catch (JsonMappingException e) {
			e.printStackTrace();
			return errorResult(e,"80003");
		} catch (IOException e) {
			e.printStackTrace();
			return errorResult(e,"80004");
		} catch (DataAccessException e) {
			e.printStackTrace();
			return errorResult(e,"80005");
		} catch(TransactionException e){
			e.printStackTrace();
			return errorResult(e,"80006");
		}
		  catch( SecurityException e){
			  e.printStackTrace();
			  if(e.getCause() == null) return errorResult(e, "89002");
			  return errorResult(e.getCause(), "89002");
		  }
		  catch(IllegalArgumentException e){
			  e.printStackTrace();
			  if(e.getCause() == null) return errorResult(e, "89003");
			  return errorResult(e.getCause(), "89003"); 
		} 
		catch (Exception e) {
			e.printStackTrace();
			return errorResult(e, "89999");
		}
	}
    
    
    @RequestMapping(value="/info.do")
    @ResponseBody
	public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
    	ModelAndView mav = new ModelAndView("info");
		return mav;
    }
    
}
