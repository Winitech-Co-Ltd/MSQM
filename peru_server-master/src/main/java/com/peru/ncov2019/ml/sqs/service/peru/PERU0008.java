package com.peru.ncov2019.ml.sqs.service.peru;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Clase para buscar la última información de autodiagnóstico de la persona en cuarentena
 * @author Winitech (Gildong, Park)
 *
 */

@CommonsLog
@Service("PERU0008")
class PERU0008 implements TargetService {

	@Resource(name="ResCdMapper")
	private ResCdMapper resCdMapper;
	
	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request)  throws Exception {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
		Map<String,Object> resData = new HashMap<String,Object>();
		try{
			String msg = MlUtil.emptyValidation(
					vo,
					new String[][] {{"ISLPRSN_SN","No. de serie ciudadano"}}
					);
			vo = MlUtil.mappingSn(vo, ScmDao);
			
			if(!"".equals(msg)){
				resCdMapper.setMsg(mav , "110", msg);
			}
			else {
				// Búsqueda de la última información de autodiagnóstico de la persona en cuarentena
				List<Map<String,Object>> list = ScmDao.selectList("PERU_USR.selectLastSq",vo);
				if(list != null && list.size() > 0){
					resData.putAll(list.get(0));
				}
				
				if(resData != null){
					resList.add(resData);
					mav.addObject("RES_DATA",resList);
				}
				resCdMapper.setMsg(mav , "100");
				
				// Actualización de información del terminal
				msg = MlUtil.emptyValidation(
						vo,
						new String[][] {{"ISLPRSN_SN","No. de serie ciudadano"},
							{"TRMNL_SN","No. de serie terminal"}}
						);
		
				if("".equals(msg)){
					ScmDao.update("PERU_USR.updateTrmnlGpsAndLastCommDt", vo);
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
