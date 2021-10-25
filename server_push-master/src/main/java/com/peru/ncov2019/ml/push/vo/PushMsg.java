package com.peru.ncov2019.ml.push.vo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.peru.ncov2019.ml.cmm.util.MlUtil;



/**
 * Clase para almacenar y llamar valores de mensajes Push
 * @author Winitech
 *
 */
public class PushMsg {

	/**
	 * Sujeto que debe recibir Push
	 */
	String to;
	
	/**
	 * Prioridad de la comunicación Push
	 */
	String priority = "high";
	
	/**
	 * Configuración de notificaciones push
	 */
	boolean isSetNoti = false;
	
	/**
	 * Notification title
	 */
	String notititle = "";
	
	/**
	 * Notification body
	 */
	String notibody = "";
	
	/**
	 * Data title
	 */
	String datatitle = "";
	
	/**
	 *  Data message
	 */
	String datamessage = "";
	
	/**
	 *  Data flag
	 */
	String flag = "1";
	
	/**
	 * Lista de sujeto que debe recibir Push
	 */
	List<String> registration_ids;
	
	/**
	 * Utiliza para ​​enviar a IOS en estado Background
	 */
	boolean contentavailable = true;
	
	String senddate = "";
	
	public PushMsg(){
		registration_ids = new ArrayList<String>();
	}
	
	public PushMsg(long crrenttime){
		senddate = MlUtil.getTimeStamp(crrenttime, "yyyy-MM-dd HH:mm:ss");
		registration_ids = new ArrayList<String>();
	}
	
	
	
	/**
	 * flag to get 
	 * @return String 
	 */
	public String getFlag() {
		return flag;
	}

	/**
	 * flag to set
	 * @param flag push flag
	 */
	public void setFlag(String flag) {
		this.flag = flag;
	}

	/**
	 * Mensaje Push en formato de mapa
	 * @return Map
	 */
	public Map<String,Object> getMapMsg(){
		Map<String,Object> msg = new HashMap<String,Object>();
		if(to != null && !"".equals(to)){
			msg.put("to", to);
		}
		msg.put("priority", priority);
		
		if(isSetNoti){
			Map<String,Object> notification = new HashMap<String,Object>();
			notification.put("body", notibody);
			/*notification.put("title", notititle);*/
			notification.put("sound", "msg.mp3"); // Insertar sonido
			msg.put("notification", notification);
			
			Map<String,Object> apns = new HashMap<String,Object>();
			Map<String,Object> headers = new HashMap<String,Object>();
			headers.put("apns-push-type", "alert");
			apns.put("headers", headers);
			msg.put("apns", apns);
		}
		if(registration_ids != null && registration_ids.size()>0){
			msg.put("registration_ids", registration_ids);
		}
		Map<String,Object> data = new HashMap<String,Object>();
		data.put("title", datatitle);
		data.put("message", datamessage);
		data.put("flag", flag);
		msg.put("data", data);
		
		msg.put("content_available", contentavailable);
		
		return msg;
	}
	
	/**
	 * Sujeto que debe recibir Push
	 * @return String
	 */
	public String getTo() {
		return to;
	}
	
	/**
	 * Sujeto que debe recibir Push
	 * @param to 
	 */
	public void setTo(String to) {
		this.to = to;
	}
	
	/**
	 * Prioridad de la comunicación Push
	 * @return String
	 */
	public String getPriority() {
		return priority;
	}
	
	/**
	 * Prioridad de la comunicación Push
	 * @param priority
	 */
	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	/**
	 * Notification body get
	 * @return String
	 */
	public String getNotibody() {
		return notibody;
	}
	
	/**
	 * Notification body set
	 * @param notibody
	 */
	public void setNotibody(String notibody) {
		this.notibody = notibody;
	}
	
	/**
	 * Notification title get
	 * @return String
	 */
	public String getNotititle() {
		return notititle;
	}
	
	/**
	 * Notification title set
	 * @param notititle
	 */
	public void setNotititle(String notititle) {
		this.notititle = notititle;
	}
	
	/**
	 * Data title get
	 * @return String
	 */
	public String getDatatitle() {
		return datatitle;
	}
	
	/**
	 * Data title set
	 * @param datatitle
	 */
	public void setDatatitle(String datatitle) {
		this.datatitle = datatitle;
	}
	
	/**
	 * Data message get
	 * @return String
	 */
	public String getDatamessage() {
		return datamessage;
	}
	
	/**
	 * Data message set
	 * @param datamessage 
	 */
	public void setDatamessage(String datamessage) {
		this.datamessage = datamessage;
	}
	
	/**
	 * 
	 * @return List
	 */
	public List<String> getRegistration_ids() {
		return registration_ids;
	}
	
	/**
	 * 
	 * @param registration_ids
	 */
	public void setRegistration_ids(List<String> registration_ids) {
		this.registration_ids = registration_ids;
	}
	
	/**
	 * Agregar en lista personas en cuarentena que deben recibir Push
	 * @param id
	 */
	public void addRegistration_ids(String id){
		if(this.registration_ids == null){
			this.registration_ids = new ArrayList<String>();
		}
		this.registration_ids.add(id);
	}

	/**
	 * Notification
	 * @return boolean
	 */
	public boolean isSetNoti() {
		return isSetNoti;
	}

	/**
	 * Notification set
	 * @param isSetNoti
	 */
	public void setSetNoti(boolean isSetNoti) {
		this.isSetNoti = isSetNoti;
	}
	
	
	
	
	
	/***
	 * 통신안됨을 해제하기위한 push msg (android)
	 * 2020-05-28
	 * @return
	 */
	public Map<String,Object> getWakeupPushMsgMap_android(){
		Map<String,Object> msg = new HashMap<String,Object>();
		if(to != null && !"".equals(to)){
			msg.put("to", to);
		}
		msg.put("priority", priority);
		
		Map<String,Object> notification = new HashMap<String,Object>();
		notification.put("body", notibody);
		msg.put("notification", notification);
			
		if(registration_ids != null && registration_ids.size()>0){
			msg.put("registration_ids", registration_ids);
		}
		
		msg.put("content_available", contentavailable);
		
		return msg;
	}

	/***
	 * 통신안됨을 해제하기위한 push msg (ios)
	 * 2020-05-28
	 * @return
	 */
	
	public Map<String,Object> getWakeupPushMsgMap_ios(){
		Map<String,Object> msg = new HashMap<String,Object>();
		if(to != null && !"".equals(to)){
			msg.put("to", to);
		}
		msg.put("priority", priority);
		
		Map<String,Object> apns = new HashMap<String,Object>();
		Map<String,Object> headers = new HashMap<String,Object>();
		headers.put("apns-push-type", "background");
		headers.put("apns-priority", 5);
		apns.put("headers", headers);
		msg.put("apns", apns);
		
		if(registration_ids != null && registration_ids.size()>0){
			msg.put("registration_ids", registration_ids);
		}
		
		msg.put("content_available", contentavailable);
		
		return msg;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
