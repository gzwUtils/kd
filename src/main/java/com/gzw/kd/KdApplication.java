package com.gzw.kd;

import com.gzw.kd.scheduletask.ScheduleTaskTwo;
import com.gzw.kd.scheduletask.TaskEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.util.StopWatch;

/**
 * @author 高志伟
 */
@EnableRetry
@EnableDiscoveryClient
@ServletComponentScan
@EnableScheduling
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("com.gzw.kd.mapper")
@Slf4j
public class KdApplication {



	public static void main(String[] args) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		SpringApplication app = new SpringApplication(KdApplication.class);
		app.setBannerMode(Banner.Mode.LOG);
		ConfigurableApplicationContext context = app.run(args);
		stopWatch.stop();
		ServerProperties serverProperties = context.getBean(ServerProperties.class);
		Integer port = serverProperties.getPort();
		ServerProperties.Servlet servlet = serverProperties.getServlet();
		String contextPath = servlet.getContextPath();
		String urlSuffix = StringUtils.isBlank(contextPath)? String.valueOf(port):port+contextPath;
		log.info("kd 服务启动完成，耗时:{}s  登陆页面请访问: http://127.0.0.1:{} ", stopWatch.getTotalTimeSeconds(), urlSuffix);


		/* SpringBootServletInitializer */


		ScheduleTaskTwo scheduleTaskTwo = new ScheduleTaskTwo();
		TaskEngine.getInstance().schedule(scheduleTaskTwo, 3000, 5000);
	}




}
