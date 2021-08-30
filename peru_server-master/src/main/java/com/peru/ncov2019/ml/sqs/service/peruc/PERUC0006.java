package com.peru.ncov2019.ml.sqs.service.peruc;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.peru.ncov2019.ml.cmm.service.TargetService;
import com.peru.ncov2019.ml.cmm.util.ResCdMapper;
import com.peru.ncov2019.ml.sqs.dao.ScmDao;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Clase para buscar clasificación de cuarentena
 *
 */
@CommonsLog
@Service("PERUC0006")
public class PERUC0006 implements TargetService {
	
	@Resource(name="ResCdMapper")
	private ResCdMapper resCdMapper;
	
	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request)  throws Exception {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		try{

			List<Map<String,Object>> list = ScmDao.selectList("PERU_CODE.selectIslSeCodeList", vo);
			mav.addObject("SIZE",(list == null) ? "0" : list.size()+"" );
			mav.addObject("RES_DATA",list);
			resCdMapper.setMsg(mav , "100");

		}
		catch(Exception e){
			log.error(e.getMessage());
			resCdMapper.setMsg(mav , "E100");
		}
		return mav;
	}
}
