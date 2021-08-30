package com.peru.ncov2019.ml.cmm.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.apachecommons.CommonsLog;
/**
 * Clase que controla según ID de interfaz
 *
 */
@CommonsLog
@Controller
public class MlInterfaceIdController {
	
	@Resource private ApplicationContext context;
    
    @RequestMapping(value="/info.do")
    @ResponseBody
	public ModelAndView info(HttpServletRequest request, HttpServletResponse response) {
    	ModelAndView mav = new ModelAndView("info");
		return mav;
    }
}
