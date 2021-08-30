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
 * Clase para buscar Encargado Oficial
 *
 */

@CommonsLog
@Service("PERU0013")
public class PERU0013 implements TargetService {
	
	@Resource(name="ResCdMapper")
	private ResCdMapper resCdMapper;
	
	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request)  throws Exception {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		try{
			
			String ECSHG_MNGR_SN = (vo.get("ECSHG_MNGR_SN") == null) ? "" : vo.get("ECSHG_MNGR_SN")+"";
			String MNGR_LOGIN_ID = (vo.get("MNGR_LOGIN_ID") == null) ? "" : vo.get("MNGR_LOGIN_ID")+"";
			String ISLPRSN_SN = (vo.get("ISLPRSN_SN") == null) ? "" : vo.get("ISLPRSN_SN")+"";

			vo = MlUtil.mappingSn(vo, ScmDao);
			
			if("".equals(ECSHG_MNGR_SN) && "".equals(MNGR_LOGIN_ID) && "".equals(ISLPRSN_SN)){
				resCdMapper.setMsg(mav , "110", "Por favor, introduzca alguna condición.");
			}
			else {
				// Buscar información de Encargado Oficial
				List<Map<String,Object>> list = ScmDao.selectList("PERU_USR.selectChrgMngr0013",vo);
				if(list == null || list.size() == 0){
					resCdMapper.setMsg(mav , "104");	
				}
				else{
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

