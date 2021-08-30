package com.peru.ncov2019.ml.cmm.util;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
/**
 * Clase para mapeo de valorde resultados
 *
 */
@Service("ResCdMapper")
public class ResCdMapper{
	
	@Resource(name="resCdMsg") 
	private Properties resCdMsg;
	
	public void setMsg(ModelAndView mav, String cd) throws UnsupportedEncodingException{
		setMsg(mav, cd, null);
	}
	
	public void setMsg(ModelAndView mav, String cd, String msg) throws UnsupportedEncodingException{
		if(mav != null && mav.getModel().get("RES_MSG") == null){
			mav.addObject("RES_CD",cd);
			mav.addObject("RES_MSG",new String(resCdMsg.getProperty(cd).getBytes(resCdMsg.getProperty("write.encoding","UTF-8")),resCdMsg.getProperty("out.encoding","UTF-8")));
		}
	}

}
