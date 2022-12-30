package net.buscompany.controller;

import net.buscompany.Cookies;
import net.buscompany.dto.response.admin.*;
import net.buscompany.service.AdminService;
import net.buscompany.dto.request.admin.AddTripDtoRequest;
import net.buscompany.dto.request.admin.UpdateTripDtoRequest;
import net.buscompany.dto.response.user.GetTripsDtoResponse;
import net.buscompany.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final UserService userService;
    private final AdminService adminService;

    public TripController(UserService userService, AdminService adminService) {
        this.userService = userService;
        this.adminService = adminService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddTripDtoResponse> addTrip(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                      @RequestBody @Valid AddTripDtoRequest request) {
        return adminService.addTrip(cookieValue, request);
    }

    @PutMapping(value = "/{tripId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpdateTripDtoResponse> updateTrip(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                            @PathVariable("tripId") @Min(1) int tripId,
                                                            @RequestBody @Valid UpdateTripDtoRequest request) {
        return adminService.updateTrip(cookieValue, tripId, request);
    }

    @DeleteMapping(value = "/{tripId}")
    public ResponseEntity<DeleteTripDtoResponse> deleteTrip(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                            @PathVariable("tripId") @Min(1) int tripId) {
        return adminService.deleteTrip(cookieValue, tripId);
    }

    @GetMapping(value = "/{tripId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetTripInfoDtoResponse> getTripInfo(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                              @PathVariable("tripId") @Min(1) int tripId) {
        return adminService.getTripInfo(cookieValue, tripId);
    }

    @PutMapping(value = "/{tripId}/approve", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApproveTripDtoResponse> approveTrip(@CookieValue(required = false, name = Cookies.COOKIE_NAME) String cookieValue,
                                                              @PathVariable("tripId") @Min(1) int tripId) {
        return adminService.approveTrip(cookieValue, tripId);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GetTripsDtoResponse> getTripsWithParams(@CookieValue(required = false, name =  Cookies.COOKIE_NAME) String cookieValue,
                                                           @RequestParam(required = false) String fromStation,
                                                           @RequestParam(required = false) String toStation,
                                                           @RequestParam(required = false) String busName,
                                                           @RequestParam(required = false) String fromDate,
                                                           @RequestParam(required = false) String toDate) {

        return userService.getTripsWithParams(cookieValue, fromStation, toStation, busName, fromDate, toDate);
    }
}
