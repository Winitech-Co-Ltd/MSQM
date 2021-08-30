package com.peru.ncov2019.ml.sqs.service.peru;

import java.util.ArrayList;
import java.util.HashMap;
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
 * Clase para transferir información de ubicación de la personas en autocuarentena
 * @author Winitech (Gildong, Park)
 *
 */

@CommonsLog
@Service("PERU0006")
public class PERU0006 implements TargetService {
	
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
		
		List<Map<String,Object>> resList = new ArrayList<Map<String,Object>>();
		Map<String,Object> resData = new HashMap<String,Object>();
		
		try{
			String msg = MlUtil.emptyValidation(
					vo,
					new String[][] {{"ISLPRSN_SN","No. de serie ciudadano"},
									{"TRMNL_SN","No. de serie terminal"},
									{"ISLPRSN_XCNTS","Coordenada X actual"},
									{"ISLPRSN_YDNTS","Coordenada Y actual"}}
					);
			vo = MlUtil.mappingSn(vo, ScmDao);
			
			if(!"".equals(msg)){
				resCdMapper.setMsg(mav , "110", msg);
			}
			else {
				
				// Ubicación actual de la persona en cuarentena 
				double lon1 = (vo.get("ISLPRSN_XCNTS") == null ) ? 0.0 : Double.parseDouble(vo.get("ISLPRSN_XCNTS")+"");
				double lat1 = (vo.get("ISLPRSN_YDNTS") == null ) ? 0.0 : Double.parseDouble(vo.get("ISLPRSN_YDNTS")+"");
				
				// fake로 들어오면 다 거른다.
				if(vo.get("GPS_ONOFF").equals("FAKE")){
					log.info("fake gps 입니다");
					resCdMapper.setMsg(mav , "100");
				}
				else if(lon1 != 0.0 && lat1 != 0.0 && !Double.isNaN(lon1) && !Double.isNaN(lat1) && !Double.isInfinite(lon1) && !Double.isInfinite(lat1)){
					
					boolean insertResult = false;
					boolean updateResult = false;
					
					// Última actualización de información de ubicación
					List<Map<String,Object>> selectUserLocation = ScmDao.selectList("PERU_USR.selectUserLocation", vo);
					// INSERT si no hay ningún valor en la tabla de ubicación
					if(selectUserLocation == null || selectUserLocation.size() == 0){
						int iResult = ScmDao.insert("PERU_USR.insertLocation",vo);
						if(iResult < 1){;
							resCdMapper.setMsg(mav , "601");
						}
						else{
							resCdMapper.setMsg(mav , "100","Exitoso registro de ubicación");
							insertResult = true;
						}
					}
					// UPDATE si la tabla de ubicación tiene un valor
					else{
						int uResult = ScmDao.update("PERU_USR.updateLocation", vo);
						if(uResult == 0){
							resCdMapper.setMsg(mav , "202");
						}
						else{
							resCdMapper.setMsg(mav , "100", "Exitoso registro de ubicación");
							updateResult = true;
						}
					}
					
					if(insertResult || updateResult){
						// Verificar si registrará la información de abandono (Sujeto de registrar en caso de ser YY) 
						Map<String,Object> selectGpsRegistYYNN = ScmDao.selectOne("PERU_USR.selectGpsRegistYYNN", vo);
						if(selectGpsRegistYYNN != null && "YY".equals(selectGpsRegistYYNN.get("GPS_REGIST_YYNN"))){
								
							// Obtener información de la persona en cuarentena
							Map<String,Object> item = ScmDao.selectOne("PERU_USR.selectUserInfo", vo);
								
							double cDistance  = (config.get("distance") == null) ? 5000 : Double.parseDouble(config.get("distance")+"");
							// Ubicación de cuarentena
							double lon2 = (item.get("ISLLC_XCNTS") == null ) ? 0.0 : Double.parseDouble(item.get("ISLLC_XCNTS")+"");
							double lat2 = (item.get("ISLLC_YDNTS") == null ) ? 0.0 : Double.parseDouble(item.get("ISLLC_YDNTS")+"");
							if(lon1 != 0.0 && lat1 != 0.0 && lon2 != 0.0 && lat2 != 0.0 &&
							   !Double.isNaN(lon1) && !Double.isNaN(lat1) && !Double.isNaN(lon2) && !Double.isNaN(lat2) &&
							   !Double.isInfinite(lon1) && !Double.isInfinite(lat1) && !Double.isInfinite(lon2) && !Double.isInfinite(lat2))
							{
										
								double distance = DistanceCalculator.distance(lat1, lon1, lat2, lon2, "meter");
								// En caso de haber abandonado 
								if(cDistance < Math.abs(distance)) {
									vo.put("SCESN_SE", "G");
									// Agregar en tabla ubicación de abandono de la persona en cuarentena
									ScmDao.insert("PERU_USR.insertScesnLocation", vo);
									// Agregar a la tabla de historial de abandono de la persona en cuarentena
									ScmDao.insert("PERU_USR.insertScesnLocationHist", vo);
								}
								// En caso de no ser una persona que haya abandonado su ubicación de cuarentena, realizar búsqueda en la tabla de abandono y eliminar 
								else {
									ScmDao.delete("PERU_USR.deleteScesnLocation", vo);
								}
							}
						}
					}
				}
				else{
					log.info("Está 0.0 en la información de ubicación.");
					resCdMapper.setMsg(mav , "100");
				}
			}
			
			// Actualización de información del terminal
			ScmDao.update("PERU_USR.updateTrmnlGpsAndLastCommDt", vo);

			if(resData != null){
				resList.add(resData);
				mav.addObject("RES_DATA", resList);				
			}

		}
		catch(Exception e){
			log.error(e.getMessage());
			resCdMapper.setMsg(mav , "E100");
		}
		return mav;
	}
}
