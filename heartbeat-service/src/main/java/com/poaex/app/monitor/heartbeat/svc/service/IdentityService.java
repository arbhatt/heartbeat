package com.poaex.app.monitor.heartbeat.svc.service;


import com.poaex.app.monitor.heartbeat.exception.service.ServiceSecurityException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;
import com.poaex.app.monitor.heartbeat.model.MonitoringProfile;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


@Service
public class IdentityService {

    @Setter
    @Value("${auth.identityKey}")
    private String salt;

    public String encrypt(String original) throws ServiceSecurityException {
        byte[] salt = getSalt();
        try {
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(salt);
            byte[] encyptedBytes = sha256.digest(original.getBytes());
            return convertBytesToString(encyptedBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new ServiceSecurityException(e);
        }
    }


    public String createInstanceId(MonitoringProfile monitoringProfile) throws ServiceSecurityException {
        final String identityString = String.format("{%s}-{%s}-{%s}-{%s}",
                monitoringProfile.getPid(),
                monitoringProfile.getProcessSignature(),
                monitoringProfile.getHostname(),
                monitoringProfile.getEnvironment());
        return String.valueOf(encrypt(identityString));

    }

    private String convertBytesToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for(byte encryptedByte : bytes) {
            sb.append(Integer.toString((encryptedByte & 0xff) + 0x100, 16).substring(1));
        }
        String encryptedHash = sb.toString();
        return encryptedHash;
    }


    private byte[] getSalt() {
        return salt.getBytes();
    }
}

