/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月1日 下午5:04:56
 *
 * @Package com.happgo.dlc.base  
 * @Title: DlcFieldConstants.java
 * @Description: DlcFieldConstants.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happgo.dlc.base;

/**
 * ClassName:DlcConstants
 * 
 * @Description: DlcConstants.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月1日 下午5:04:56
 */
public interface DlcConstants {
	
	/**
	 * String the DLC_HOST_IP 
	 */
	String DLC_HOST_IP = "hostIp";
	
	/** 
	* The field DLC_TIME
	*/
	String DLC_TIME = "time";
	
	/** 
	* The field DLC_LEVEL
	*/
	String DLC_LEVEL = "level";

	/** 
	* The field DLC_CONTENT
	*/
	String DLC_CONTENT = "content";
	
	/** 
	* The field DLC_HIGHLIGHT_PRE_TAG
	*/
	String DLC_HIGHLIGHT_PRE_TAG = "<font color='red'>";
	
	/** 
	* The field DLC_HIGHLIGHT_POST_TAG
	*/
	String DLC_HIGHLIGHT_POST_TAG = "</font>";
	
	/** 
	* The field DLC_FRAGMENT_SIZE
	*/
	int DLC_FRAGMENT_SIZE = 50;
	
	/** 
	* The field DEPLOY_CLUSTER_SINGLETON
	*/
	String DEPLOY_CLUSTER_SINGLETON = "clusterSingleton";
	
	/** 
	* The field DEPLOY_NODE_SINGLETON
	*/
	String DEPLOY_NODE_SINGLETON = "nodeSingleton";
	
	/**
	 * String the DLC_LOG_QUERY_SERVICE_NAME 
	 */
	String DLC_LOG_QUERY_SERVICE_NAME = "dlcIgniteService";
}
