package com.adp.enets.heartbeat.service;


import com.poaex.app.monitor.heartbeat.exception.client.ClientException;
import com.poaex.app.monitor.heartbeat.exception.client.ClientRegistrationException;
import com.poaex.app.monitor.heartbeat.model.Heartbeat;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.stream.Collectors;

public class EncryptionService {

    private Cipher cipher;

    public void initCipher(String token) throws ClientException {
        try {
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            final byte[] keyBytes = sha256.digest(token.getBytes());

            SecretKeySpec secretKey = new SecretKeySpec(Arrays.copyOf(keyBytes, 16), "AES");
            cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            throw new ClientException("Unable to intialize Identity encryption. The application will malfunction", e);
        }
    }

    public byte[] createChecksum(Heartbeat hb) throws BadPaddingException, IllegalBlockSizeException {
        final String identityString = String.format("{%s}-{%s}-{%s}-{%s}-{%s}",
                hb.getPid(), hb.getProcessSignature(), hb.getHostname(),
                hb.getEnvironment(), hb.getLastheartbeatTime());
        byte[] encryptedValue = cipher.doFinal(identityString.getBytes());
        byte[] encodedValue = Base64.getEncoder().encode(encryptedValue);

        return encodedValue;
    }

    public byte[] createKey(String... params) throws BadPaddingException, IllegalBlockSizeException {
        final String identityString = Arrays.stream(params).collect(Collectors.joining("::"));
        byte[] encryptedValue = cipher.doFinal(identityString.getBytes());
        byte[] encodedValue = Base64.getEncoder().encode(encryptedValue);

        return encodedValue;
    }
}
