package com.sunpower.scale.wmssimulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WmsSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(WmsSimulatorApplication.class, args);
	}

}
