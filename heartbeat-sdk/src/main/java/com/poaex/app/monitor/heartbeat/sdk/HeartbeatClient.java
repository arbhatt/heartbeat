package com.poaex.app.monitor.heartbeat.sdk;

import com.google.gson.Gson;
import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import com.poaex.app.monitor.heartbeat.model.MonitoringProfile;
import com.poaex.app.monitor.heartbeat.model.MonitoringProfileBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
public class HeartbeatClient {
    private static final String propertiesFileName = "heartbeat.properties";
    private static Properties heartbeatClientProperties;
    private static String heartbeatUrl;
    private static UUID monitoringProfileId;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    private static MonitoringProfile monitoringProfile;


    public static void initialize() {
        heartbeatClientProperties = initializeProperties();
        heartbeatUrl = heartbeatClientProperties.getProperty("heartbeat.url");
        monitoringProfileId = initializeMonitoringProfileId();
    }

    public static Properties initializeProperties() {

        Properties heartbeatProperties = new Properties();
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFileName)) {
            if (is != null) {
                heartbeatProperties.load(is);
                log.debug("ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFileName))" + String.valueOf(heartbeatProperties == null));
                return heartbeatProperties;
            }
        } catch (IOException e) {
            log.warn("Unable to load {}. Attempting again", propertiesFileName, e);
        }


        try (InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFileName)) {
            if (is != null) {
                heartbeatProperties.load(is);
                log.debug("ClassLoader.getSystemResourceAsStream(propertiesFileName)" + String.valueOf(heartbeatProperties == null));
                return heartbeatProperties;
            }
        } catch (IOException e) {
            log.warn("Unable to load {}. Attempting again", propertiesFileName, e);
        }


//        try (InputStream is = new DefaultResourceLoader().getResource("classpath:" + propertiesFileName).getInputStream()) {
//            if (is != null) {
//                heartbeatProperties.load(is);
//                log.debug("new DefaultResourceLoader().getResource(\"classpath:\" + propertiesFileName);" + String.valueOf(heartbeatProperties == null));
//                return heartbeatProperties;
//            }
//        } catch (IOException e) {
//            log.warn("Unable to load {}. Monitoring will be unavailable", propertiesFileName, e);
//            throw new RuntimeException("Monitoring will be unavailable", e);
//        }

        return heartbeatProperties;
    }

    public static UUID initializeMonitoringProfileId() {

        UUID monitoringProfileId = UUID.randomUUID();
        try {
            monitoringProfile = MonitoringProfileBuilder.instance()
                    .scanForCurrentProcess()
                    .withEnvironment(heartbeatClientProperties.getProperty("environment"))
                    .withProfileId(monitoringProfileId)
                    .startMonitoring()
                    .build();
            log.info("Instantiated monitoring profile " + monitoringProfileId);
            new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5 * DateUtils.MILLIS_PER_SECOND);
                        HbServiceHelper.postApi(heartbeatUrl + "/start", new Gson().toJson(monitoringProfile).toString());
                    } catch (InterruptedException e) {
                        log.error("Unable to register. Monitoring won't continue");
                    }
                }
            }.start();


        } catch (ClientException e) {
            log.error("Unable to register. Monitoring won't continue");
        }

        return monitoringProfileId;
    }

    public static Heartbeat createHeartbeat() {

        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setMonitoringProfileId(monitoringProfileId);
        return heartbeat.beat();
    }

    public static void postHeartbeat() throws RuntimeException {
        String url = heartbeatUrl + "/beat";
        Heartbeat hb = createHeartbeat();
        HbServiceHelper.postApi(url, new Gson().toJson(hb).toString());
    }

    public static void scheduleMonitoring() {

        scheduler.scheduleAtFixedRate(() -> postHeartbeat(),
                5 * DateUtils.MILLIS_PER_SECOND + (long) (DateUtils.MILLIS_PER_MINUTE * Math.random()), // Start checking in 10 seconds
                30 * DateUtils.MILLIS_PER_SECOND + (long) (DateUtils.MILLIS_PER_MINUTE * Math.random()), //Randomly in another 2 - 3 mins
                TimeUnit.MILLISECONDS);
    }


    public static void stopMonitoring() {
        scheduler.shutdown();
        HbServiceHelper.postApi(heartbeatUrl + "/stop", new Gson().toJson(monitoringProfile).toString());
    }


}