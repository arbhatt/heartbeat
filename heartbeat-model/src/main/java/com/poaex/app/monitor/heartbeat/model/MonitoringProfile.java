package com.poaex.app.monitor.heartbeat.model;

import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import lombok.Data;

import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Data
public class MonitoringProfile implements Serializable {

    enum MonitoringStatus {MONITORING, DOWN}

    private UUID monitoringProfileId;
    private String pid;
    private String processSignature;
    private String hostname;
    private String environment;
    private MonitoringStatus status;
    private String threshold;
    private Duration frequency;
    private Map<String, String> customParams;


    public void startMonitoring() {
        status = MonitoringStatus.MONITORING;
    }
    public void stopMonitoring() {
        status = MonitoringStatus.DOWN;
    }

    public Heartbeat beat() {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setLastheartbeatTime(Instant.now().toEpochMilli());
        heartbeat.setMonitoringProfileId(monitoringProfileId);
        return heartbeat;
    }

}
