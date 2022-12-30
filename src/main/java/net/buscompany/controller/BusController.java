package net.buscompany.controller;

import net.buscompany.Cookies;
import net.buscompany.service.AdminService;
import net.buscompany.dto.request.admin.AddBusDtoRequest;
import net.buscompany.dto.response.admin.BusDtoResponse;
import net.buscompany.dto.response.admin.GetAllBusesDtoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/buses")
public class BusController {
    private final AdminService adminService;

    public BusController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BusDtoResponse> addBus(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                 @RequestBody @Valid AddBusDtoRequest request) {
        return adminService.addBus(cookieValue, request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetAllBusesDtoResponse> getAllBuses(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue) {
        return adminService.getBusList(cookieValue);
    }
}
