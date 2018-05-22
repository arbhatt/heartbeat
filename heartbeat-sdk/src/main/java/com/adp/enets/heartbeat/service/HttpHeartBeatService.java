package com.adp.enets.heartbeat.service;


import com.google.gson.Gson;
import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import com.poaex.app.monitor.heartbeat.exception.client.ClientRegistrationException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;


/**
 * Heartbeat service implementation for those components that need out of the box feature
 */
@Slf4j
public class HttpHeartBeatService {


    private String heartbeatUrl;

    private Runnable heartbeatPostThread;

    private HbServiceHelper hbServiceHelper = new HbServiceHelper();

    private EncryptionService encryptionService;


    /**
     * Initialize monitoring
     */

    public void initialize() throws ClientException {

        try {
            HeartbeatProperties.initializeProperties();
            heartbeatUrl = HeartbeatProperties.getProperty("heartbeat.url");
//            encryptionService.initCipher(systemProperties.getProperty("token"));
//            registerStartup();
        } catch (RuntimeException e) {
            //If the process is HB Monitor, then monitoring is not mandatory
            //Bootstrap cycle breaker
            if (!"true".equalsIgnoreCase(System.getProperty("adminsvc"))) {
                log.error("Fail to start since monitoring is unavailable", e);
                throw new ClientRegistrationException(e);
            }
        }
    }

//    private void registerStartup() {
//
//        String token = systemProperties.getProperty("token");
//        String secretKey = systemProperties.getProperty("secretKey");
//        Heartbeat hb = HeartbeatBuilder.build()
//                .withToken(token)
//                .withSecretKey(secretKey);
//
//        startMonitoring(hb);
//    }

    public void startMonitoring(Heartbeat hb) {

        String startHb = new Gson().toJson(hb.start()).toString();
        String response = hbServiceHelper.postApi(heartbeatUrl + "/start", startHb);
        log.info("Heart Beat Start Operation" + response);
    }

    public void stopMonitoring(Heartbeat hb) {

        String stopHb = new Gson().toJson(hb.stop()).toString();
        String response = hbServiceHelper.postApi(heartbeatUrl + "/stop", stopHb);
        log.info("Heart Beat Start Operation" + response);
    }

    /**
     * Post Heartbeat periodically
     */
    public void postHeartbeat(Heartbeat hb) {
        String beatHb = new Gson().toJson(hb.beat()).toString();
        String response = hbServiceHelper.postApi(heartbeatUrl + "/stop", beatHb);

    }
}
