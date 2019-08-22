package com.lin.seckill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SeckillApplication {
    private static final Logger logger = LoggerFactory.getLogger(SeckillApplication.class);

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(SeckillApplication.class);
        application.setBannerMode(Banner.Mode.OFF);
        application.run(args);
        logger.info("seckill app start");
    }

}
