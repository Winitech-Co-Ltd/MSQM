package com.peru.ncov2019.ml.sqs.service.peruc;

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
 * Clases para buscar Provincia
 *
 */

@CommonsLog
@Service("PERUC0003")
public class PERUC0003 implements TargetService {
	
	@Resource(name="ResCdMapper")
	private ResCdMapper resCdMapper;
	
	@Resource(name="ScmDao")
	private ScmDao ScmDao;
	
	@Override
	public ModelAndView serviceCall(Map<String, Object> vo, HttpServletRequest request)  throws Exception {
		log.info("1");
		log.info(vo);
		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		try{
			String msg = MlUtil.emptyValidation(
					vo,
					new String[][] {{"DPRTMNT_CODE","Código Regional"}}
					);
			vo = MlUtil.mappingSn(vo, ScmDao);
			
			if(!"".equals(msg)){
				resCdMapper.setMsg(mav , "110", msg);
			}
			else {
				Map<String,Object> parm = new HashMap<String,Object>();
				parm.put("DPRTMNT_CODE", vo.get("DPRTMNT_CODE"));
				List<Map<String,Object>> list = ScmDao.selectList("PERU_CODE.selectSggCdList", parm);
				mav.addObject("SIZE",(list == null) ? "0" : list.size()+"" );
				mav.addObject("RES_DATA",list);
				resCdMapper.setMsg(mav , "100");
			}
		}
		catch(Exception e){
			log.error(e.getMessage());
			resCdMapper.setMsg(mav , "E100");
		}
		return mav;
	}
}