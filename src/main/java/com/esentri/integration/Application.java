package com.esentri.integration;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource({ "classpath:spring/camel-context.xml" })
public class Application {

    final Logger logger = LoggerFactory.getLogger(Application.class);

    @Value("${camel.component.servlet.mapping.context-path}")
    private String mapping;

    /**
     * A main method to start this application.
     */
    public static void main(String[] args) {
	      SpringApplication.run(Application.class, args);
    }

    /**
     * This Route definition is not needed when using Camel 2.19 or later. However
     * when using REST in conjunction with the Servlet component the CamelServlet
     * will not be registered automatically. So this class is intended to to this
     * job.
     */
    @Bean
    public ServletRegistrationBean camelServlet() {
      	logger.info("Register CamelServlet with context path {}.", mapping);

      	ServletRegistrationBean servletBean = new ServletRegistrationBean();
      	servletBean.setName("CamelServlet");
      	servletBean.setLoadOnStartup(1);
      	servletBean.setServlet(new CamelHttpTransportServlet());
      	servletBean.addUrlMappings(mapping);
      	return servletBean;
    }

}
