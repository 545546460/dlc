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

import java.net.InetAddress;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteServices;
import org.apache.ignite.Ignition;
import org.apache.ignite.services.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

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
	 * The field mode
	 */
	private String mode;

	/**
	 * Ignite the ignite
	 */
	private static final Ignite ignite;
	
	/**
	 * The LOOGER
	 */
	private static final Logger LOGGER = LogManager.getLogger(DlcIgniteServicesExporter.class);

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
	 * @param service
	 *            the service to set
	 */
	public void setService(Object service) {
		this.service = service;
	}

	/**
	 * @return the mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(String mode) {
		this.mode = mode;
	}

	/**
	 * @MethodName: export
	 * @Description: the export
	 */
	public void export() {
		try {
			ThreadContext.put("hostIp", InetAddress.getLocalHost().getHostAddress());
		} catch (Exception e) {
			LOGGER.warn("<<<=== Do not get the node hostIp ===>>>");
		}
		if (!Service.class.isAssignableFrom(service.getClass())) {
			throw new DLCException(
					"This ignite service is not 'Services' object");
		}
	
		IgniteServices svcs = ignite.services();
		switch (mode) {
		case DlcConstants.DEPLOY_CLUSTER_SINGLETON:
			svcs.deployClusterSingleton(
					DlcConstants.DLC_LOG_QUERY_SERVICE_NAME, (Service) service);
			break;

		case DlcConstants.DEPLOY_NODE_SINGLETON:
			svcs.deployNodeSingleton(DlcConstants.DLC_LOG_QUERY_SERVICE_NAME,
					(Service) service);
			break;

		default:
			throw new DLCException("The mode '" + mode + "' is not supported!");
		}
		LOGGER.info("<<<=== Dlc ignite service deploy successfully ===>>>");
	}
}
