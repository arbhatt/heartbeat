package com.poaex.app.monitor.heartbeat.service.service;

import com.poaex.app.monitor.heartbeat.exception.service.ServiceSecurityException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class IdentityServiceTest {

    IdentityService identityService = new IdentityService();

    @Before
    public void initSalt() {
        identityService.setSalt("12345");
    }

    @Test
    public void testEncrypt() throws ServiceSecurityException {
        String firstAttempt = identityService.encrypt("password");
        String secondAttempt = identityService.encrypt("password");
        assertEquals(firstAttempt, secondAttempt);
    }

}