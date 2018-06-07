package com.poaex.app.monitor.heartbeat.svc.config;

import com.poaex.app.monitor.heartbeat.sdk.HeartbeatClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Slf4j
@Component
public class MonitoringConfig {

    @PostConstruct
    public void monitoringProfileId() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HeartbeatClient.initialize();
                HeartbeatClient.scheduleMonitoring();

            }
        }.start();
    }

}
