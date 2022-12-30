package net.buscompany.controller;

import net.buscompany.dto.response.info.GetInfoUserDtoResponse;
import net.buscompany.Cookies;
import net.buscompany.dto.response.unregister.UnregisterUserDtoResponse;
import net.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final UserService userService;

    public AccountController(UserService userService) {
        this.userService = userService;
    }

    @DeleteMapping
    public ResponseEntity<UnregisterUserDtoResponse> unregisterUser(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue) {
        return userService.unregisterUser(cookieValue);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetInfoUserDtoResponse> getInfoUser(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue) {
        return userService.getInfoUser(cookieValue);
    }
}
