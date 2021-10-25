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
import com.peru.ncov2019.ml.push.service.DistanceCalculator;
import com.peru.ncov2019.ml.sqs.dao.ScmDao;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Clase para la modificar información sobre la persona en autocuarentena 
 *
 */

@CommonsLog
@Service("PERU0005")
public class PERU0005 implements TargetService{
	
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
		
		try{
			String msg = MlUtil.emptyValidation(
					vo,
					new String[][] {{"ISLPRSN_SN","No. de serie ciudadano"},
									{"TRMNL_SN","No. de serie terminal"},
									{"TELNO","No. de Celular"},
									{"ISLLC_DPRTMNT_CODE","Código Regional"},
									{"ISLLC_PRVNCA_CODE","Código Provincial"},
									{"ISLLC_DSTRT_CODE","Código Distrital"}}
					);
			vo = MlUtil.mappingSn(vo, ScmDao);
			
			
			if(!"".equals(msg)){
				resCdMapper.setMsg(mav , "110", msg);
			}
			else {
				// 1. Verificar información de la persona en cuarentena
				Map<String,Object> selectUserInfo = ScmDao.selectOne("PERU_USR.selectUserInfo", vo);
				if(selectUserInfo == null || selectUserInfo.isEmpty()){
					resCdMapper.setMsg(mav , "103");
				}
				else{
					selectUserInfo.putAll(vo);
					// Tabla atualizada de persona en cuarentena 
					int uResult = ScmDao.update("PERU_USR.updateSqsm1", selectUserInfo);
					if(uResult == 0){
						resCdMapper.setMsg(mav , "202" ,"Falló el cambio."); 
					}
					else{
						// Tabla actualizada de terminal 
						int uResult2 = ScmDao.update("PERU_USR.updateSqsm2", selectUserInfo);
						if(uResult2 == 0){
							resCdMapper.setMsg(mav , "202" , "Falló el cambio.");
						}
						else{
							// Rebúsqueda de información actualizada de persona en cuarentena 
							Map<String,Object> item = ScmDao.selectOne("PERU_USR.selectUserInfo", selectUserInfo);
							if(item == null){
								resCdMapper.setMsg(mav , "103");
							}
							else{	
								// Verificar si registrará la información de abandono (Sujeto de registrar en caso de ser YY) 
								Map<String,Object> selectGpsRegistYYNN = ScmDao.selectOne("PERU_USR.selectGpsRegistYYNN", vo);
								if(selectGpsRegistYYNN != null && "YY".equals(selectGpsRegistYYNN.get("GPS_REGIST_YYNN"))){
									
									double cDistance  = (config.get("distance") == null) ? 5000 : Double.parseDouble(config.get("distance")+"");
									// Ubicación de cuarentena
									double lon1 = (item.get("ISLLC_XCNTS") == null ) ? 0 : Double.parseDouble(item.get("ISLLC_XCNTS")+"");
									double lat1 = (item.get("ISLLC_YDNTS") == null ) ? 0 : Double.parseDouble(item.get("ISLLC_YDNTS")+"");
									
									// Ubicación actual de la persona en cuarentena 
									double lon2 = (item.get("ISLPRSN_XCNTS") == null ) ? 0 : Double.parseDouble(item.get("ISLPRSN_XCNTS")+"");
									double lat2 = (item.get("ISLPRSN_YDNTS") == null ) ? 0 : Double.parseDouble(item.get("ISLPRSN_YDNTS")+"");
									
									if(lon1 != 0.0 && lat1 != 0.0 && lon2 != 0.0 && lat2 != 0.0
									   && !Double.isNaN(lon1) && !Double.isNaN(lat1) && !Double.isNaN(lon2) && !Double.isNaN(lat2)
									   && !Double.isInfinite(lon1) && !Double.isInfinite(lat1) && !Double.isInfinite(lon2) && !Double.isInfinite(lat2))
									{
										double distance = new DistanceCalculator().distance(lat1, lon1, lat2, lon2, "meter");
										// En caso de haber abandonado 
										if(cDistance < Math.abs(distance)) {
											item.put("SCESN_SE", "G");
											// Agregar en tabla ubicación de abandono de la persona en cuarentena
											ScmDao.insert("PERU_USR.insertScesnLocation", item);
										}
										// En caso de no ser una persona que haya abandonado su ubicación de cuarentena, realizar búsqueda en la tabla de abandono y eliminar 
										else {
											// Eliminar d ela tabla de ubicación de abandono de la persona en cuarentena
											ScmDao.delete("PERU_USR.deleteScesnLocation", item);
										}
									}
								}
								List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
								item.put("DISTANCE", config.get("distance"));
								list.add(item);
								mav.addObject("RES_DATA",list);
								resCdMapper.setMsg(mav , "100");
							}
						}
					}
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
