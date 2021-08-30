package com.peru.ncov2019.ml.cmm.service;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interfaz para mapeo de servicios
 *
 */
@Service("TargetService")
public interface TargetService {
    public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request) throws Exception;
    
    
}
