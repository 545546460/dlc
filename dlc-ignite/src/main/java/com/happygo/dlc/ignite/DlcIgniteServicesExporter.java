/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月1日 下午3:25:32
 *
 * @Package com.happygo.dlc.ignite  
 * @Title: DlcIgniteServicesExporter.java
 * @Description: DlcIgniteServicesExporter.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happygo.dlc.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.Ignition;
import org.apache.ignite.services.Service;

import com.happgo.dlc.base.DLCException;
import com.happgo.dlc.base.DlcConstants;
/**
 * DlcIgniteServicesExporter
 * 
 * @Description: DlcIgniteServicesExporter.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月1日 下午3:25:32
 */
public class DlcIgniteServicesExporter {
	
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

	/**
	 * Ignite the ignite 
	 */
	private static final Ignite ignite;
	
	static {
		ignite = Ignition.start("config/dlc-ignite.xml");
	}
	
	/**
	 * @return the service
	 */
	public Object getService() {
		return service;
	}

	/**
	 * @param service the service to set
	 */
	public void setService(Object service) {
		this.service = service;
	}

	/**
	 * @return the serviceName
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * @param serviceName the serviceName to set
	 */
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}
	
	/**
	* @MethodName: export
	* @Description: the export
	*/
	public void export() {
		if (!Service.class.isAssignableFrom(service.getClass())) {
			throw new DLCException(
					"This ignite service is not 'Services' object");
		}
		
		IgniteServices svcs = ignite.services();
		switch (mode) {
		case DlcConstants.DEPLOY_CLUSTER_SINGLETON:
			svcs.deployClusterSingleton(serviceName, (Service) service);
			break;

		case DlcConstants.DEPLOY_NODE_SINGLETON:
			svcs.deployNodeSingleton(serviceName, (Service) service);
			break;

		default:
			throw new DLCException("The mode '" + mode + "' is not supported!");
		}
	}
}
