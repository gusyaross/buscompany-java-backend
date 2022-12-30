package net.buscompany;

import net.buscompany.dto.request.register.RegisterAdminDtoRequest;
import net.buscompany.dto.response.debug.ClearDatabaseDtoResponse;
import net.buscompany.dto.request.login.LoginUserDtoRequest;
import net.buscompany.dto.response.error.ErrorDtoResponse;
import net.buscompany.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void clearDatabase() {
        restTemplate.postForEntity(TestUtils.URL_DEBUG_CLEAR, null, ClearDatabaseDtoResponse.class);
    }

    @Test
    public void wrongPasswordLogin() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login1","password1",
                "position1","Никита","Гусев","Алексеевич");

        HttpEntity<RegisterAdminDtoRequest> requestEntity = new HttpEntity<>(adminDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, requestEntity, String.class);

        Cookie cookie = TestUtils.getCookieFromHeader(response.getHeaders());

        restTemplate.exchange(TestUtils.URL_SESSION,HttpMethod.DELETE,new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookie)), String.class);

        LoginUserDtoRequest request = new LoginUserDtoRequest("login1","wrongPassword");

        HttpEntity<ErrorDtoResponse> responseError = restTemplate.exchange(TestUtils.URL_SESSION,HttpMethod.POST,new HttpEntity<>(request), ErrorDtoResponse.class);

        Assertions.assertNotNull(responseError.getBody());
        Assertions.assertEquals(responseError.getBody().getErrors().get(0).getMessage(),ErrorCode.INVALID_LOGIN_OR_PASSWORD.getMessage());
    }

    @Test
    public void correctUnregister() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login2", "password2",
                "position2", "Виталий", "Гусев", "Алексеевич");

        RegisterAdminDtoRequest adminDtoRequest2 = new RegisterAdminDtoRequest("login3", "password3",
                "position3", "Петр", "Гусев", "Алексеевич");

        HttpEntity<RegisterAdminDtoRequest> requestEntity1 = new HttpEntity<>(adminDtoRequest);
        HttpEntity<RegisterAdminDtoRequest> requestEntity2 = new HttpEntity<>(adminDtoRequest2);
        ResponseEntity<String> response = restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, requestEntity1, String.class);
        restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, requestEntity2, String.class);

        Cookie cookie = TestUtils.getCookieFromHeader(response.getHeaders());

        restTemplate.exchange(TestUtils.URL_ACCOUNT, HttpMethod.DELETE, new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookie)), String.class);

        HttpEntity<ErrorDtoResponse> responseError = restTemplate.exchange(TestUtils.URL_ACCOUNT, HttpMethod.DELETE,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookie)), ErrorDtoResponse.class);

        Assertions.assertNotNull(responseError.getBody());
        Assertions.assertEquals(responseError.getBody().getErrors().get(0).getMessage(), ErrorCode.USER_NOT_EXISTS.getMessage());
    }
}
