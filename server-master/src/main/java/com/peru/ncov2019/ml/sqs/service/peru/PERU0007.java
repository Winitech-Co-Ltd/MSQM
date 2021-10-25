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
 * Clase para buscar lista de autodiagnóstico de la persona en cuarentena
 *
 */

@CommonsLog
@Service("PERU0007")
class PERU0007 implements TargetService {

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
									{"PAGE","Info. de página"}}
					);
			vo = MlUtil.mappingSn(vo, ScmDao);
	
			if(!"".equals(msg)){
				resCdMapper.setMsg(mav , "110", msg);
			}
			else {
				// Búsqueda de lista de autodiagnóstico
				List<Map<String,Object>> list = ScmDao.selectList("PERU_USR.selectSqList",vo);
				
				String totCnt = "0";
                String totPage = "0";
                if(list != null && list.size() > 0){
                       totCnt = list.get(0).get("TOT_CNT")+"";
                       totPage = list.get(0).get("TOT_PAGE")+"";
                }
                
                resCdMapper.setMsg(mav , "100");
                mav.addObject("RES_DATA",list);
                mav.addObject("TOT_CNT",totCnt);
                mav.addObject("TOT_PAGE",totPage);
                
			}
		}
		catch(Exception e){
			log.error(e.getMessage());
			resCdMapper.setMsg(mav , "E100");
		}
		return mav;
	}
	
}
