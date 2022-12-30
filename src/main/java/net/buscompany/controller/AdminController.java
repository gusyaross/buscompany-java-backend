package net.buscompany.controller;

import net.buscompany.dto.response.register.RegisterAdminDtoResponse;
import net.buscompany.service.AdminService;
import net.buscompany.Cookies;
import net.buscompany.dto.request.register.RegisterAdminDtoRequest;
import net.buscompany.dto.request.update.UpdateAdminInfoDtoRequest;
import net.buscompany.dto.response.update.UpdateAdminInfoDtoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterAdminDtoResponse> registerAdmin(@RequestBody @Valid RegisterAdminDtoRequest request) {
        return adminService.registerAdmin(request);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateAdminInfoDtoResponse> updateAdminProfile(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                                         @RequestBody @Valid UpdateAdminInfoDtoRequest request) {
        return adminService.updateAdminInfo(cookieValue, request);
    }
}
