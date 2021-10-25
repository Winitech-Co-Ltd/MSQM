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
 * Clase para inicio de sesión del Encargado Oficial
 * @author Winitech (Gildong, Park)
 */

@CommonsLog
@Service("PERUO0001")
public class PERUO0001 implements TargetService {
	
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
						new String[][] {{"LOGIN_ID","ID"},
										{"PASSWORD","Contraseña"}}
						);
		vo = MlUtil.mappingSn(vo, ScmDao);
		
		if(!"".equals(msg)){
			resCdMapper.setMsg(mav , "110", msg);
		}
		else{
			// Búsqueda de Encargado Oficial 
			list = ScmDao.selectList("PERU_MNGR.selectMngrList",vo);
			if(list != null && list.size() == 1){
				
				String pushId = (list.get(0).get("PUSHID") == null) ? "" : list.get(0).get("PUSHID")+"";
				String newPushId = (vo.get("PUSHID") == null) ? "" : vo.get("PUSHID")+"";
				
				list.get(0).put("IS_LOGIN", "Y");
				// En caso de ser igual el ID del Push actual y nuevo Push 
				if(!"".equals(newPushId) && !"".equals(pushId) && newPushId.equals(pushId)){
					ScmDao.update("PERU_MNGR.updateMngr", list.get(0));
					resCdMapper.setMsg(mav , "100");
					mav.addObject("RES_DATA",list);
				}
				else{
					list.get(0).put("PUSHID", newPushId);
					list.get(0).putAll(vo);
					int uResult = ScmDao.update("PERU_MNGR.updateMngr", list.get(0));
					if(uResult == 1){
						list = ScmDao.selectList("PERU_MNGR.selectMngrList",list.get(0));
						resCdMapper.setMsg(mav , "100");
						mav.addObject("RES_DATA",list);
					}else{
						resCdMapper.setMsg(mav , "202");
					}
				}
				
			}
			else if(list != null && list.size() > 1){
				resCdMapper.setMsg(mav , "102");
			}
			else{
				resCdMapper.setMsg(mav , "101");
			}
		}
		
		mav.addObject("SIZE",(list != null) ? list.size()+"" : "0");
		return mav;
	}
}
