package com.peru.ncov2019.ml.sqs.service.peru;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.peru.ncov2019.ml.cmm.service.TargetService;
import com.peru.ncov2019.ml.cmm.util.MlUtil;
import com.peru.ncov2019.ml.cmm.util.ResCdMapper;
import com.peru.ncov2019.ml.sqs.dao.ScmDao;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Clase para consulta de información sobre la persona en autocuarentena
 */

@CommonsLog
@Service("PERU0004")
public class PERU0004 implements TargetService {

	@Resource(name="config") 
	private Properties config;
	
	@Resource(name="ResCdMapper")
	private ResCdMapper resCdMapper;

	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request)  throws Exception {
		log.info("1 : " + vo);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		
		try{
			String msg = MlUtil.emptyValidation(
					vo,
					new String[][] {{"ISLPRSN_SN","No. de serie ciudadano"},
									{"TRMNL_SN","No. de serie terminal"}}
					);
			vo = MlUtil.mappingSn(vo, ScmDao);
			
			if(!"".equals(msg)){
				resCdMapper.setMsg(mav , "110", msg);
			}
			else {
				// Consulta para buscar información sobre la persona en autocuarentena 
				Map<String,Object> item = ScmDao.selectOne("PERU_USR.selectUserInfo_PERU_0004", vo);
				
				if(item == null){
					resCdMapper.setMsg(mav , "103");
				}
				else{
					List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
					item.put("DISTANCE", config.get("distance"));
					list.add(item);
					mav.addObject("RES_DATA",list);
					resCdMapper.setMsg(mav , "100");
				}
			}
		}
		catch(Exception e){
			log.error(e.getMessage());
			resCdMapper.setMsg(mav , "E100");
		}
		return mav;
	}
}
