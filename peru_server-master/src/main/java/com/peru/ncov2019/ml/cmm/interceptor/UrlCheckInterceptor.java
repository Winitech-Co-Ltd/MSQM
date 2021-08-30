package com.peru.ncov2019.ml.cmm.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import lombok.extern.apachecommons.CommonsLog;
/**
 * Clase de proceso específico interceptar una llamada URL 
 *
 */
@CommonsLog
public class UrlCheckInterceptor extends HandlerInterceptorAdapter{
    
    static final String[] EXCLUDE_URL_LIST = {
        "/info"
    };
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String reqUrl = request.getRequestURL().toString();
        
        //log.info("Session ID".concat(request.getSession().getId()).concat(" created at ").concat(new Date().toString()));
        for( String target : EXCLUDE_URL_LIST ){        
            if(reqUrl.indexOf(target)>-1){
                return true;    
            }            
        }
        
        response.sendRedirect(request.getContextPath() + "/info.do");
    	
        return false;        
    }

}
