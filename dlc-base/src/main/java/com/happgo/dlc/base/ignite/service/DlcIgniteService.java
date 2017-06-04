/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月1日 下午3:10:14
 *
 * @Package com.happgo.dlc.base.ignite.service  
 * @Title: DlcIgniteService.java
 * @Description: DlcIgniteService.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happgo.dlc.base.ignite.service;

import java.util.List;

import com.happgo.dlc.base.DlcLog;

/**
 * ClassName:DlcIgniteService
 * 
 * @Description: DlcIgniteService.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月1日 下午3:10:14
 */
public interface DlcIgniteService {
	
	/**
	* @MethodName: logQuery
	* @Description: the logQuery
	* @param keyWord
	* @return
	* @return List<DlcLog>
	*/
	List<DlcLog> logQuery(String keyWord);

}
