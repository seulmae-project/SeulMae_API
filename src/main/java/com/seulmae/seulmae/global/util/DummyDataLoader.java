package com.seulmae.seulmae.global.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seulmae.seulmae.user.dto.request.UserSignUpDto;
import com.seulmae.seulmae.workplace.dto.WorkplaceAddDto;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DummyDataLoader implements CommandLineRunner {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        createUsers(10);
    }

    private void createUsers(int count) {
        String url = "http://localhost:8080/api/users";

        for (int i = 1; i < count; i++) {
            UserSignUpDto userSignUpDto = new UserSignUpDto(
                    "test" + i + i + i + i,
                    "password" + i + i + i + i + "!",
                    "010" + String.format("%04d", random.nextInt(10000)) + String.format("%04d", random.nextInt(10000)),
                    "testUser" + i,
                    random.nextBoolean(),
                    String.format("%08d", 19900101 + random.nextInt(300000))
            );

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("userSignUpDto", userSignUpDto);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            restTemplate.postForEntity(url, requestEntity, String.class);

            // 로그인 및 근무지 생성
            if (i < 4) {
                String token = signIn(userSignUpDto.getAccountId(), userSignUpDto.getPassword());
                createWorkplaces(i, token);
            }
        }
    }

    private String signIn(String accountId, String password) {
        String loginUrl = "http://localhost:8080/api/users/login";

        Map<String, String> body = new HashMap<>();
        body.put("accountId", accountId);
        body.put("password", password);
        body.put("fcmToken", "fVkUc4d3XEaqtM7hvPb6-h:APA91bFPzgbzyMlYrQd_PANAit-5RwUozz7n5HgjL6rCuh7lmAxWkxLcbZqprh4aAgFx8-vUaPOBzFfP6Ujv9MJS2krUWre6vSIldBtMNClJuTF7TpHmKRtHWDdMEo8PgXYtbIQYYS9D");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(loginUrl, requestEntity, String.class);

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            return root.path("data").path("tokenResponse").path("accessToken").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse login response", e);
        }
    }

    private void createWorkplaces(int num, String token) {
        String url = "http://localhost:8080/api/workplace/v1/add";

        WorkplaceAddDto workplaceAddDto = new WorkplaceAddDto(
                "근무지" + num,
                "경기도",
                "안양시",
                "010" + String.format("%04d", random.nextInt(10000)) + String.format("%04d", random.nextInt(10000))
        );

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("workplaceAddDto", workplaceAddDto);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(token);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, requestEntity, String.class);
    }
}