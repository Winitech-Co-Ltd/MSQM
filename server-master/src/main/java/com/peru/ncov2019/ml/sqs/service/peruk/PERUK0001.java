package com.peru.ncov2019.ml.sqs.service.peruk;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.peru.ncov2019.ml.cmm.service.TargetService;


@Service("PERUK0001")
public class PERUK0001  implements TargetService{

	public static final String key = "peru20public0722";     // 변경해서 사용해주세요      Por favor cambie y use
	public static final String vector = "20public0722peru";  // 변경해서 사용해주세요      Por favor cambie y use
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		
		mav.addObject("RES_CD","100");
		mav.addObject("RES_MSG","success");
		mav.addObject("RES", key+vector);
		
		return mav;
	}
	
}