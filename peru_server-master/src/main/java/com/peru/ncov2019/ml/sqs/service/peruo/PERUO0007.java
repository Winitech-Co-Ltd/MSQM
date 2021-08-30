package com.peru.ncov2019.ml.sqs.service.peruo;

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
 * Clase para moficiar Encargado Oficial

 * @author Winitech (Gildong, Park)
 *
 */

@CommonsLog
@Service("PERUO0007")
public class PERUO0007 implements TargetService {
	
	@Resource(name="ResCdMapper")
	private ResCdMapper resCdMapper;
	
	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request) throws Exception {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		
		String msg = MlUtil.emptyValidation(
						vo,
						new String[][] {{"MNGR_SN","No. Encargado Oficial"},
										{"ISLPRSN_SN","No. de serie ciudadano"}}
						);
		vo = MlUtil.mappingSn(vo, ScmDao);
		if(!"".equals(msg)){
			resCdMapper.setMsg(mav , "110", msg);
		}
		else{
			// Búsqueda de Encargado Oficial
			List<Map<String, Object>> list = ScmDao.selectList("PERU_MNGR.selectMngr", vo);
			if(list == null || list.size() == 0){
				resCdMapper.setMsg(mav , "104");
			}else {
				// Modificar Encargado Oficial
				int uResult = ScmDao.update("PERU_MNGR.replaceMngr", vo);
				
				if(uResult == 1){
					resCdMapper.setMsg(mav , "100", "El cambio de encargado oficial ha sido extitosa.");
				}
				else{
					resCdMapper.setMsg(mav , "202", "El cambio de encargado oficial ha fallado.");
				}
			}
			
		}
		return mav;
	}
}
