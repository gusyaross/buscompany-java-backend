package net.buscompany;

import javax.servlet.http.Cookie;

import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpHeaders;

public class TestUtils {

    public final static String URL_CLIENT = "/api/clients";
    public final static String URL_ADMIN = "/api/admins";
    public final static String URL_DEBUG_CLEAR = "/api/debug/clear";
    public final static String URL_ACCOUNT = "/api/accounts";
    public final static String URL_SESSION = "/api/sessions";
    public final static String URL_BUS = "/api/buses";
    public final static String URL_ORDER = "/api/orders";
    public final static String URL_PLACE = "/api/places";
    public final static String URL_TRIP = "/api/trips";

    public static Cookie getCookieFromHeader(HttpHeaders headers) {
        String setCookie = headers.getFirst(HttpHeaders.SET_COOKIE);

        Assertions.assertNotNull(setCookie);

        String cookieName = setCookie.split("=")[0];
        String cookieValue = setCookie.split("=")[1].split(";")[0];

        return new Cookie(cookieName, cookieValue);
    }

    public static HttpHeaders getHttpHeadersWithCookie(Cookie cookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie",cookie.getName() + "=" + cookie.getValue());
        return headers;
    }
}
