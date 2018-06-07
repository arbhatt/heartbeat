package com.poaex.app.monitor.heartbeat.model;

import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import com.sun.javafx.text.PrismTextLayout;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static lombok.AccessLevel.PACKAGE;


public class MonitoringProfileBuilder {
    private static String DEFAULT_PID;
    private static String DEFAULT_HOSTNAME;
    private static final String DEFAULT_CLASSPATH = ManagementFactory.getRuntimeMXBean().getClassPath();
    private static final String DEFAULT_ENVIRONMENT = Optional.ofNullable(System.getenv("environment")).orElse("Unknown");

    private MonitoringProfile monitoringProfile = new MonitoringProfile();

    private MonitoringProfileBuilder() {}

    public MonitoringProfileBuilder scanForCurrentProcess() throws ClientException {
        populateStaticFields();
        monitoringProfile.setHostname(DEFAULT_HOSTNAME);
        monitoringProfile.setPid(DEFAULT_PID);
        monitoringProfile.setProcessSignature("DEFAULT SCAN PROCESS");
        return this;
    }

    public MonitoringProfileBuilder withEnvironment(String environment) {
        monitoringProfile.setEnvironment(environment);
        return this;
    }

    public MonitoringProfileBuilder withHeartbeatFrequency(Duration duration) {
        monitoringProfile.setFrequency(duration);
        return this;
    }

    public MonitoringProfileBuilder withProfileId(UUID profileId) {
        monitoringProfile.setMonitoringProfileId(profileId);
        return this;
    }

    public MonitoringProfileBuilder startMonitoring() {
        monitoringProfile.setStatus(MonitoringProfile.MonitoringStatus.MONITORING);
        return this;
    }

    public MonitoringProfileBuilder stopMonitoring() {
        monitoringProfile.setStatus(MonitoringProfile.MonitoringStatus.DOWN);
        return this;
    }

    public MonitoringProfile build() {
        if (monitoringProfile.getMonitoringProfileId() == null) {
            monitoringProfile.setMonitoringProfileId(UUID.randomUUID());
        }
        return monitoringProfile;
    }

    public MonitoringProfileBuilder withCustomParams(Map<String, String> customParams) {
        monitoringProfile.setCustomParams(customParams);
        return this;
    }

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

    public static MonitoringProfileBuilder instance() {
        return new MonitoringProfileBuilder();
    }
}
