/**
* Copyright  2017
* 
* All  right  reserved.
*
* Created  on  2017年6月6日 下午8:33:35
*
* @Package com.happygo.dlc.api.controller  
* @Title: HtmlHandleController.java
* @Description: HtmlHandleController.java
* @author sxp (1378127237@qq.com) 
* @version 1.0.0 
*/
package com.happygo.dlc.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * ClassName:HtmlHandleController
 * @Description: HtmlHandleController.java
 * @author sxp (1378127237@qq.com) 
 * @date:2017年6月6日 下午8:33:36
 */
@Controller
@RequestMapping("/dlc")
public class HtmlHandleController {
	
	/**
	* @MethodName: index
	* @Description: the index
	* @return
	* @return ModelAndView
	*/
	@RequestMapping(value = "/index")
	public ModelAndView index() {
		return new ModelAndView("index");
	}
	
	/**
	* @MethodName: index_v1
	* @Description: the index_v1
	* @return ModelAndView
	*/
	@RequestMapping(value = "/index_v1")
	public ModelAndView index_v1() {
		return new ModelAndView("pin_board");
	}
}
