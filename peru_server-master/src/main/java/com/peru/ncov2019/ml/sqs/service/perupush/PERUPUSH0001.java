package com.peru.ncov2019.ml.sqs.service.perupush;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import com.peru.ncov2019.ml.cmm.service.TargetService;
import com.peru.ncov2019.ml.cmm.util.MlUtil;
import com.peru.ncov2019.ml.sqs.dao.ScmDao;

import lombok.extern.apachecommons.CommonsLog;

@CommonsLog
@Service("PERUPUSH0001")
public class PERUPUSH0001 implements TargetService{

	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		try{
			
			
			String msg = MlUtil.emptyValidation(
					vo,
					new String[][] {{"MESSAGEID","No hay message ID (mensaje ID)"}});
			vo = MlUtil.mappingSn(vo, ScmDao);
			if(!"".equals(msg)){
				mav.addObject("RES_MSG",msg);
				mav.addObject("RES_CD","110");
			}else{
				
				List<Map<String,Object>> list = ScmDao.selectList("PERU_PUSH.selectPushMsg",vo);
				
				ScmDao.insert("PERU_PUSH.insertPushTrnsmisHist_client", vo);
				
				if(list != null && list.size() == 1){
					String TRMNL_SN = (list.get(0).get("TRMNL_SN") == null) ? "" : list.get(0).get("TRMNL_SN")+"";
					String ISLPRSN_SN = (list.get(0).get("ISLPRSN_SN") == null) ? "" : list.get(0).get("ISLPRSN_SN")+"";
					if(!"".equals(TRMNL_SN) && !"".equals(ISLPRSN_SN)){
						ScmDao.update("PERU_PUSH.updateTrmnlLastCommAck", list.get(0));
					}else{
						log.info("Falló renovación "+list.get(0));
					}
				}else{
					log.info("Información de terminal no válida");
				}
				
				mav.addObject("RES_CD","100");
				mav.addObject("RES_MSG","Exitoso");
			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			mav.addObject("RES_CD","E100");
			log.error(e.getMessage());
			mav.addObject("RES_MSG","Ha ocurrido un error en el sistema.");
		}
		return mav;
	}
}
