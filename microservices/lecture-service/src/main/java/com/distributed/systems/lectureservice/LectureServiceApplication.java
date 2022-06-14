package com.distributed.systems.lectureservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.distributed.systems")
public class LectureServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LectureServiceApplication.class, args);
	}

}
