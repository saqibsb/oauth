package com.app.oauth.controller;

import com.app.oauth.dto.request.LoginRequestDto;
import com.app.oauth.dto.request.RegisterRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testRegister_Success() {
        String username = "newuser_" + System.nanoTime();

        RegisterRequestDto request = new RegisterRequestDto();
        request.setUsername(username);
        request.setEmail(username + "@example.com");
        request.setPassword("password123");

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/register", request, String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"success\":true"));
    }

    @Test
    void testLogin_FailsForUnknownUser() {
        LoginRequestDto request = new LoginRequestDto();
        request.setUsername("unknown_user_" + System.nanoTime());
        request.setPassword("password123");

        ResponseEntity<String> response = restTemplate.postForEntity("/auth/login", request, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"success\":false"));
    }
}
