package com.peru.ncov2019.ml.sqs.service.peruo;

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
 * Clase para buscar lista de persona en cuarentena
 * @author Winitech (Gildong, Park)
 *
 */

@CommonsLog
@Service("PERUO0002")
public class PERUO0002 implements TargetService {

	@Resource(name="config") 
	private Properties config;
	
	@Resource(name="ResCdMapper")
	private ResCdMapper resCdMapper;
	
	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request)  throws Exception {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		
		List<Map<String,Object>> list = null;
		String msg = MlUtil.emptyValidation(
						vo,
						new String[][] {{"MNGR_SN","No. de Encargado Oficial"}}
						);
		vo = MlUtil.mappingSn(vo, ScmDao);
		
		if(!"".equals(msg)){
			resCdMapper.setMsg(mav , "110", msg);
		}else{
			list = ScmDao.selectList("PERU_MNGR.selectMngrList",vo);
			if(list != null && list.size() == 1){
				Map<String,Object> mngrInfo = list.get(0);
				mngrInfo.putAll(vo);
				mngrInfo.put("DISTANCE", config.get("distance"));
				List<Map<String,Object>> selectIslprsnList = ScmDao.selectList("PERU_MNGR.selectIslprsnList",mngrInfo);
				
				String totCnt = "0";
				String totPage = "0"; // 
				if(selectIslprsnList != null && selectIslprsnList.size() > 0){
					totCnt = selectIslprsnList.get(0).get("TOT_CNT")+"";
					totPage = selectIslprsnList.get(0).get("TOT_PAGE")+"";
				}
				
				resCdMapper.setMsg(mav , "100");
				mav.addObject("RES_DATA",selectIslprsnList);
				mav.addObject("TOT_CNT",totCnt);
				mav.addObject("TOT_PAGE",totPage);
				
			}else if(list != null && list.size() > 1){
				resCdMapper.setMsg(mav , "105");
			}else{
				resCdMapper.setMsg(mav , "104");
			}
		}
		return mav;
	}
	
	
}
