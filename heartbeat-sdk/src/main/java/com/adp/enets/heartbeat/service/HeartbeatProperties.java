package com.adp.enets.heartbeat.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class HeartbeatProperties {
    private static Properties heartbeatProperties;
    private static final String propertiesFileName = "heartbeat.properties";

    public static void initializeProperties() {

        heartbeatProperties = new Properties();
        try (InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFileName)) {
            if (is != null) {
                heartbeatProperties.load(is);
                log.debug("ClassLoader.getSystemClassLoader().getResourceAsStream(propertiesFileName))" + String.valueOf(heartbeatProperties == null));
                return;
            }
        } catch (IOException e) {
            log.warn("Unable to load {}. Attempting again", propertiesFileName, e);
        }


        try (InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFileName)) {
            if (is != null) {
                heartbeatProperties.load(is);
                log.debug("ClassLoader.getSystemResourceAsStream(propertiesFileName)" + String.valueOf(heartbeatProperties == null));
                return;
            }
        } catch (IOException e) {
            log.warn("Unable to load {}. Attempting again", propertiesFileName, e);
        }


        try (InputStream is = new DefaultResourceLoader().getResource("classpath:" + propertiesFileName).getInputStream()) {
            if (is != null) {
                heartbeatProperties.load(is);
                log.debug("new DefaultResourceLoader().getResource(\"classpath:\" + propertiesFileName);" + String.valueOf(heartbeatProperties == null));
                return;
            }
        } catch (IOException e) {
            log.warn("Unable to load {}. Monitoring will be unavailable", propertiesFileName, e);
            throw new RuntimeException("Monitoring will be unavailable", e);
        }
    }

    public static String getProperty(String key) {
        return heartbeatProperties.getProperty(key);
    }

}
