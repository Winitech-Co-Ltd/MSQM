package com.peru.ncov2019.ml.cmm.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;

import com.peru.ncov2019.ml.cmm.util.MlUtil;
import com.peru.ncov2019.ml.push.service.HttpApiLink;
import com.peru.ncov2019.ml.push.vo.PushMsg;
import com.peru.ncov2019.ml.sqs.dao.ScmDao;

import lombok.extern.apachecommons.CommonsLog;
/**
 * Despierta la App en modo dormir usando el Silent push
 * @author Winitech
 *
 */
@CommonsLog
public class SilentPush {
	
	@Resource(name="config") 
	private Properties config;
	
	@Resource(name="ScmDao") 
	private ScmDao scmDao;
	
	
	private String[][] useLangList = {
			{"th", "Tailandia"}
			,{"vi","Vietnam"}
			,{"ru","Rusia"}
			,{"en","Inglés"}
			,{"zh","Chino"}
			,{"ko","Coreano"}
			,{"es","Español"}
			,{"PE","Perú"}};
	
	
	private boolean isWakeup(){
		String pushsend = (config.get("wakeUpPushSend") == null) ? "N" : config.get("wakeUpPushSend")+"";
		if("Y".equals(pushsend)){
			return true;
		}
		else{
			return false;
		}
	}
	

	// 
	private void wakeup_pushSendForMngOne(List<Map<String,Object>> list, boolean isIOS){
		
		if(list != null && list.size() >0){
			for(Map<String,Object> item : list){
				String USR_PUSHID 	= (item.get("USR_PUSHID") == null) 	 ?   "" : item.get("USR_PUSHID")   + "";
				String ISLPRSN_NM 	= (item.get("ISLPRSN_NM") == null) 	 ?   "" : item.get("ISLPRSN_NM")   + "";
				
				if(!"".equals(USR_PUSHID)){
					PushMsg msg = new PushMsg(System.currentTimeMillis());
					msg.setTo(USR_PUSHID);
					Map<String,Object> resultMap = new HttpApiLink().callApi(config, (isIOS) ? msg.getWakeupPushMsgMap_ios() : msg.getWakeupPushMsgMap_android() , true);
					
					log.info("Resultado WAKE UP PUSH : "+resultMap);
					try {
						resultDataInsertD(((isIOS) ? msg.getWakeupPushMsgMap_ios() : msg.getWakeupPushMsgMap_android()), resultMap, item, "01302");
					} catch (Exception e) {
						log.error(e.getLocalizedMessage());
					}
					
				}else{
					log.info("No hay PUSH ID para el usuario [" + ISLPRSN_NM +"]");
				}
			}
		}else{
			log.info("No hay información sobre el usuario objetivo del wakeup");
		}
	}
	
	
	
	
	/**
	 * wakeup push
	 */
	@Scheduled(cron = "${job.cron.push.wakeUpPush}")
	public void wakeUp() {

		if(!isWakeup()){
			return ;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				
			try {
				String timeStamp = MlUtil.getTimeStamp(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss");
				log.info("##Enviar wakeup push## "+timeStamp);
				Map<String, Object> vo = new HashMap<String, Object>();
				vo.put("TIME", timeStamp.replace("-", "").replace(" ","").replace(":", ""));
				
				for(int i=0; i< useLangList.length; i++){
					String useLang = useLangList[i][0];
					String name = useLangList[i][1];
					
					vo.put("USE_LANG", useLang);
					vo.put("TRMNL_KND_CODE", "00401");
					
					List<Map<String,Object>> AndroidList = scmDao.selectList("SQSM_PUSH.selectLastCmm", vo);
					if(AndroidList == null || AndroidList.size() == 0){
						log.info("[" + name + "] No hay usuario para enviar wakeup push (Android)");
					}
					else{
						log.info("Total " + AndroidList.size() + " personas en el [" + name + "] para enviar wakeup push (Android)");
						wakeup_pushSendForMngOne(AndroidList,false);
					}
				}
				
				for(int i=0; i< useLangList.length; i++){
					
					String useLang = useLangList[i][0];
					String name = useLangList[i][1];
					
					vo.put("USE_LANG", useLang);
					vo.put("TRMNL_KND_CODE", "00402");
					List<Map<String,Object>> IOSList = scmDao.selectList("SQSM_PUSH.selectLastCmm", vo);
					if(IOSList == null || IOSList.size() == 0){
						log.info("[" + name + "] No hay usuario para enviar wakeup push (IOS)");
					}
					else{
						log.info("Total " + IOSList.size() + " personas en el [" + name + "] para enviar wakeup push (IOS)");
						wakeup_pushSendForMngOne(IOSList,true);
					}
				}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
			}
		}).start();
	}
	
	
	private void resultDataInsertD(Map<String,Object> sendMap ,Map<String,Object> resultMap, Map<String,Object> targetMap, String pushKdnCode) throws Exception{
		// 
		if(sendMap != null && resultMap != null && targetMap != null){
			
			List<Map<String,Object>> results = (resultMap.get("results") == null) ? null : (ArrayList<Map<String,Object>>)resultMap.get("results");
			
			List<Map<String,Object>> insertInfos = new ArrayList<Map<String,Object>>();
			if(results != null){
				for(int i =0; i < results.size() ; i++){
					Map<String,Object> info = results.get(i);
					
					info.put("PUSH_KND_CODE", pushKdnCode);
					info.put("ISLPRSN_SN",targetMap.get("ISLPRSN_SN"));
					info.put("TRMNL_SN",targetMap.get("TRMNL_SN"));
					info.put("MNGR_SN",targetMap.get("MNGR_SN"));
					info.put("SENDMAP", sendMap.toString());
					
					insertInfos.add(info);
					
				}
				if(insertInfos != null &&insertInfos.size() >1){
					log.info(insertInfos);
				}else if(insertInfos != null && insertInfos.size() == 1){
					scmDao.insert("SQSM_PUSH.insertPushTrnsmisHist", insertInfos.get(0));
				}
			}else{
				log.info("Sin resultado");
			}
			
		}
		else{
			log.error("Falla al registrar la información sobre el resultado de envio push");
		}
		
	}
	
}
