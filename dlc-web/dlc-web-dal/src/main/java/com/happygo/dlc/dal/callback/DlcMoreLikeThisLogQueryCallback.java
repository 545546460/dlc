/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年6月13日 下午9:57:07
*
* @Package com.happygo.dlc.dal.callback
* @Title: DlcMoreLikeThisLogQueryCallback.java
* @Description: DlcMoreLikeThisLogQueryCallback.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.happygo.dlc.dal.callback;

import java.util.List;

import org.apache.ignite.lang.IgniteCallable;
import org.apache.ignite.resources.ServiceResource;

import com.happgo.dlc.base.DlcConstants;
import com.happgo.dlc.base.bean.DlcLog;
import com.happgo.dlc.base.ignite.service.DlcIgniteService;

/**
 * ClassName:DlcMoreLikeThisLogQueryCallback
 * @Description: DlcMoreLikeThisLogQueryCallback.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年6月13日 下午9:57:07
 */
public class DlcMoreLikeThisLogQueryCallback implements IgniteCallable<List<DlcLog>> {
	
	/**
	 * long the serialVersionUID 
	 */
	private static final long serialVersionUID = -1791890440195334801L;
	
	/**
	 * DlcIgniteService the dlcIgniteService 
	 */
	@ServiceResource(serviceName = DlcConstants.DLC_LOG_QUERY_SERVICE_NAME, 
			proxyInterface = DlcIgniteService.class)
	private DlcIgniteService dlcIgniteService;
	
	/**
	 * String the keyWord 
	 */
	private String keyWord;

	private String appName;
	
	/**
	 * String the queryMode 
	 */
	private String queryMode;
	
	/**
	 * Constructor com.happygo.dlc.dal.callback.DlcMoreLikeThisLogQueryCallback
	 * @param keyWord
	 * @param queryMode
	 */
	public DlcMoreLikeThisLogQueryCallback(String keyWord, String appName, String queryMode) {
		this.keyWord = keyWord;
		this.appName = appName;
		this.queryMode = queryMode;
	}

	/* (non-Javadoc)
	 * @see java.util.concurrent.Callable#call()
	 */
	public List<DlcLog> call() throws Exception {
		return dlcIgniteService.logQuery(keyWord, appName, queryMode);
	}
}
