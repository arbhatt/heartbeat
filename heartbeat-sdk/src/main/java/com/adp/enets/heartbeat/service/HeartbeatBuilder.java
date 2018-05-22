package com.adp.enets.heartbeat.service;

import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Optional;

public class HeartbeatBuilder {

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

    public static Heartbeat build() {
        Heartbeat pr = new Heartbeat();
        pr.setPid(DEFAULT_PID);
        pr.setHostname(DEFAULT_HOSTNAME);
        String processName = DEFAULT_CLASSPATH;
        int length = processName.length();
        if (length > 50) {
            pr.setProcessSignature(processName.substring(0, 25) + processName.substring(length - 25, length));
        } else {
            pr.setProcessSignature(processName);
        }

        pr.setEnvironment(DEFAULT_ENVIRONMENT);
        return pr.start().beat();
    }
}
