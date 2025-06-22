package com.timesheet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimeSheetManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(TimeSheetManagementApplication.class, args);
    }
} 