package com.gzw.kd;

import com.gzw.kd.scheduletask.ScheduleTaskTwo;
import com.gzw.kd.scheduletask.TaskEngine;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
/**
 * @author 高志伟
 */
@EnableDiscoveryClient
@ServletComponentScan
@EnableScheduling
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@MapperScan("com.gzw.kd.mapper")
public class KdApplication {



	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(KdApplication.class);
		app.setBannerMode(Banner.Mode.LOG);
		app.run(args);

		/* SpringBootServletInitializer */


		ScheduleTaskTwo scheduleTaskTwo = new ScheduleTaskTwo();
		TaskEngine.getInstance().schedule(scheduleTaskTwo, 3000, 5000);
	}




}
