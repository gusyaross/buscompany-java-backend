package net.buscompany.controller;

import net.buscompany.Cookies;
import net.buscompany.dto.request.login.LoginUserDtoRequest;
import net.buscompany.dto.response.login.LoginUserDtoResponse;
import net.buscompany.dto.response.logout.LogoutUserDtoResponse;
import net.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final UserService userService;

    public SessionController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginUserDtoResponse> loginUser(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                          @RequestBody @Valid LoginUserDtoRequest request){
        return userService.loginUser(cookieValue, request);
    }

    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LogoutUserDtoResponse> logoutUser(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue) {
        return userService.logoutUser(cookieValue);
    }
}
