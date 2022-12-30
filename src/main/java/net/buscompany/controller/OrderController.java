package net.buscompany.controller;

import net.buscompany.Cookies;
import net.buscompany.dto.request.client.OrderDtoRequest;
import net.buscompany.dto.response.user.GetOrdersDtoResponse;
import net.buscompany.service.ClientService;
import net.buscompany.dto.response.client.CancelOrderDtoResponse;
import net.buscompany.dto.response.client.OrderDtoResponse;
import net.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final UserService userService;
    private final ClientService clientService;

    public OrderController(UserService userService, ClientService clientService) {
        this.userService = userService;
        this.clientService = clientService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<OrderDtoResponse> orderTrip(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                      @RequestBody @Valid OrderDtoRequest request) {
        return clientService.createOrder(cookieValue, request);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetOrdersDtoResponse> getOrdersWithParams(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                                    @RequestParam(required = false) String fromStation,
                                                                    @RequestParam(required = false) String toStation,
                                                                    @RequestParam(required = false) String busName,
                                                                    @RequestParam(required = false) String fromDate,
                                                                    @RequestParam(required = false) String toDate,
                                                                    @RequestParam(required = false) String clientId) {
        return userService.getOrdersWithParams(cookieValue, fromStation, toStation, busName, fromDate, toDate, clientId);
    }

    @DeleteMapping(value = "/{orderId}")
    public ResponseEntity<CancelOrderDtoResponse> cancelOrder(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                              @PathVariable @Min(1) int orderId) {
        return clientService.cancelOrder(cookieValue, orderId);
    }
}
