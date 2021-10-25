package com.peru.ncov2019.ml.cmm.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.peru.ncov2019.ml.cmm.service.CmmService;
/**
 * Clase para aplicar un cambio en tiempo real de un archivo Config 
 *
 */
public class ConfigCheck {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	@Resource(name="CmmService")
	private CmmService cmmService;
	
	@Scheduled(cron = "0 */1 * * * ?")
	public void propertiesCheck(){
		cmmService.configReload();
	}

}
