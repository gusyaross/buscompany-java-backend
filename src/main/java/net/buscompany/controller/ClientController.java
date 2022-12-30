package net.buscompany.controller;

import net.buscompany.dto.response.register.RegisterClientDtoResponse;
import net.buscompany.service.AdminService;
import net.buscompany.service.ClientService;
import net.buscompany.Cookies;
import net.buscompany.dto.request.register.RegisterClientDtoRequest;
import net.buscompany.dto.request.update.UpdateClientInfoDtoRequest;
import net.buscompany.dto.response.admin.GetClientListDtoResponse;
import net.buscompany.dto.response.update.UpdateClientInfoDtoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientService clientService;
    private final AdminService adminService;

    public ClientController(ClientService clientService, AdminService adminService) {
        this.clientService = clientService;
        this.adminService = adminService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegisterClientDtoResponse> registerClient(@RequestBody @Valid RegisterClientDtoRequest request) {
        return clientService.registerClient(request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetClientListDtoResponse> getClientList(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue) {
        return adminService.getClientList(cookieValue);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateClientInfoDtoResponse> updateClientProfile(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                                           @RequestBody @Valid UpdateClientInfoDtoRequest request) {
        return clientService.updateClientInfo(cookieValue, request);
    }
}
