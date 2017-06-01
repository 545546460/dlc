/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月1日 下午3:25:32
 *
 * @Package com.happygo.dlc.ignite  
 * @Title: DlcIgniteServicesDeploy.java
 * @Description: DlcIgniteServicesDeploy.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happygo.dlc.ignite;

import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceConfiguration;

import com.happgo.dlc.base.DLCException;
import com.happgo.dlc.base.DlcConstants;

/**
 * ClassName:DlcIgniteServicesDeploy
 * 
 * @Description: DlcIgniteServicesDeploy.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月1日 下午3:25:32
 */
public class DlcIgniteServicesDeploy {
	
	/** 
	* The field service
	*/
	private Object service;
	
	/** 
	* The field serviceName
	*/
	private String serviceName;
	
	/** 
	* The field mode
	*/
	private String mode;
	
	public Object getService() {
		return service;
	}

	public void setService(Object service) {
		this.service = service;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * Constructor com.happygo.dlc.ignite.DlcIgniteServicesDeploy
	 */
	public DlcIgniteServicesDeploy() {
		if (DlcConstants.DEPLOY_CLUSTER_SINGLETON.equals(mode)) {
			deployClusterSingleton();
		} else {
			deployNodeSingleton();
		}
	}
	
	/**
	* @MethodName: deployClusterSingleton
	* @Description: the method deployClusterSingleton
	*/
	public void deployClusterSingleton() {
		ServiceConfiguration svcCfg = new ServiceConfiguration();
		svcCfg.setName(serviceName);
		svcCfg.setMaxPerNodeCount(1);
		svcCfg.setTotalCount(1);
		if (!Service.class.isAssignableFrom(service.getClass())) {
			throw new DLCException("This ignite service is not implements Services");
		}
		svcCfg.setService((Service) service);
		IgniteConfiguration igniteCfg = new IgniteConfiguration();
		igniteCfg.setServiceConfiguration(svcCfg);
	}
	
	/**
	* @MethodName: deployNodeSingleton
	* @Description: the method deployNodeSingleton
	*/
	public void deployNodeSingleton() {
		ServiceConfiguration svcCfg = new ServiceConfiguration();
		svcCfg.setName(serviceName);
		svcCfg.setMaxPerNodeCount(1);
		if (!Service.class.isAssignableFrom(service.getClass())) {
			throw new DLCException("This ignite service is not implements Services");
		}
		svcCfg.setService((Service) service);
		IgniteConfiguration igniteCfg = new IgniteConfiguration();
		igniteCfg.setServiceConfiguration(svcCfg);
	}
}
