package com.peru.ncov2019.ml.cmm.job;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.apache.commons.codec.binary.Base64;
import org.springframework.scheduling.annotation.Scheduled;

import com.peru.ncov2019.ml.cmm.util.MlUtil;
import com.peru.ncov2019.ml.push.service.HttpApiLink;
import com.peru.ncov2019.ml.push.vo.PushMsg;
import com.peru.ncov2019.ml.sqs.dao.ScmDao;

import lombok.extern.apachecommons.CommonsLog;



/**
 * Clase para enviar Push
 *
 */
@CommonsLog
public class PushGetter{
	
	@Resource(name="config") 
	private Properties config;
	
	@Resource(name="ScmDao") 
	private ScmDao scmDao;
	
	/**
	 * Función para verificar si se ha enviado Push 
	 * @return boolean Envió o no envió Push
	 */
	private boolean isPush(){
		String pushsend = (config.get("pushsend") == null) ? "N" : config.get("pushsend")+"";
		if("Y".equals(pushsend)){
			return true;
		}
		else{
			return false;
		}
	}
	
	/**
	 * Función para transmitir Push a la persona en cuarentena
	 * @param list		Lista de personas que deben recibir
	 * @param pushFlag	flag Push
	 * @param isSetNoti	Configurar o no configurar la notificación 
	 * @param useLang	Idioma en uso de la persona a ser entregada 
	 */
	private void pushSendForUsrGroup(List<Map<String,Object>> list, String pushFlag, boolean isSetNoti, Map<String,Object> useLang){
		
		List<String> registration_ids = new ArrayList<String>();
		int count = 0;
		PushMsg msg = new PushMsg();
		for(Map<String, Object> item : list){
			String ISLPRSN_NM 	= (item.get("ISLPRSN_NM") == null)   ?   "" : item.get("ISLPRSN_NM")   + "";
			String USR_PUSHID 	= (item.get("USR_PUSHID") == null) 	 ?   "" : item.get("USR_PUSHID")   + "";
			String PUSH_MSG_USR = (item.get("PUSH_MSG_USR") == null) ?   "" : item.get("PUSH_MSG_USR") + "";
			if(PUSH_MSG_USR != null && !"".equals(PUSH_MSG_USR) && useLang != null && useLang.get("USE_LANG") != null 
							&& !"ko".equals(useLang.get("USE_LANG"))
							&& !"en".equals(useLang.get("USE_LANG"))
							){
				try {
					PUSH_MSG_USR = new String(Base64.decodeBase64(PUSH_MSG_USR),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			
			if(PUSH_MSG_USR != null && !"".equals(PUSH_MSG_USR)){
				PUSH_MSG_USR = PUSH_MSG_USR.replace("###", ISLPRSN_NM).replace("###", "");
			}
			
			if(!"".equals(USR_PUSHID)){
				registration_ids.add(USR_PUSHID);
			}
			else{
				log.info("ID PUSH del ciudadano (" + ISLPRSN_NM + ")");
			}
			
			count++;
			
			// Enviar a 800 personas a la vez
			if(registration_ids.size() == 800 || count >= list.size()){
				
				msg.setFlag(pushFlag);
				msg.setNotititle(PUSH_MSG_USR);
				msg.setNotibody(PUSH_MSG_USR);
				msg.setDatatitle(PUSH_MSG_USR);
				msg.setDatamessage(PUSH_MSG_USR);
				msg.setRegistration_ids(registration_ids);
				msg.setSetNoti(isSetNoti);
				Map<String,Object> resultMap = new HttpApiLink().callApi(config, msg.getMapMsg(),isSetNoti);
				if(resultMap != null){
					log.info("Resultados de la envío PUSH de autocuarentena : " + resultMap);
				}
				
				registration_ids = new ArrayList<String>();
			}
		}
	}
	
	/**
	 * Función para transmitir Push al administrador
	 * @param list			Lista de administradores para recibir Push
	 * @param pushFlag		flag Push
	 * @param isSetNoti		Configurar o no configurar la notificación 
	 */
	private void pushSendForMngOne(List<Map<String,Object>> list, String pushFlag, boolean isSetNoti){
		
		if(list != null && list.size() >0){
			for(Map<String,Object> item : list){
				
				String ISLPRSN_NM 	= (item.get("ISLPRSN_NM") 	== null)  ?   "" : item.get("ISLPRSN_NM")  	+ "";
				String MNGR_NM    	= (item.get("MNGR_NM")    	== null)  ?   "" : item.get("MNGR_NM")     	+ "";
				String MNGR_PUSHID 	= (item.get("MNGR_PUSHID") 	== null)  ?   "" : item.get("MNGR_PUSHID")	+ "";
				String PUSH_MSG_MNG = (item.get("PUSH_MSG_MNG") == null)  ?   "" : item.get("PUSH_MSG_MNG")	+ "";
				String SLFDGNSS 	= (item.get("SLFDGNSS") 	== null)  ?   "" : item.get("SLFDGNSS")+"";
				
				if(!"".equals(MNGR_PUSHID)){
					PushMsg msg = new PushMsg();
					msg.setTo(MNGR_PUSHID);
					if(pushFlag == "4"){
						msg.setFlag(pushFlag);
						msg.setNotititle("Se confirma el resultado de autodiagnóstico (" + SLFDGNSS + ") de " + ISLPRSN_NM + ".");
						msg.setNotibody("Se confirma el resultado de autodiagnóstico (" + SLFDGNSS + ") de " + ISLPRSN_NM + ".");
						msg.setDatatitle("Se confirma el resultado de autodiagnóstico (" + SLFDGNSS + ") de " + ISLPRSN_NM + ".");
						msg.setDatamessage("Se confirma el resultado de autodiagnóstico (" + SLFDGNSS + ") de " + ISLPRSN_NM + ".");
						msg.setSetNoti(isSetNoti);
					}
					else{
						msg.setFlag(pushFlag);
						msg.setNotititle(PUSH_MSG_MNG);
						msg.setNotibody(PUSH_MSG_MNG);
						msg.setDatatitle(PUSH_MSG_MNG);
						msg.setDatamessage(PUSH_MSG_MNG);
						msg.setSetNoti(isSetNoti);
					}
					
					Map<String,Object> resultMap = new HttpApiLink().callApi(config, msg.getMapMsg(),isSetNoti);
					log.info("Resultado PUSH del Administrador : "+resultMap);
					
				}
				else{
					log.info("ID PUSH del Administrador ["+MNGR_NM+"]");
				}
			}
		}else{
			log.info("Autocuarentena finalizado ");
		}
	}

	
	/**
	 * Lista de códigos y nombres de idiomas que se utilizarán al transmitir el Push
	 */
	private String[][] useLangList = {
			{"th", "Tailandia"}
			,{"vi","Vietnam"}
			,{"ru","Rusia"}
			,{"en","Inglés"}
			,{"zh","Chino"}
			,{"ko","Coreano"}
			,{"es","Español"}
			,{"PE","Perú"}};
	
	/**
	 * Push a transmitir a las personas en cuarentena.
	 * 
	 */
	@Scheduled(cron = "${job.cron.push.getter1}")
	public void getter1() {
		
		if(!isPush()){
			return ;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
			try {
				String timeStamp = MlUtil.getTimeStamp(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
				log.info("##Notificación envío de autodiagnóstico## "+timeStamp);
				Map<String, Object> vo = new HashMap<String, Object>();
				vo.put("TIME", timeStamp.replace("-", "").replace(" ","").replace(":", ""));
				
				for(int i=0; i< useLangList.length; i++){
					String useLang = useLangList[i][0];
					String name = useLangList[i][1];
					
					vo.put("USE_LANG", useLang);
					vo.put("TRMNL_KND_CODE", "00401");
					List<Map<String,Object>> korAndroidList = scmDao.selectList("SQSM_PUSH.selectAlertList", vo);
					if(korAndroidList == null || korAndroidList.size() == 0){
						log.info("[Android]Notificaciones de autodiagnóstico a enviar en tiempo real (" + name + ")");
					}
					else{
						log.info("[Android]envío de autodiagnóstico Total " + korAndroidList.size() + " enviados");
						pushSendForUsrGroup(korAndroidList, "1",false,vo);
					}
				}
				
				for(int i=0; i< useLangList.length; i++){
					
					String useLang = useLangList[i][0];
					String name = useLangList[i][1];
					
					vo.put("USE_LANG", useLang);
					vo.put("TRMNL_KND_CODE", "00402");
					List<Map<String,Object>> korIOSList = scmDao.selectList("SQSM_PUSH.selectAlertList", vo);
					if(korIOSList == null || korIOSList.size() == 0){
						log.info("[IOS]Notificaciones de autodiagnóstico a enviar en tiempo real (" + name + ")");
					}
					else{
						log.info("[IOS]envío de autodiagnóstico Total " + korIOSList.size() + " enviados");
						pushSendForUsrGroup(korIOSList, "1",true,vo);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
			}
		}).start();
	}
	
	
	/**
	 * Push a transmitir a las personas en cuarentena.
	 * Enviar notificación cuando no se ha autodiagnósticado la persona en cuarentena
	 * 
	 */
	@Scheduled(cron = "${job.cron.push.getter1}")
	public void getter2() {

		if(!isPush()){
			return ;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
		
				try {
					String timeStamp = MlUtil.getTimeStamp(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
					log.info("##Consulta de autodiagnósticos realizados (de los ciudadanos en autocuarentena)## "+timeStamp);
					Map<String, Object> vo = new HashMap<String, Object>();
					vo.put("TIME", timeStamp.replace("-", "").replace(" ","").replace(":", ""));
					
					for(int i=0; i< useLangList.length; i++){
						String useLang = useLangList[i][0];
						String name = useLangList[i][1];
						
						vo.put("USE_LANG", useLang);
						vo.put("TRMNL_KND_CODE", "00401");
						List<Map<String,Object>> korAndroidList = scmDao.selectList("SQSM_PUSH.selectSlfdgnssAtList", vo);
						if(korAndroidList == null || korAndroidList.size() == 0){
							log.info("[Android]Autodiagnóstico no realizado: No hay.");
						}
						else{
							log.info("[Android]Autodiagnóstico no realizado: Total " + korAndroidList.size() + "personas.");
							pushSendForUsrGroup(korAndroidList, "2", false, vo);
						}
					}
					
					for(int i=0; i< useLangList.length; i++){
						
						String useLang = useLangList[i][0];
						String name = useLangList[i][1];
						
						vo.put("USE_LANG", useLang);
						vo.put("TRMNL_KND_CODE", "00402");
						List<Map<String,Object>> korIOSList = scmDao.selectList("SQSM_PUSH.selectSlfdgnssAtList", vo);
						if(korIOSList == null || korIOSList.size() == 0){
							log.info("[IOS]Autodiagnóstico no realizado: No hay.");
						}
						else{
							log.info("[IOS]Autodiagnóstico no realizado: Total " + korIOSList.size() + "personas.");
							pushSendForUsrGroup(korIOSList, "2", true, vo);
						}
					}
					
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage()); 
				}
		}}).start();
	}
	
	
	/**
	 * Push a transmitir al Encargado Oficial.
	 * Enviar notificación cuando no se ha autodiagnósticado la persona en cuarentena
	 * 
	 */
	@Scheduled(cron = "${job.cron.push.getter1}")
	public void getter3() {

		if(!isPush()){
			return ;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					String timeStamp = MlUtil.getTimeStamp(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
					log.info("##Consulta de autodiagnósticos realizados (Administrador)## "+timeStamp);
					Map<String, Object> vo = new HashMap<String, Object>();
					vo.put("TIME", timeStamp.replace("-", "").replace(" ","").replace(":", ""));
					log.info("vo : " + vo);
					// android
					vo.put("TRMNL_KND_CODE", "00401");
					List<Map<String,Object>> androidlist = scmDao.selectList("SQSM_PUSH.selectSlfdgnssAtList2", vo);
					if(androidlist == null || androidlist.size() == 0){
						log.info("[Android]Autodiagnóstico no realizado: No hay. (Administrador)");
					}
					else{
						log.info("[Android]Autodiagnóstico no realizado: Total " + androidlist.size() + " personas. (Administrador)");
						pushSendForMngOne(androidlist, "3",false);
					}
					// IOS
					vo.put("TRMNL_KND_CODE", "00402");
					List<Map<String,Object>> IOSlist = scmDao.selectList("SQSM_PUSH.selectSlfdgnssAtList2", vo);
					if(IOSlist == null || IOSlist.size() == 0){
						log.info("[IOS]Autodiagnóstico no realizado: No hay. (Administrador)");
					}
					else{
						log.info("[IOS]Autodiagnóstico no realizado: Total" + IOSlist.size() + " personas. (Administrador)");
						pushSendForMngOne(IOSlist, "3",true);
					}
		
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}}).start();
	}
	
	
	/**
	 * Push a transmitir al Encargado Oficial.
	 * Enviar notificación cuando haya una anomalía en los resultados del autodiagnóstico de la persona en cuarentena
	 * 
	 */
	@Scheduled(cron = "${job.cron.push.getter1}")
	public void getter4() {

		if(!isPush()){
			return ;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					String timeStamp = MlUtil.getTimeStamp(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
					log.info("##Consulta de resultado de autodiagnóstico con síntomas (Administrador) ## "+timeStamp);
					Map<String, Object> vo = new HashMap<String, Object>();
					vo.put("TIME", timeStamp.replace("-", "").replace(" ","").replace(":", ""));
					
					// android
					vo.put("TRMNL_KND_CODE", "00401");
					List<Map<String,Object>> androidlist = scmDao.selectList("SQSM_PUSH.selectSlfdgnssAbnormalityAtList", vo);
					if(androidlist == null || androidlist.size() == 0){
						log.info("[Android]Resultado de autodiagnóstico con síntomas : No hay. (Administrador)");
					}
					else{
						log.info("[Android]Resultado de autodiagnóstico con síntomas : Total " + androidlist.size() + " personas. (Administrador)");
						pushSendForMngOne(androidlist, "4",false);
					}
					// IOS
					vo.put("TRMNL_KND_CODE", "00402");
					List<Map<String,Object>> IOSlist = scmDao.selectList("SQSM_PUSH.selectSlfdgnssAbnormalityAtList", vo);
					if(IOSlist == null || IOSlist.size() == 0){
						log.info("[IOS]Resultado de autodiagnóstico con síntomas : No hay. (Administrador)");
					}
					else{
						log.info("[IOS]Resultado de autodiagnóstico con síntomas : Total " + IOSlist.size() + " personas. (Administrador)");
						pushSendForMngOne(IOSlist, "4",true);
					}
					
				
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}}).start();
	}
	
	
	/**
	 * Push transmitido a ambas personas en cuarentena y encargado oficial.
	 * En caso de abandono de ubicación de cuarentena, envia notificación de advertencia a los desertores y transmite  la informacón de los desetores al Encargado Oficial
	 * 
	 */
	@Scheduled(cron = "${job.cron.push.getter1}")
	public void getter5() {
		
		if(!isPush()){
			return ;
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					
					String timeStamp = MlUtil.getTimeStamp(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
					log.info("##Consulta de personas fuera de área de cuarentena (Ciudadano, Adminsitrador) ## "+timeStamp);
					Map<String, Object> vo = new HashMap<String, Object>();
					vo.put("TIME", timeStamp.replace("-", "").replace(" ","").replace(":", ""));
					
					for(int i=0; i< useLangList.length; i++){
						String useLang = useLangList[i][0];
						String name = useLangList[i][1];
						
						vo.put("USE_LANG", useLang);
						vo.put("TRMNL_KND_CODE", "00401");
						List<Map<String,Object>> korAndroidList = scmDao.selectList("SQSM_PUSH.selectScesnLocation", vo);
						if(korAndroidList == null || korAndroidList.size() == 0){
							log.info("[Android]Personas en autocuarentena que salieron fuera de área de cuarentena");
						}
						else{
							log.info("[Android]Notificaciones de fuera de área de cuarentena : Total " + korAndroidList.size() + " personas.");
							pushSendForUsrGroup(korAndroidList, "5",false,vo);
						}
					}
					
					for(int i=0; i< useLangList.length; i++){
						
						String useLang = useLangList[i][0];
						String name = useLangList[i][1];
						
						vo.put("USE_LANG", useLang);
						vo.put("TRMNL_KND_CODE", "00402");
						List<Map<String,Object>> korIOSList = scmDao.selectList("SQSM_PUSH.selectScesnLocation", vo);
						if(korIOSList == null || korIOSList.size() == 0){
							log.info("[IOS]Personas en autocuarentena que salieron fuera de área de cuarentena.");
						}
						else{
							log.info("[IOS]Notificaciones de fuera de área de cuarentena : Total " + korIOSList.size() + " personas.");
							pushSendForUsrGroup(korIOSList, "5",true,vo);
						}
					}
					
					
					// android
					vo.put("TRMNL_KND_CODE", "00401");
					List<Map<String,Object>> androidlist = scmDao.selectList("SQSM_PUSH.selectScesnLocation2", vo);
					if(androidlist == null || androidlist.size() == 0){
						log.info("[Android]Personas en autocuarentena que salieron fuera de área de cuarentena. (administrador)");
					}
					else{
						log.info("[Android]Total " + androidlist.size() + " notificaciones de fuera de área de cuarentena. (administrador)");
						pushSendForMngOne(androidlist, "5",false);
					}
					// IOS
					vo.put("TRMNL_KND_CODE", "00402");
					List<Map<String,Object>> IOSlist = scmDao.selectList("SQSM_PUSH.selectScesnLocation2", vo);
					if(IOSlist == null || IOSlist.size() == 0){
						log.info("[IOS]Personas en autocuarentena que salieron fuera de área de cuarentena. (administrador)");
					}
					else{
						log.info("[IOS]Notificaciones de fuera de área de cuarentena : Total  " + IOSlist.size() + " personas. (administrador)");
						pushSendForMngOne(IOSlist, "5",true);
					}
				
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}}).start();
	}

	
	/**
	 * Push a transmitir al Encargado Oficial.
	 * Enviar notificación cuando no se recibe el GPS de la persona en cuarentena
	 * 
	 */
	@Scheduled(cron = "${job.cron.push.getter1}")
	public void getter6() {

		if(!isPush()){
			return ;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
		
					String timeStamp = MlUtil.getTimeStamp(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
					log.info("##Consultas GPS no recibidos (administrador)## "+timeStamp);
					Map<String, Object> vo = new HashMap<String, Object>();
					vo.put("TIME", timeStamp.replace("-", "").replace(" ","").replace(":", ""));
					
					// 안드로이드
					vo.put("TRMNL_KND_CODE", "00401");
					List<Map<String,Object>> androidlist = scmDao.selectList("SQSM_PUSH.selectGpsStatus", vo);
					if(androidlist == null || androidlist.size() == 0){
						log.info("[Android]GPS no recibidos (administrador)");
					}
					else{
						log.info("[Android]Total " + androidlist.size() + " de GPS no recibidos (administrador)");
						pushSendForMngOne(androidlist, "6", false);
					}
					
					// IOS
					vo.put("TRMNL_KND_CODE", "00402");
					List<Map<String,Object>> IOSlist = scmDao.selectList("SQSM_PUSH.selectGpsStatus", vo);
					if(IOSlist == null || IOSlist.size() == 0){
						log.info("[IOS]GPS no recibidos (administrador)");
					}
					else{
						log.info("[IOS]Total " + IOSlist.size() + " de GPS no recibidos (administrador)");
						pushSendForMngOne(IOSlist, "6",true);
					}
		
					
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}}).start();
	}
	
	
	/**
	 * Notificación push de FAKE GPS
	 * 
	 */
	@Scheduled(cron = "${job.cron.push.getter1}")
	public void getter7() {

		if(!isPush()){
			return ;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
		
					String timeStamp = MlUtil.getTimeStamp(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
					log.info("##Notificación push de FAKE GPS## "+timeStamp);
					Map<String, Object> vo = new HashMap<String, Object>();
					vo.put("TIME", timeStamp.replace("-", "").replace(" ","").replace(":", ""));
					
					// android
					vo.put("TRMNL_KND_CODE", "00401");
					List<Map<String,Object>> androidlist = scmDao.selectList("SQSM_PUSH.selectFakeGPSUser", vo);
					if(androidlist == null || androidlist.size() == 0){
						log.info("[Android]No hay usuario que utilicen FAKE GPS");
					}
					else{
						log.info("[Android]Total " + androidlist.size() + " personas usan FAKE GPS");
						pushSendForMngOne(androidlist, "7", false);
					}
					
					// IOS
					vo.put("TRMNL_KND_CODE", "00402");
					List<Map<String,Object>> IOSlist = scmDao.selectList("SQSM_PUSH.selectFakeGPSUser", vo);
					if(IOSlist == null || IOSlist.size() == 0){
						log.info("[IOS]No hay usuario que utilicen FAKE GPS");
					}
					else{
						log.info("[IOS]Total " + IOSlist.size() + " personas usan FAKE GPS");
						pushSendForMngOne(IOSlist, "7", true);
					}
		
					
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}}).start();
	}
	
	
	
	
}
