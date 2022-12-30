package net.buscompany.controller;

import net.buscompany.dto.request.client.ChoosePlaceDtoRequest;
import net.buscompany.dto.response.client.GetFreePlacesDtoResponse;
import net.buscompany.service.ClientService;
import net.buscompany.Cookies;
import net.buscompany.dto.response.client.ChoosePlaceDtoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/places")
public class PlacesController {
    private final ClientService clientService;

    public PlacesController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetFreePlacesDtoResponse> getFreePlaces(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                                  @PathVariable @Min(1) int orderId){
        return clientService.getFreePlaces(cookieValue, orderId);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ChoosePlaceDtoResponse> choosePlace(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                              @RequestBody @Valid ChoosePlaceDtoRequest request){
        return clientService.choosePlace(cookieValue, request);
    }
}
