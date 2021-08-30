package com.peru.ncov2019.ml.cmm.application;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;

import lombok.extern.apachecommons.CommonsLog;

/**
 * Clase para verificar si la ejecución del servidor ha operado normalmente
 *
 */

@CommonsLog
public class MlApplication{
	
	@Resource(name="config") 
	private Properties config;
	
	/**
	 * Función para ver la hora de inicio y IP cuando se ejecuta el servidor
	 */
	public void serverStart(){
		log.info("================================================================================");
		log.info("Server Start "+ (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()));
		log.info(getIp());
		log.info("================================================================================");
		
	}
	

	/**
	 * Función que devuelve el IP del servidor
	 * @return String Servidor IP
	 */
	private String getIp(){
		StringBuffer sb = new StringBuffer();
		try
		{
		    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();)
		    {
		        NetworkInterface intf = en.nextElement();
		        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();)
		        {
		            InetAddress inetAddress = enumIpAddr.nextElement();
		            if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress())
		            {
		            	sb.append(" ").append(inetAddress.getHostAddress().toString());
		            }
		        }
		    }
		    
		}
		catch (SocketException ex) {
			log.error(ex.getLocalizedMessage());
		}
		
		return sb.toString();
	}
	
}
