package com.adp.enets.heartbeat.service;

import com.google.gson.Gson;
import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScheduledHeartbeatService {

    @Setter
    private HttpHeartBeatService httpHeartBeatService;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);

    public void initialize() throws ClientException {
        httpHeartBeatService.initialize();
    }

    public void postHeartbeat(Heartbeat hb) throws ClientException {
        String heartbeatPostUrl = HeartbeatProperties.getProperty("heartbeat.url") + "/beat";
        Runnable heartbeatThread = new Runnable() {
            @Override
            public void run() {
                try {
                    httpHeartBeatService.postHeartbeat(hb.beat());
                } catch (Exception e) {
                    throw new RuntimeException(new ClientException(e));
                }
            }
        };

        //Sampling to ensure all the callers do not swamp at the same time

        scheduler.scheduleAtFixedRate(heartbeatThread,
                5 * DateUtils.MILLIS_PER_SECOND + (long) (DateUtils.MILLIS_PER_MINUTE * Math.random()), // Start checking in 10 seconds
                30 * DateUtils.MILLIS_PER_SECOND + (long) (DateUtils.MILLIS_PER_MINUTE * Math.random()), //Randomly in another 2 - 3 mins
                TimeUnit.MILLISECONDS);
    }

    // @PreDestroy
    public void shutdown(Heartbeat hb) {
        log.info("Shutting down monitoring service");
        scheduler.shutdown();
        httpHeartBeatService.stopMonitoring(hb);
        log.info("Shutting down heartbeat");
    }


}
