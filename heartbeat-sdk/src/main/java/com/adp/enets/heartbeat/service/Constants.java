package com.adp.enets.heartbeat.service;


import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class Constants {

    public static final String IDENTITY_KEY = "identityKey";
    public static String HOSTNAME;
    static {
        String host;
        try {
            host = (InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            String[] process = ManagementFactory.getRuntimeMXBean().getName().split("@");
            if (process.length == 2) {
                host = process[1];
            } else {
                host = "Unknown";
            }
        }
        HOSTNAME = host;
    }


}
