/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月5日 上午10:13:54
 *
 * @Package com.happygo.dlc.api  
 * @Title: DlcLogQueryController.java
 * @Description: DlcLogQueryController.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happygo.dlc.api.controller;

import com.happgo.dlc.base.DlcConstants;
import com.happgo.dlc.base.bean.DlcLog;
import com.happgo.dlc.base.bean.PageParam;
import com.happygo.dlc.biz.service.DlcLogQueryService;
import com.happygo.dlc.biz.service.LogSourceService;
import com.happygo.dlc.common.entity.DlcLogResult;
import com.happygo.dlc.common.entity.LogSource;
import com.happygo.dlc.common.entity.helper.DlcLogResultHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * ClassName:DlcLogQueryController
 * 
 * @Description: DlcLogQueryController.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月5日 上午10:13:54
 */
@RestController
@RequestMapping("/dlc")
public class DlcLogQueryController {
	
	/**
	 * Logger the LOGGER 
	 */
	private static final Logger LOGGER = LogManager.getLogger();
	
	/** 
	* The field dlcLogQueryService
	*/
	@Autowired
	private transient DlcLogQueryService dlcLogQueryService;

	/**
	 * LogSourceService the logSourceService 
	 */
	@Autowired
	private transient LogSourceService logSourceService;
	
	/**
	 * @MethodName: logQuery
	 * @Description: the method logQuery
	 * @param keyWord
	 * @return String
	 */
	@GetMapping(value = "/log/query")
	public ModelAndView logQuery(
			@RequestParam(value = "keyWord") String keyWord, PageParam pageParam) {
		LOGGER.info("^------- DLC 日志查询开始，keyWord:[" + keyWord + "] -------^");
		long startTime = System.currentTimeMillis();
		LogSource defaultLogSource = logSourceService.selectDefault(DlcConstants.DEFAULT);
		ModelAndView modelAndView = new ModelAndView("search_results");
		if (defaultLogSource == null) {
			modelAndView.addObject("errorMsg", "日志源未设置，至左侧菜单栏日志源设置");
			DlcLogResult dlcLogResult = DlcLogResultHelper.buildDlcLogResult(
					keyWord, 0, null, pageParam);
			modelAndView.addObject("dlcLogResult", dlcLogResult);
            return modelAndView;
		}
		String appName = defaultLogSource.getAppName();
		List<List<DlcLog>> queryDlcLogs = dlcLogQueryService.logQuery(keyWord.trim(), appName, pageParam);
		long endTime = System.currentTimeMillis();
		long searchTime = endTime - startTime;
		DlcLogResult dlcLogResult = DlcLogResultHelper.buildDlcLogResult(
				keyWord, searchTime, queryDlcLogs, pageParam);
		modelAndView.addObject("dlcLogResult", dlcLogResult);
		LOGGER.info("^------- DLC 日志查询结束  -------^");
		return modelAndView;
	}
	
	/**
	* @MethodName: logDetail
	* @Description: the logDetail
	* @param logDetail
	* @return ModelAndView
	*/
	@GetMapping(value = "/log/detail")
	public ModelAndView logDetail(@RequestParam("logDetail")String logDetail) {
		ModelAndView modelAndView = new ModelAndView("search_results_detail");
		modelAndView.addObject("logDetail", logDetail);
		return modelAndView;
	}
}