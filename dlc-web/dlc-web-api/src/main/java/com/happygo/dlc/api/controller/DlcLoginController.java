/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月6日 上午9:33:46
 *
 * @Package com.happygo.dlc.api.controller  
 * @Title: DlcLoginController.java
 * @Description: DlcLoginController.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happygo.dlc.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.happgo.dlc.base.util.Strings;

/**
 * ClassName:DlcLoginController
 * 
 * @Description: DlcLoginController.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月6日 上午9:33:46
 */
@RestController
@RequestMapping("/dlc")
public class DlcLoginController {

	/**
	 * @MethodName: dlcLogin
	 * @Description: the method dlcLogin
	 * @param email
	 * @param password
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/login", produces = { "application/json" })
	public ModelAndView dlcLogin(@RequestParam(value = "account", required = false) String email,
			@RequestParam(value = "pwd", required = false) String password) {
		if (Strings.isNotEmpty(email) && Strings.isNotEmpty(password)) {
			return new ModelAndView("redirect:/dlc/index");
		}
		return new ModelAndView("login");
	}
}
