package net.buscompany.controller;

import net.buscompany.Cookies;
import net.buscompany.dto.response.settings.GetSettingsDtoResponse;
import net.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {
    private final UserService userService;

    public SettingsController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetSettingsDtoResponse> getSettings(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue) {
        return userService.getSettings(cookieValue);
    }
}
