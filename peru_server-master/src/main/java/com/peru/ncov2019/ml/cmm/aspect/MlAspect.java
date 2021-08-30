package com.peru.ncov2019.ml.cmm.aspect;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peru.ncov2019.ml.cmm.encryption.AES256Util;
import com.peru.ncov2019.ml.cmm.util.MlUtil;
import com.peru.ncov2019.ml.sqs.dao.ScmDao;
import com.peru.ncov2019.ml.sqs.service.peruk.PERUK0001;

import lombok.extern.apachecommons.CommonsLog;

@Component
@Aspect
@CommonsLog
public class MlAspect extends Thread {
	@Resource
	private ApplicationContext context;
	@Resource(name = "ScmDao")
	private ScmDao ScmDao;

	@Pointcut("execution(* com.peru.ncov2019.ml.cmm.controller.MlInterfaceSecController.sqsmServiceProc(..))")
	public void controllerTargetMethod() {
	}

	@SuppressWarnings("unchecked")
	@Around("controllerTargetMethod()")
	public Object logAround(ProceedingJoinPoint thisJoinPoint) throws Throwable {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		
		// Obtener información de autenticación de usuario
		String peruuname = (request.getHeader("peruuname") == null) ? "" : request.getHeader("peruuname");
		String peruoname = (request.getHeader("peruoname") == null) ? "" : request.getHeader("peruoname");
		

		ModelAndView mav = new ModelAndView();
		mav.setViewName("jsonView");
		AES256Util aesPub = new AES256Util(PERUK0001.key+PERUK0001.key, PERUK0001.vector);
		AES256Util aes = null;
		Object[] args = thisJoinPoint.getArgs();
		try {
			String jsonString = null;
			if (args[0] instanceof String) {
				jsonString = args[0] + "";

				HashMap<String, Object> mappingParmMap = null;

				mappingParmMap = new ObjectMapper().readValue(jsonString, HashMap.class);
				String IFID = (mappingParmMap.get("IFID") == null) ? "" : (String) mappingParmMap.get("IFID");
				log.info("IFID :" + IFID);
				// Cifrado / descifrado
				if (!"".equals(peruuname) || !"".equals(peruoname)) {
					
					String secKey = null;
					String key = null;
					String vector = null;
					JSONObject json = null;
					try{
					
						if (!"".equals(peruuname)) {
							Map<String, Object> userHashParm = new HashMap<String, Object>();
							userHashParm.put("peruuname", aesPub.decrypt(peruuname));
							
							String temp = ScmDao.selectOne("PERU_USR.selectUserHash", userHashParm);
							
							secKey = temp;
							key = secKey.substring(0, 16);
							vector = secKey.substring(16, 32);
						} else if (!"".equals(peruoname)) {
	
							Map<String, Object> userHashParm = new HashMap<String, Object>();
							userHashParm.put("peruoname", aesPub.decrypt(peruoname));
							String temp = ScmDao.selectOne("PERU_MNGR.selectUserHash", userHashParm);
	
							secKey = temp;
							key = secKey.substring(0, 16);
							vector = secKey.substring(16, 32);
						}
						// descifrado
						json = (JSONObject) new JSONParser().parse(jsonString);
						String parm = (json.get("PARM") == null) ? "" : json.get("PARM") + "";
						if (!"".equals(parm)) {
							aes = new AES256Util(key + key, vector);
							parm = aes.decrypt(parm);
							
							JSONObject jParm = (JSONObject) new JSONParser().parse(parm);
							jParm.put("ISHEAD", "true");
							json.put("PARM", jParm);
						}
					
					}catch (Exception e) {
						log.error(e.getMessage());
						mav.addObject("RES_CD", "EI0002");
						mav.addObject("RES_MSG", "Invalid information(encryption error)");
						return mav;
					}
					
					args[0] = json.toJSONString();

					Object retVal = thisJoinPoint.proceed(args);

					mav = (ModelAndView) retVal;
					// Cifrado
					if (mav.getModel() != null && mav.getModel().get("RES_DATA") != null) {
						String jArray = null;
						String sResData = null;
						try {
							if (mav.getModel().get("RES_CD") != null && !"100".equals(mav.getModel().get("RES_CD"))) {
								log.error("RES_CD : " + mav.getModel().get("RES_CD") + " / " + "RES_MSG : "+ mav.getModel().get("RES_MSG"));
							}

							if (mav.getModel().get("RESULT_CODE") != null) {
								log.error("RESULT_CODE : " + mav.getModel().get("RESULT_CODE"));
								log.error("RESULT : " + mav.getModel().get("RESULT"));
							}

							//if(IFID.length()== 8 && "PERU".equals(IFID.substring(0,4))){
								// ISLPRSN_INNB
								List<Map<String, Object>> resData = (List<Map<String, Object>>) mav.getModel().get("RES_DATA");
								for(int i=0; i<resData.size();i++){
									Map<String, Object> item = (Map<String, Object>) resData.get(i);
									String islprsnSn = (item.get("ISLPRSN_SN")==null) ? "" : item.get("ISLPRSN_SN")+"";
									String islprsnInnb = (item.get("ISLPRSN_INNB")==null) ? "" : item.get("ISLPRSN_INNB")+"";
									
									if(!"".equals(islprsnSn) && islprsnSn.length() < 32){
										
										// db - Extracción de valor
										if("".equals(islprsnInnb)){
											islprsnInnb = ScmDao.selectOne("PERU_USR.selectUserInbb", item);
											islprsnInnb = (islprsnInnb == null) ? islprsnSn : islprsnInnb;
										}
										
										item.put("ISLPRSN_SN", islprsnInnb);
										item.remove("ISLPRSN_INNB");
										resData.remove(i);
										resData.add(i, item);
									}
								}

								mav.addObject("RES_DATA", resData);
							//}

							jArray = new ObjectMapper().writeValueAsString(mav.getModel().get("RES_DATA"));
							// !!!Cifrar resultado!!!
							sResData = aes.encrypt(jArray.toString());

						} catch (JsonParseException e) {
							makeErrorMsg(e);
						} catch (JsonMappingException e) {
							makeErrorMsg(e);
						} catch (IOException e) {
							makeErrorMsg(e);
						} catch (NoSuchAlgorithmException e) {
							makeErrorMsg(e);
						} catch (GeneralSecurityException e) {
							makeErrorMsg(e);
						}

						mav.getModel().put("RES_DATA", sResData);
					} else {
						log.error("RES_DATA IS NOT EXIST");
						log.error(mav);
					}
					return mav;

				} 
				// Cifrado / descifrado de datos con clave pública
				else if ("PERU0012".equals(IFID) || "PERUO0001".equals(IFID) || "PERU0011".equals(IFID) || IFID.indexOf("PERUC")>=0
						|| "PERU0013".equals(IFID)) {
					// Descifrar
					JSONObject json = (JSONObject) new JSONParser().parse(jsonString);
					String parm = (json.get("PARM") == null) ? "" : json.get("PARM") + "";
					if (!"".equals(parm)) {
						try{
							parm = aesPub.decrypt(parm);
	
							JSONObject jParm = (JSONObject) new JSONParser().parse(parm);
							json.put("PARM", jParm);
						}catch (Exception e) {
							mav.addObject("RES_CD", "EI0002");
							mav.addObject("RES_MSG", "Invalid information(encryption error)");
							return mav;
						}
					}
					args[0] = json.toJSONString();

					Object retVal = thisJoinPoint.proceed(args);

					mav = (ModelAndView) retVal;
					// cifrado
					if (mav.getModel() != null && mav.getModel().get("RES_DATA") != null) {
						String jArray = null;
						String sResData = null;
						try {
							if (mav.getModel().get("RES_CD") != null && !"100".equals(mav.getModel().get("RES_CD"))) {
								log.error("RES_CD : " + mav.getModel().get("RES_CD") + " / " + "RES_MSG : "+ mav.getModel().get("RES_MSG"));
							}

							if (mav.getModel().get("RESULT_CODE") != null) {
								log.error("RESULT_CODE : " + mav.getModel().get("RESULT_CODE"));
								log.error("RESULT : " + mav.getModel().get("RESULT"));
							}

							// Agregar clave de cifrado
							if ("PERUO0001".equals(IFID)) {
								List<Map<String, Object>> resData = (List<Map<String, Object>>) mav.getModel()
										.get("RES_DATA");
								Map<String, Object> item = (Map<String, Object>) resData.get(0);
								String mngrSn = item.get("MNGR_SN") + "";
								String uuid = UUID.randomUUID().toString();
								uuid = uuid.replaceAll("-", "");
								item.put("MNGR_INNB", mngrSn+uuid);
								String privateKey = MlUtil.getSHA256(uuid);
								item.put("ENCPT_DECD_KEY", privateKey.substring(0, 32));

								ScmDao.update("PERU_MNGR.updateHashAndPrivateKey", item);

								item.put("MNGR_SN", mngrSn + uuid);
								item.remove("MNGR_INNB");
								resData.remove(0);
								resData.add(0, item);

								mav.addObject("RES_DATA", resData);
							}

							else if ("PERU0012".equals(IFID)) {
								List<Map<String, Object>> resData = (List<Map<String, Object>>) mav.getModel().get("RES_DATA");
								Map<String, Object> item = (Map<String, Object>) resData.get(0);
								String islprsnSn = item.get("ISLPRSN_SN") + "";
								String uuid = UUID.randomUUID().toString();
								uuid = uuid.replaceAll("-", "");
								item.put("ISLPRSN_INNB", islprsnSn+uuid);
								String privateKey = MlUtil.getSHA256(uuid);
								item.put("ENCPT_DECD_KEY", privateKey.substring(0, 32));

								ScmDao.update("PERU_USR.updateHashAndPrivateKey", item);

								item.put("ISLPRSN_SN", islprsnSn + uuid);
								item.remove("ISLPRSN_INNB");
								resData.remove(0);
								resData.add(0, item);

								mav.addObject("RES_DATA", resData);
							}
							
							jArray = new ObjectMapper().writeValueAsString(mav.getModel().get("RES_DATA"));
							sResData = aesPub.encrypt(jArray.toString());

						} catch (JsonParseException e) {
							makeErrorMsg(e);
						} catch (JsonMappingException e) {
							makeErrorMsg(e);
						} catch (IOException e) {
							makeErrorMsg(e);
						} catch (NoSuchAlgorithmException e) {
							makeErrorMsg(e);
						} catch (GeneralSecurityException e) {
							makeErrorMsg(e);
						}

						mav.getModel().put("RES_DATA", sResData);
					} else {
						log.error("RES_DATA IS NOT EXIST");
						log.error(mav);
					}
					return mav;

				}
				// 공개키용 서비스 호출시 암호화 없음 Sin cifrado al invocar el servicio para clave pública
				else if ("PERUK0001".equals(IFID)) {
					Object retVal = thisJoinPoint.proceed();
					return retVal;
				}
				else {
					mav.addObject("RES_CD", "EI0001");
					mav.addObject("RES_MSG", "Invalid information");
					return mav;
				}

			} else {
				Object retVal = thisJoinPoint.proceed();
				return retVal;
			}

		} catch (RuntimeException e) {
			log.error(mav);
			mav = makeErrorMsg(e);
		} catch (Exception e) {
			log.error(mav);
			mav = makeErrorMsg(e);
		}
		mav.addObject("RES_CD","EI0002");
		mav.addObject("RES_MSG","Unknown Exception");
		return mav;
	}
	
	/**
	 * 에러 표출용 함수 Función de expresión de error
	 * @param e : Exception
	 * @return 에러값 Error
	 */
	private ModelAndView makeErrorMsg(Exception e){
		ModelAndView errmav = new ModelAndView();
		errmav.setViewName("jsonView");
		Map<String, Object> cmv2 = new HashMap<String, Object>();
		errmav.addObject("RESULT_CODE", "89998");
		errmav.addObject("RESULT", "ERR");
		if (e.getCause() != null && e.getCause().getMessage() != null) {
			errmav.addObject("ERROR_DETAIL_MSG", e.getCause().getMessage());
		} else {
			errmav.addObject("ERROR_DETAIL_MSG", e.getMessage());
		}
		errmav.addObject("ERROR_CLASS", e.getClass().toString());
		errmav.addObject("ERROR_MSG", e.getLocalizedMessage());
		List<Map<String, Object>> rvo = new ArrayList<Map<String, Object>>();
		rvo.add(cmv2);

		StackTraceElement[] stack = e.getStackTrace();
		for (int i = 0; i < stack.length; i++) {
			log.error(stack[i].toString());
		}
		return errmav;
	}

}

