package com.poaex.app.monitor.heartbeat.model;

import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import lombok.Data;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Domain object for heartbeat
 */
@Data
public class Heartbeat implements Serializable {

    private static String DEFAULT_PID;
    private static String DEFAULT_HOSTNAME;
    private static final String DEFAULT_CLASSPATH = ManagementFactory.getRuntimeMXBean().getClassPath();
    private static final String DEFAULT_ENVIRONMENT = Optional.ofNullable(System.getenv("environment")).orElse("Unknown");

    private static void populateStaticFields() throws ClientException {
        try {
            String[] processParams = ManagementFactory.getRuntimeMXBean().getName().split("@");
            if (processParams.length > 0) {
                DEFAULT_PID = processParams[0];
            }
            if (processParams.length > 1) {
                DEFAULT_HOSTNAME = processParams[1];
            }
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    private String pid;
    private String processSignature;
    private String hostname;
    private String environment;
    private String status;

    private Long startTime;
    private Long downTime;
    private Long lastheartbeatTime;

//    private String token;
//    private String secretKey;
//    private String instanceId;

    private Map<String, String> customParams;

    public Heartbeat withCustomParams(Map<String, String> customParams) {
        this.setCustomParams(customParams);
        return this;
    }

//    public Heartbeat withSecretKey(String key) {
//        this.setSecretKey(key);
//        return this;
//    }
//
//    public Heartbeat createInstanceId(String id) {
//        this.setInstanceId(id);
//        return this;
//    }
//
//    public Heartbeat withToken(String token) {
//        this.setToken(token);
//        return thi
// s;
//    }

    public Heartbeat up() {
        this.setStatus("UP");
        return this;
    }

    public Heartbeat down() {
        this.setStatus("DOWN");
        return this;
    }

    public Heartbeat beat() {
        this.setLastheartbeatTime(Instant.now().toEpochMilli());
        return this;
    }

    public Heartbeat start() {
        this.setStartTime(Instant.now().toEpochMilli());
        return this;
    }
    public Heartbeat stop() {
        this.setDownTime(Instant.now().toEpochMilli());
        return this;
    }
}
