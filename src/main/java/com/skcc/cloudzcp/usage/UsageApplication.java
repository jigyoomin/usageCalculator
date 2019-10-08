package com.skcc.cloudzcp.usage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
@EnableAutoConfiguration
public class UsageApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsageApplication.class, args);
    }

}
