/**
 * Copyright  2017
 * 
 * All  right  reserved.
 *
 * Created  on  2017年6月5日 上午10:31:22
 *
 * @Package DlcWebApplication  
 * @Title: DlcWebApplication.java
 * @Description: DlcWebApplication.java
 * @author sxp (1378127237@qq.com) 
 * @version 1.0.0 
 */
package com.happygo.dlc.api;

import java.util.concurrent.TimeUnit;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.http.HttpStatus;

import com.happygo.dlc.api.config.DefaultView;

/**
 * ClassName:DlcWebApplication
 * 
 * @Description: DlcWebApplication.java
 * @author sxp (1378127237@qq.com)
 * @date:2017年6月5日 上午10:31:22
 */
@SpringBootApplication
@ComponentScans(value = {@ComponentScan(value = "com.happygo.dlc.biz")})
public class DlcWebApplication extends SpringBootServletInitializer {
	
	/**
	 * @MethodName: startIgniteNode
	 * @Description: the method startIgniteNode
	 * @return Ignite
	 */
	@Bean
	public Ignite startIgniteNode() {
		return Ignition.start("config/dlc-ignite.xml");
	}
	
	/**
	* @MethodName: defaultView
	* @Description: the method defaultView
	* @return DefaultViewCacheConfiguration
	*/
	@Bean
	public DefaultView defaultView() {
		return new DefaultView();
	}
	
	/**
	* @MethodName: servletContainer
	* @Description: the method servletContainer
	* @return EmbeddedServletContainerFactory
	*/
	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
	    TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
	    factory.setSessionTimeout(10, TimeUnit.MINUTES);
	    factory.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/dlc/error/404"));
	    factory.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/dlc/error/500"));
	    return factory;
	}

	/**
	 * @MethodName: main
	 * @Description: the method main
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(DlcWebApplication.class, args);
	}
}
