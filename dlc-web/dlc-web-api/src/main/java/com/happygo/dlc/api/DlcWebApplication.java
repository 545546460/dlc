/**
 * Copyright  2017
 * <p>
 * All  right  reserved.
 * <p>
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

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.http.HttpStatus;

import com.happygo.dlc.api.config.DefaultView;

/**
 * ClassName:DlcWebApplication
 *
 * @author sxp (1378127237@qq.com)
 * @Description: DlcWebApplication.java
 * @date:2017年6月5日 上午10 :31:22
 */
@SpringBootApplication
@ComponentScans(value = {@ComponentScan(value = "com.happygo.dlc")})
public class DlcWebApplication extends SpringBootServletInitializer {

    /**
     * Start ignite node ignite.
     *
     * @return Ignite ignite
     * @MethodName: startIgniteNode
     * @Description: the method startIgniteNode
     */
    @Bean
    public Ignite startIgniteNode() {
        return Ignition.start("config/dlc-ignite.xml");
    }

    /**
     * Default view default view.
     *
     * @return DefaultViewCacheConfiguration default view
     * @MethodName: defaultView
     * @Description: the method defaultView
     */
    @Bean
    public DefaultView defaultView() {
        return new DefaultView();
    }

    /**
     * Servlet container embedded servlet container factory.
     *
     * @return EmbeddedServletContainerFactory embedded servlet container factory
     * @MethodName: servletContainer
     * @Description: the method servletContainer
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
     * Druid servlet servlet registration bean.
     *
     * @return the servlet registration bean
     */
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean reg = new ServletRegistrationBean();
        reg.setServlet(new StatViewServlet());
        reg.addUrlMappings("/druid/*");
        reg.addInitParameter("loginUsername", "admin");
        reg.addInitParameter("loginPassword", "admin");
        return reg;
    }

    /**
     * Filter registration bean filter registration bean.
     *
     * @return the filter registration bean
     */
    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new WebStatFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        filterRegistrationBean.addInitParameter("profileEnable", "true");
        filterRegistrationBean.addInitParameter("principalCookieName", "USER_COOKIE");
        filterRegistrationBean.addInitParameter("principalSessionName", "USER_SESSION");
        return filterRegistrationBean;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @MethodName: main
     * @Description: the method main
     */
    public static void main(String[] args) {
        SpringApplication.run(DlcWebApplication.class, args);
    }
}
