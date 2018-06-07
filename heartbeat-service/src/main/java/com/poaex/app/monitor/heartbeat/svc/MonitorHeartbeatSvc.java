package com.poaex.app.monitor.heartbeat.svc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class MonitorHeartbeatSvc {

    public static final String DUMMY = "DUMMY";
    public static final Integer FIRST_PAGE = 0;
    public static final Integer DB_PAGE_SIZE = 100;
    public static final Long ONE_MIN_IN_MILLIS = 60000L;
    public static final Long FIVE_MIN_IN_MILLIS = 300000L;
    public static final Long HB_RESPONSE_THRESHOLD_MILLIS = ONE_MIN_IN_MILLIS;
    public static final Long NOTIFICATION_THROTTLE_LIMIT = FIVE_MIN_IN_MILLIS;

    public static void main(String... args) {
        SpringApplication.run(MonitorHeartbeatSvc.class, args);
    }



}
