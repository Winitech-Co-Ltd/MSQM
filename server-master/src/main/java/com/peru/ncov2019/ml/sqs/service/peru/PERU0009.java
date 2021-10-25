package com.peru.ncov2019.ml.sqs.service.peru;

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
 * Clase para registrar información de autodiagnóstico de la persona en cuarentena
 * @author Winitech (Gildong, Park)
 *
 */

@CommonsLog
@Service("PERU0009")
class PERU0009 implements TargetService{
	
	@Resource(name="ResCdMapper")
	private ResCdMapper resCdMapper;
	
	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request)  throws Exception {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		try{
			String msg = MlUtil.emptyValidation(
					vo,
					new String[][] {{"ISLPRSN_SN","No. de serie ciudadano"},
									{"PYRXIA_AT","Fiebre"},
									{"COUGH_AT","Tos"},
									{"SORE_THROAT_AT","Dolor de garganta"},
									{"DYSPNEA_AT","Dificultad respiratoria"}}
					);
			vo = MlUtil.mappingSn(vo, ScmDao);
			
			if(!"".equals(msg)){
				resCdMapper.setMsg(mav , "110", msg);
			}
			else {
				// Registrar información de autodiagnóstico
				int iResult = ScmDao.insert("PERU_USR.insertSq",vo);
				if(iResult < 1){						
					resCdMapper.setMsg(mav , "201");								
				}
				else{
					resCdMapper.setMsg(mav , "100");
					// Búsqueda de última información de autodiagnóstico
					List<Map<String,Object>> list = ScmDao.selectList("PERU_USR.selectLastSq",vo);
					mav.addObject("RES_DATA",list);
				}
				
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