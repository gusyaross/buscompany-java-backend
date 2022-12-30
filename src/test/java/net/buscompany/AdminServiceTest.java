package net.buscompany;

import net.buscompany.dto.request.update.UpdateAdminInfoDtoRequest;
import net.buscompany.dto.response.admin.*;
import net.buscompany.dto.response.info.GetInfoAdminDtoResponse;
import net.buscompany.dto.request.admin.AddBusDtoRequest;
import net.buscompany.dto.request.admin.AddTripDtoRequest;
import net.buscompany.dto.request.admin.ScheduleDtoRequest;
import net.buscompany.dto.request.admin.UpdateTripDtoRequest;
import net.buscompany.dto.request.register.RegisterAdminDtoRequest;
import net.buscompany.dto.request.register.RegisterClientDtoRequest;
import net.buscompany.dto.response.debug.ClearDatabaseDtoResponse;
import net.buscompany.dto.response.error.ErrorDtoResponse;
import net.buscompany.dto.response.user.GetTripsDtoResponse;
import net.buscompany.exception.ErrorCode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.Cookie;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static net.buscompany.TestUtils.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void clearDatabase() {
        restTemplate.postForEntity(URL_DEBUG_CLEAR, null, ClearDatabaseDtoResponse.class);
    }

    @Test
    public void correctRegister() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login1", "password1",
                "position1", "Никита", "Гусев", "Алексеевич");

        HttpEntity<RegisterAdminDtoRequest> requestEntity = new HttpEntity<>(adminDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(URL_ADMIN, HttpMethod.POST, requestEntity, String.class);

        HttpHeaders headers = response.getHeaders();

        Cookie cookie = getCookieFromHeader(headers);

        Assertions.assertEquals(cookie.getName(), "JAVASESSIONID");
        Assertions.assertEquals(response.getStatusCode().value(), 200);
    }

    @Test
    public void incorrectNameRegister() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login2", "password2",
                "position2", "wrongName", "Гусев", "Алексеевич");

        HttpEntity<RegisterAdminDtoRequest> requestEntity = new HttpEntity<>(adminDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(URL_ADMIN, HttpMethod.POST, requestEntity, String.class);

        Assertions.assertEquals(response.getStatusCode().value(), 400);
    }

    @Test
    public void incorrectPasswordRegister() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login3", "444",
                "position3", "Никита", "Гусев", "Алексеевич");

        HttpEntity<RegisterAdminDtoRequest> requestEntity = new HttpEntity<>(adminDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(URL_ADMIN, HttpMethod.POST, requestEntity, String.class);

        Assertions.assertEquals(response.getStatusCode().value(), 400);
    }

    @Test
    public void testGetAllClients() {
        restTemplate.postForEntity(URL_DEBUG_CLEAR, null, ClearDatabaseDtoResponse.class);

        RegisterClientDtoRequest clientDtoRequest1 = new RegisterClientDtoRequest("login1", "password1",
                "email@gmail.com", "89227883556", "Никита", "Гусев", "Алексеевич");

        RegisterClientDtoRequest clientDtoRequest2 = new RegisterClientDtoRequest("login2", "password2",
                "email2@gmail.com", "79227883556", "Дмитрий", "Гусев", "Алексеевич");

        restTemplate.exchange(URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest1), String.class);
        restTemplate.exchange(URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest2), String.class);

        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login3", "password3",
                "position3", "Никита", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());


        ResponseEntity<GetClientListDtoResponse> responseClients =
                restTemplate.exchange(URL_CLIENT, HttpMethod.GET, new HttpEntity<>(null, getHttpHeadersWithCookie(cookieAdmin)),
                        GetClientListDtoResponse.class);

        Assertions.assertNotNull(responseClients.getBody());
        Assertions.assertEquals(responseClients.getBody().getClients().get(0).getPhoneNumber(), clientDtoRequest1.getPhoneNumber());
        Assertions.assertEquals(responseClients.getBody().getClients().get(1).getPhoneNumber(), clientDtoRequest2.getPhoneNumber());
    }


    @Test
    public void testUpdateAndGetInfo() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login4", "password4",
                "position4", "Василий", "Гусев", "Алексеевич");

        ResponseEntity<String> response =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookie = getCookieFromHeader(response.getHeaders());

        UpdateAdminInfoDtoRequest updateAdminInfoDtoRequest =
                new UpdateAdminInfoDtoRequest("Василий", "Гусев", "Алексеевич",
                        "password4", "newPassword", "newPosition");

        restTemplate.exchange(URL_ADMIN, HttpMethod.PUT, new HttpEntity<>(updateAdminInfoDtoRequest, getHttpHeadersWithCookie(cookie)), String.class);

        ResponseEntity<GetInfoAdminDtoResponse> responseInfo = restTemplate.exchange(URL_ACCOUNT, HttpMethod.GET,
                new HttpEntity<>(null, getHttpHeadersWithCookie(cookie)), GetInfoAdminDtoResponse.class);

        Assertions.assertNotNull(responseInfo.getBody());
        Assertions.assertEquals(responseInfo.getBody().getPosition(), updateAdminInfoDtoRequest.getPosition());
    }

    @Test
    public void testInsertBusesAndGet() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login5", "password5",
                "position5", "Андрей", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus1", 20);
        AddBusDtoRequest addBusDtoRequest2 = new AddBusDtoRequest("nameBus2", 25);

        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), String.class);
        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest2, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        ResponseEntity<GetAllBusesDtoResponse> response =
                restTemplate.exchange(URL_BUS, HttpMethod.GET, new HttpEntity<>(null, getHttpHeadersWithCookie(cookieAdmin)), GetAllBusesDtoResponse.class);

        int totalPlaceCount = addBusDtoRequest.getPlaceCount() + addBusDtoRequest2.getPlaceCount();

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getBuses().get(0).getPlaceCount() + response.getBody().getBuses().get(1).getPlaceCount(), totalPlaceCount);
    }

    @Test
    public void testInsertTripBusNotFound() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login6", "password6",
                "position6", "Михаил", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        ScheduleDtoRequest scheduleDtoRequest = new ScheduleDtoRequest(LocalDate.of(2022, 11, 1),
                LocalDate.of(2022, 11, 10), "even");

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("wrongBus", "station1", "station2",
                LocalTime.of(12, 10), 180, 500, scheduleDtoRequest, null);

        ResponseEntity<ErrorDtoResponse> response = restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), ErrorDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getErrors().get(0).getErrorCode(), ErrorCode.BUS_NOT_FOUND.toString());
    }

    @Test
    public void testCorrectInsertTripWithSchedule() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login6", "password6",
                "position6", "Михаил", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus3", 15);

        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        ScheduleDtoRequest scheduleDtoRequest = new ScheduleDtoRequest(LocalDate.of(2022, 11, 1),
                LocalDate.of(2022, 11, 10), "even");

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus3", "station1", "station2",
                LocalTime.of(12, 10), 180, 500, scheduleDtoRequest, null);

        ResponseEntity<AddTripDtoResponse> response = restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);


        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getDates().size(), 4);
        Assertions.assertEquals(response.getBody().getBus().getName(), addBusDtoRequest.getName());
        Assertions.assertEquals(response.getBody().getScheduleDtoResponse().getPeriod(), scheduleDtoRequest.getPeriod());
        Assertions.assertEquals(response.getBody().getFromStation(), addTripDtoRequest.getFromStation());
        Assertions.assertEquals(response.getBody().getToStation(), addTripDtoRequest.getToStation());
    }

    @Test
    public void testCorrectInsertTripWithDates() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login6", "password6",
                "position6", "Михаил", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus3", 15);

        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022, 11, 15), LocalDate.of(2022, 11, 17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus3", "station1", "station2",
                LocalTime.of(12, 10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> response = restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);


        Assertions.assertNotNull(response.getBody());
        Assertions.assertNull(response.getBody().getScheduleDtoResponse());
        Assertions.assertEquals(response.getBody().getDates().size(), 2);
        Assertions.assertEquals(response.getBody().getBus().getName(), addBusDtoRequest.getName());
        Assertions.assertEquals(response.getBody().getFromStation(), addTripDtoRequest.getFromStation());
        Assertions.assertEquals(response.getBody().getToStation(), addTripDtoRequest.getToStation());
    }

    @Test
    public void testUpdateTrip() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login8", "password8",
                "position8", "Руслан", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus5", 10);

        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        ScheduleDtoRequest scheduleDtoRequest = new ScheduleDtoRequest(LocalDate.of(2022, 11, 1),
                LocalDate.of(2022, 11, 10), "even");

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus5", "station1", "station2",
                LocalTime.of(12, 10), 180, 500, scheduleDtoRequest, null);

        ResponseEntity<AddTripDtoResponse> addTripResponse = restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        UpdateTripDtoRequest updateTripDtoRequest = new UpdateTripDtoRequest("nameBus5", "station3", "station4",
                LocalTime.of(12, 10), 200, 1000, scheduleDtoRequest, null);

        Assertions.assertNotNull(addTripResponse.getBody());
        ResponseEntity<UpdateTripDtoResponse> response = restTemplate.exchange(URL_TRIP + '/' + addTripResponse.getBody().getId(), HttpMethod.PUT,
                new HttpEntity<>(updateTripDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), UpdateTripDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getPrice(), updateTripDtoRequest.getPrice());
        Assertions.assertEquals(response.getBody().getDurationInMinutes(), updateTripDtoRequest.getDurationInMinutes());
    }

    @Test
    public void testGetTrip() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login9", "password9",
                "position9", "Максим", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus6", 15);

        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022, 11, 15), LocalDate.of(2022, 11, 17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus6", "station3", "station4",
                LocalTime.of(12, 10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());

        ResponseEntity<GetTripInfoDtoResponse> response = restTemplate.exchange(URL_TRIP + '/' + responseAddTrip.getBody().getId(),
                HttpMethod.GET, new HttpEntity<>(null, getHttpHeadersWithCookie(cookieAdmin)), GetTripInfoDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getDates().size(), 2);
        Assertions.assertEquals(response.getBody().getBus().getName(), addBusDtoRequest.getName());
        Assertions.assertEquals(response.getBody().getFromStation(), addTripDtoRequest.getFromStation());
        Assertions.assertEquals(response.getBody().getToStation(), addTripDtoRequest.getToStation());
    }

    @Test
    public void testDeleteTrip() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login10", "password10",
                "position10", "Вадим", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus7", 15);

        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022, 11, 15), LocalDate.of(2022, 11, 17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus7", "station3", "station4",
                LocalTime.of(12, 10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());

        restTemplate.exchange(URL_TRIP + '/' + responseAddTrip.getBody().getId(), HttpMethod.DELETE,
                new HttpEntity<>(null, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        ResponseEntity<ErrorDtoResponse> response = restTemplate.exchange(URL_TRIP + '/' + responseAddTrip.getBody().getId(), HttpMethod.DELETE,
                new HttpEntity<>(null, getHttpHeadersWithCookie(cookieAdmin)), ErrorDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getErrors().get(0).getErrorCode(), ErrorCode.TRIP_NOT_EXISTS.toString());
    }

    @Test
    public void testApproveTrip() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login11", "password11",
                "position11", "Владислав", "Гусев", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus8", 15);

        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022, 11, 15), LocalDate.of(2022, 11, 17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus8", "station3", "station4",
                LocalTime.of(12, 10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());
        Assertions.assertFalse(responseAddTrip.getBody().isApproved());

        ResponseEntity<ApproveTripDtoResponse> response = restTemplate.exchange(URL_TRIP + '/' + responseAddTrip.getBody().getId() + "/approve", HttpMethod.PUT,
                new HttpEntity<>(null, getHttpHeadersWithCookie(cookieAdmin)), ApproveTripDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertTrue(response.getBody().isApproved());
    }

    @Test
    public void testGetTripWithParams() {
        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login12", "password12",
                "position12", "Владислав", "Петров", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus9", 15);

        restTemplate.exchange(URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022, 11, 15), LocalDate.of(2022, 11, 17)));

        AddTripDtoRequest addTripDtoRequest1 = new AddTripDtoRequest("nameBus9", "station5", "station6",
                LocalTime.of(12, 10), 180, 500, null, localDates);

        AddTripDtoRequest addTripDtoRequest2 = new AddTripDtoRequest("nameBus9", "station5", "station6",
                LocalTime.of(12, 10), 200, 600, null, localDates);

        AddTripDtoRequest addTripDtoRequest3 = new AddTripDtoRequest("nameBus9", "station5", "station7",
                LocalTime.of(12, 10), 180, 600, null, localDates);

        restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest1, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest2, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        restTemplate.exchange(URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest3, getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        String urlTemplate = UriComponentsBuilder.fromPath(URL_TRIP)
                .queryParam("fromStation", "{fromStation}")
                .queryParam("toStation", "{toStation}")
                .queryParam("busName", "{busName}")
                .queryParam("fromDate", "{fromDate}")
                .queryParam("toDate", "{toDate}")
                .encode()
                .toUriString();

        Map<String, String> params = Map.ofEntries(
                Map.entry("fromStation", "station5"),
                Map.entry("toStation", "station7"),
                Map.entry("busName", "nameBus9"),
                Map.entry("fromDate", "2022-11-15"),
                Map.entry("toDate", "2022-11-17")
        );

        ResponseEntity<GetTripsDtoResponse> response = restTemplate.exchange(urlTemplate, HttpMethod.GET,
                new HttpEntity<>(null, getHttpHeadersWithCookie(cookieAdmin)), GetTripsDtoResponse.class, params);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getTrips().size(), 1);
        Assertions.assertEquals(response.getBody().getTrips().get(0).getPrice(), addTripDtoRequest3.getPrice());
        Assertions.assertEquals(response.getBody().getTrips().get(0).getToStation(), addTripDtoRequest3.getToStation());
        Assertions.assertEquals(response.getBody().getTrips().get(0).getFromStation(), addTripDtoRequest3.getFromStation());
    }
}