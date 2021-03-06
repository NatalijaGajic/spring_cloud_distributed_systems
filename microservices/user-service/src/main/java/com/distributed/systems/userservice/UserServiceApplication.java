package com.distributed.systems.userservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@ComponentScan("com.distributed.systems")
public class UserServiceApplication {

	private static final Logger LOG = LoggerFactory.getLogger(UserServiceApplication.class);

	public static void main(String[] args) {

		ConfigurableApplicationContext ctx = SpringApplication.run(UserServiceApplication.class, args);
		String mongodDbHost = ctx.getEnvironment().getProperty("spring.data.mongodb.host");
		String mongodDbPort = ctx.getEnvironment().getProperty("spring.data.mongodb.port");
		LOG.info("Connected to MongoDb: " + mongodDbHost + ":" + mongodDbPort);
	}

}
