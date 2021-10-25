package com.peru.ncov2019.ml.sqs.service.peru;

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
 * Clase para autentificar persona en cuarentena 
 *
 */

@CommonsLog
@Service("PERU0012")
class PERU0012 implements TargetService {

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
					new String[][] {{"TELNO","No. de Celular"}
									,{"TRMNL_KND_CODE","Código tipo de dispositivo"}	
									,{"TRMNL_NM","Nombre de dispositivo"}
									,{"USE_LANG","Idioma"}
									,{"ISLPRSN_NM","Nombre"}
									,{"SEXDSTN_CODE","Código de género"}
									,{"NLTY_CODE","Código de nacionalidad"}
									,{"BRTHDY","Fecha de nacimiento"}
									,{"ECSHG_MNGR_SN","No. Encargado Oficial"}}
					);
			
			vo = MlUtil.mappingSn(vo, ScmDao);
			
			// Debe haber al menos un valor para la ubicación de cuarentena XY, Región, Provincia, Distrito. 
			String ISLLC_XCNTS = (vo.get("ISLLC_XCNTS") == null) ? "" : vo.get("ISLLC_XCNTS")+"";
			String ISLLC_YDNTS = (vo.get("ISLLC_YDNTS") == null) ? "" : vo.get("ISLLC_YDNTS")+"";
			String ISLPRSN_DPRTMNT_CODE = (vo.get("ISLPRSN_DPRTMNT_CODE") == null) ? "" : vo.get("ISLPRSN_DPRTMNT_CODE")+"";
			String ISLPRSN_PRVNCA_CODE = (vo.get("ISLPRSN_PRVNCA_CODE") == null) ? "" : vo.get("ISLPRSN_PRVNCA_CODE")+"";
			String ISLPRSN_DSTRT_CODE = (vo.get("ISLPRSN_DSTRT_CODE") == null) ? "" : vo.get("ISLPRSN_DSTRT_CODE")+"";
			
			if("".equals(ISLLC_XCNTS) && "".equals(ISLLC_YDNTS) && "".equals(ISLPRSN_DPRTMNT_CODE) && "".equals(ISLPRSN_PRVNCA_CODE) && "".equals(ISLPRSN_DSTRT_CODE)  ){
				msg = "Por favor, introduzca región, provincia, distrito, coordenadas X, Y del área de cuarentena.";
			}
			
			
			Map<String,Object> selectSnUser = null;
			Map<String,Object> selectSnMobile = null;

			if(!"".equals(msg)){
				resCdMapper.setMsg(mav , "110", msg);
			}
			else {
				// Verificar información de Encargado Oficial 
				List<Map<String,Object>> selectChrgMngr = ScmDao.selectList("PERU_USR.selectChrgMngr", vo);
				
				if(selectChrgMngr == null || selectChrgMngr.size() == 0){
					resCdMapper.setMsg(mav , "104"); 
				}
				else if(selectChrgMngr.size() > 1){
					resCdMapper.setMsg(mav , "105"); 
				}
				else{
					vo.put("NEW_MNGR_LOGIN_ID", selectChrgMngr.get(0).get("LOGIN_ID"));
					
					// Comprobar si es un usuario existente (duplicado)
					List<Map<String,Object>> selectUser = ScmDao.selectList("PERU_USR.selectUser", vo);
					
					if(selectUser == null || selectUser.size() == 0){
						
						// En caso de ser un nuevo usuario INSERT
						int iResult = ScmDao.insert("PERU_USR.insertUser", vo);
						if(iResult>0){
							
							selectSnUser = ScmDao.selectOne("PERU_USR.selectSnUser", vo);
							selectSnUser.putAll(vo);
							// Traer el valor que acaba de insertar, juntar con la nueva información y UPDATE
							int uResult = ScmDao.update("PERU_USR.updateSqsm1", selectSnUser);
							
							if(uResult>0){
								resCdMapper.setMsg(mav , "100");
							}else{
								resCdMapper.setMsg(mav , "202" , "Se produjo un error al actualizar la información del usuario.");
							}
							
						}else{
							
							resCdMapper.setMsg(mav, "201" , "Se produjo un error al registrar la información del usuario.");
						}
					}
					else if(selectUser.size() == 1){
						
						selectSnUser = ScmDao.selectOne("PERU_USR.selectSnUser", selectUser.get(0));
						selectSnUser.putAll(vo);
						// UPDATE para usuarios existentes
						int uResult = ScmDao.update("PERU_USR.updateSqsm1", selectSnUser);
						
						if(uResult>0){
							resCdMapper.setMsg(mav , "100");
						}else{
							resCdMapper.setMsg(mav , "202" , "Se produjo un error al actualizar la información del usuario.");
						}
						
					}
					else{
						// Si la información se ingresa varias veces, se usa el último ID de usuario.
						selectSnUser = ScmDao.selectOne("PERU_USR.selectSnUser", selectUser.get(0));
						selectSnUser.putAll(vo);
						// UPDATE para usuarios existentes
						int uResult = ScmDao.update("PERU_USR.updateSqsm1", selectSnUser);
						if(uResult>0){
							resCdMapper.setMsg(mav , "100");
						}
						else{
							resCdMapper.setMsg(mav , "202" , "Se produjo un error al actualizar la información del usuario.");
						}
						
					}
					
					// En caso de crear un nuevo usuario haya sigo exitoso o verificado
					if(mav != null && mav.getModelMap() != null && mav.getModelMap().get("RES_CD") != null && "100".equals(mav.getModelMap().get("RES_CD"))){
						
						// INSERT a la tabla de historial el estado de la persona en autocuarentena como 00301
						ScmDao.insert("PERU_USR.insertStateCode", selectSnUser);
					
						// Comprobar si es un terminal existente (duplicado)
						List<Map<String,Object>> selectTrmnl = ScmDao.selectList("PERU_USR.selectTrmnl", selectSnUser);
						
						if(selectTrmnl == null || selectTrmnl.size() == 0){
							// INSERT si es un nuevo terminal
							int iResult = ScmDao.insert("PERU_USR.insertTrmnl", selectSnUser);
							if(iResult>0){
	
								// Traer el valor que acaba de insertar, juntar con la nueva información y UPDATE
								selectSnMobile = ScmDao.selectOne("PERU_USR.selectTrmnl", selectSnUser);
								selectSnMobile.putAll(vo);
								
								int uResult = ScmDao.update("PERU_USR.updateTrmnl", selectSnMobile);
								if(uResult>0){
									resCdMapper.setMsg(mav , "100");
								}
								else{
									resCdMapper.setMsg(mav , "202" , "Se produjo un error al actualizar la información del dispositivo.");
								}
									
							}
							else{
								resCdMapper.setMsg(mav, "201" , "Se produjo un error al registrar la información del dispositivo.");
							}
						}
						else if(selectTrmnl.size() == 1){
							selectSnMobile = ScmDao.selectOne("PERU_USR.selectTrmnl", selectTrmnl.get(0));
							selectSnMobile.putAll(vo);
							
							// UPDATE si es un terminal existente
							int uResult = ScmDao.update("PERU_USR.updateTrmnl", selectSnMobile);
							if(uResult>0){
								resCdMapper.setMsg(mav , "100");	
							}
							else{
								resCdMapper.setMsg(mav , "202" , "Se produjo un error al actualizar la información del dispositivo.");
							}
								
						}
						else{
							resCdMapper.setMsg(mav , "106");
						}
					}
				}
				
				
				if(mav.getModelMap().get("RES_CD") != null && "100".equals(mav.getModelMap().get("RES_CD")) && selectSnMobile != null){
					
					List<Map<String,Object>> list = ScmDao.selectList("PERU_USR.selectUserInfo", selectSnMobile);
					if(list != null && list.size() >0){
						list.get(0).put("DISTANCE", config.get("distance"));

						
					}
					mav.addObject("RES_DATA",list);
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
