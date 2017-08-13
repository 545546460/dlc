/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年8月13日 上午11:41:08
*
* @Package com.happygo.dlc.ignite.service  
* @Title: DlcQueryConditionServiceImpl.java
* @Description: DlcQueryConditionServiceImpl.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.happygo.dlc.ignite.service;

import java.util.List;

import org.apache.ignite.services.Service;
import org.apache.ignite.services.ServiceContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.happgo.dlc.base.ignite.service.DlcQueryConditionService;
import com.happygo.dlc.logging.LuceneAppender;

/**
 * ClassName:DlcQueryConditionServiceImpl
 * @Description: DlcQueryConditionServiceImpl.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年8月13日 上午11:41:08
 */
public class DlcQueryConditionServiceImpl implements DlcQueryConditionService ,Service {

	/**
	 * long the serialVersionUID 
	 */
	private static final long serialVersionUID = 4057978072875039088L;
	
	/**
	 * List<String> the queryConditions 
	 */
	private static final List<String> queryConditions = LuceneAppender.indexFieldNameList;
	
	/** 
	* The field LOGEER
	*/
	private static final Logger LOGEER = LogManager.getLogger(DlcQueryConditionServiceImpl.class);

	/* (non-Javadoc)
	 * @see com.happgo.dlc.base.ignite.service.DlcQueryConditionService#getQueryConditions()
	 */
	@Override
	public List<String> getQueryConditions() {
		return queryConditions;
	}

	/**
	* cancel
	* @param ctx
	* @see org.apache.ignite.services.Service#cancel(org.apache.ignite.services.ServiceContext)
	*/
	public void cancel(ServiceContext ctx) {
		LOGEER.info("Service was cancel: " + ctx.name());
	}

	/**
	* init
	* @param ctx
	* @throws Exception
	* @see org.apache.ignite.services.Service#init(org.apache.ignite.services.ServiceContext)
	*/
	public void init(ServiceContext ctx) throws Exception {
		LOGEER.info("Service was initialized: " + ctx.name());
	}

	/**
	* execute
	* @param ctx
	* @throws Exception
	* @see org.apache.ignite.services.Service#execute(org.apache.ignite.services.ServiceContext)
	*/
	public void execute(ServiceContext ctx) throws Exception {
		LOGEER.info("Executing distributed service: " + ctx.name());
	}
}
