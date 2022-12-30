package net.buscompany;

import net.buscompany.dto.request.client.ChoosePlaceDtoRequest;
import net.buscompany.dto.request.register.RegisterClientDtoRequest;
import net.buscompany.dto.request.update.UpdateClientInfoDtoRequest;
import net.buscompany.dto.response.client.ChoosePlaceDtoResponse;
import net.buscompany.dto.response.client.GetFreePlacesDtoResponse;
import net.buscompany.dto.response.debug.ClearDatabaseDtoResponse;
import net.buscompany.dto.response.info.GetInfoClientDtoResponse;
import net.buscompany.dto.response.user.GetOrdersDtoResponse;
import net.buscompany.dto.request.admin.AddBusDtoRequest;
import net.buscompany.dto.request.admin.AddTripDtoRequest;
import net.buscompany.dto.request.client.OrderDtoRequest;
import net.buscompany.dto.request.client.PassengerDtoRequest;
import net.buscompany.dto.request.register.RegisterAdminDtoRequest;
import net.buscompany.dto.response.admin.AddTripDtoResponse;
import net.buscompany.dto.response.admin.ApproveTripDtoResponse;
import net.buscompany.dto.response.client.OrderDtoResponse;
import net.buscompany.dto.response.error.ErrorDtoResponse;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientServiceTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void clearDatabase() {
        String DEBUG_CLEAR_URL = "/api/debug/clear";
        restTemplate.postForEntity(DEBUG_CLEAR_URL, null, ClearDatabaseDtoResponse.class);
    }

    @Test
    public void correctRegister() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login1","password1",
                "email@gmail.com","89227883556","Никита","Гусев","Алексеевич");

        HttpEntity<RegisterClientDtoRequest> requestEntity = new HttpEntity<>(clientDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, requestEntity, String.class);

        HttpHeaders headers = response.getHeaders();

        Cookie cookie = TestUtils.getCookieFromHeader(headers);

        Assertions.assertEquals(cookie.getName(),"JAVASESSIONID");
        Assertions.assertEquals(response.getStatusCode().value(),200);
    }

    @Test
    public void incorrectNameRegister() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login2","password2",
                "email2@gmail.com","89227883557","wrongName","Гусев","Алексеевич");

        HttpEntity<RegisterClientDtoRequest> requestEntity = new HttpEntity<>(clientDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, requestEntity, String.class);

        Assertions.assertEquals(response.getStatusCode().value(),400);
    }

    @Test
    public void incorrectPasswordRegister() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login3","333",
                "email3@gmail.com","89227883558","Никита","Гусев","Алексеевич");

        HttpEntity<RegisterClientDtoRequest> requestEntity = new HttpEntity<>(clientDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, requestEntity, String.class);

        Assertions.assertEquals(response.getStatusCode().value(),400);
    }

    @Test
    public void incorrectPhoneNumber1Register() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login4","password4",
                "email4@gmail.com","+19227883558","Никита","Гусев","Алексеевич");

        HttpEntity<RegisterClientDtoRequest> requestEntity = new HttpEntity<>(clientDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, requestEntity, String.class);

        Assertions.assertEquals(response.getStatusCode().value(),400);
    }

    @Test
    public void incorrectPhoneNumber2Register() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login5","password5",
                "email5@gmail.com","11111","Никита","Гусев","Алексеевич");

        HttpEntity<RegisterClientDtoRequest> requestEntity = new HttpEntity<>(clientDtoRequest);
        ResponseEntity<String> response = restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, requestEntity, String.class);

        Assertions.assertEquals(response.getStatusCode().value(),400);
    }

    @Test
    public void testUpdateAndGetInfo() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login6","password6",
                "email6@gmail.com","79227883559","Максим","Гусев","Алексеевич");

        ResponseEntity<String> response =
                restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest), String.class);

        Cookie cookie = TestUtils.getCookieFromHeader(response.getHeaders());

        UpdateClientInfoDtoRequest updateClientInfoDtoRequest =
                new UpdateClientInfoDtoRequest("Максим", "Гусев", "Алексеевич",
                        "password6", "newPassword","email6@gmail.com","79227883552");

        restTemplate.exchange(TestUtils.URL_CLIENT,HttpMethod.PUT,new HttpEntity<>(updateClientInfoDtoRequest, TestUtils.getHttpHeadersWithCookie(cookie)), String.class);

        ResponseEntity<GetInfoClientDtoResponse> responseInfo = restTemplate.exchange(TestUtils.URL_ACCOUNT,HttpMethod.GET,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookie)),GetInfoClientDtoResponse.class);

        Assertions.assertNotNull(responseInfo.getBody());
        Assertions.assertEquals(responseInfo.getBody().getPhoneNumber(),updateClientInfoDtoRequest.getPhoneNumber());
    }

    @Test
    public void testCreateOrderNotApproved() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login7","password7",
                "email7@gmail.com","79227883559","Максим","Ильев","Алексеевич");

        ResponseEntity<String> responseClient =
                restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest), String.class);

        Cookie cookieClient = TestUtils.getCookieFromHeader(responseClient.getHeaders());

        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login1","password1",
                "position1","Михаил","Гусев","Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = TestUtils.getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus1",15);

        restTemplate.exchange(TestUtils.URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022,11,15), LocalDate.of(2022,11,17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus1","station1","station2",
                LocalTime.of(12,10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(TestUtils.URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());

        PassengerDtoRequest passenger = new PassengerDtoRequest("имя1","фамилия1","11110000");
        List<PassengerDtoRequest> passengers = new ArrayList<>();
        passengers.add(passenger);

        OrderDtoRequest order = new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022,11,16), passengers);

        ResponseEntity<ErrorDtoResponse> response =
                restTemplate.exchange(TestUtils.URL_ORDER,HttpMethod.POST,new HttpEntity<>(order, TestUtils.getHttpHeadersWithCookie(cookieClient)), ErrorDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getErrors().get(0).getErrorCode(), ErrorCode.TRIP_NOT_APPROVED.toString());
    }

    @Test
    public void testCorrectCreateOrder() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login8","password8",
                "email8@gmail.com","79227883559","Никита","Ильев","Алексеевич");

        ResponseEntity<String> responseClient =
                restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest), String.class);

        Cookie cookieClient = TestUtils.getCookieFromHeader(responseClient.getHeaders());

        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login2","password2",
                "position2","Михаил","Ильев","Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = TestUtils.getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus2",15);

        restTemplate.exchange(TestUtils.URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022,11,15), LocalDate.of(2022,11,17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus2","station3","station4",
                LocalTime.of(12,10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(TestUtils.URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());

        restTemplate.exchange(TestUtils.URL_TRIP + '/' + responseAddTrip.getBody().getId() + "/approve", HttpMethod.PUT,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), ApproveTripDtoResponse.class);

        PassengerDtoRequest passenger = new PassengerDtoRequest("имя2","фамилия2","11110000");
        List<PassengerDtoRequest> passengers = new ArrayList<>();
        passengers.add(passenger);

        OrderDtoRequest order =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022,11,15), passengers);

        ResponseEntity<OrderDtoResponse> response =
                restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                        new HttpEntity<>(order, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getTotalPrice(),500);
        Assertions.assertEquals(response.getBody().getBusName(),addTripDtoRequest.getBusName());
        Assertions.assertEquals(response.getBody().getFromStation(),addTripDtoRequest.getFromStation());
        Assertions.assertEquals(response.getBody().getToStation(), addTripDtoRequest.getToStation());
    }

    @Test
    public void testGetOrdersByParams() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login9","password9",
                "email9@gmail.com","79227883559","Вадим","Ильев","Алексеевич");

        ResponseEntity<String> responseClient =
                restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest), String.class);

        Cookie cookieClient = TestUtils.getCookieFromHeader(responseClient.getHeaders());

        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login3","password3",
                "position3","Михаил","Ильев","Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = TestUtils.getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus3",15);

        restTemplate.exchange(TestUtils.URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022,11,15), LocalDate.of(2022,11,17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus3","station3","station4",
                LocalTime.of(12,10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(TestUtils.URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());

        restTemplate.exchange(TestUtils.URL_TRIP + '/' + responseAddTrip.getBody().getId() + "/approve", HttpMethod.PUT,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), ApproveTripDtoResponse.class);

        PassengerDtoRequest passenger = new PassengerDtoRequest("имя3","фамилия3","11110001");
        List<PassengerDtoRequest> passengers = new ArrayList<>();
        passengers.add(passenger);

        OrderDtoRequest order1 =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022,11,15), passengers);

        OrderDtoRequest order2 =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022,11,17), passengers);

        OrderDtoRequest order3 =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022,11,15), passengers);


        restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                new HttpEntity<>(order1, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        ResponseEntity<OrderDtoResponse> responseOrder2 = restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                new HttpEntity<>(order2, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                new HttpEntity<>(order3, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        String urlTemplate = UriComponentsBuilder.fromPath(TestUtils.URL_ORDER)
                .queryParam("fromStation", "{fromStation}")
                .queryParam("toStation", "{toStation}")
                .queryParam("busName", "{busName}")
                .queryParam("fromDate", "{fromDate}")
                .queryParam("toDate", "{toDate}")
                .encode()
                .toUriString();

        Map<String, String> params = Map.ofEntries(
                Map.entry("fromStation","station3"),
                Map.entry("toStation","station4"),
                Map.entry("busName", "nameBus3"),
                Map.entry("fromDate", "2022-11-16"),
                Map.entry("toDate","2022-11-19")
        );

        ResponseEntity<GetOrdersDtoResponse> response = restTemplate.exchange(urlTemplate, HttpMethod.GET,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), GetOrdersDtoResponse.class, params);

        Assertions.assertNotNull(responseOrder2.getBody());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getOrders().size(),1);
        Assertions.assertEquals(response.getBody().getOrders().get(0).getIdOrder(),responseOrder2.getBody().getIdOrder());
    }

    @Test
    public void testGetFreePlacesAndReservePlaces() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login10","password10",
                "email10@gmail.com","79227883559","Дмитрий","Ильев","Алексеевич");

        ResponseEntity<String> responseClient =
                restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest), String.class);

        Cookie cookieClient = TestUtils.getCookieFromHeader(responseClient.getHeaders());

        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login4","password4",
                "position4","Михаил","Смирнов","Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = TestUtils.getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus4",4);

        restTemplate.exchange(TestUtils.URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022,11,15), LocalDate.of(2022,11,17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus4","station3","station4",
                LocalTime.of(12,10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(TestUtils.URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());

        restTemplate.exchange(TestUtils.URL_TRIP + '/' + responseAddTrip.getBody().getId() + "/approve", HttpMethod.PUT,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), ApproveTripDtoResponse.class);

        PassengerDtoRequest passenger = new PassengerDtoRequest("имя4","фамилия4","11110002");
        List<PassengerDtoRequest> passengers = new ArrayList<>();
        passengers.add(passenger);

        OrderDtoRequest order =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022,11,15), passengers);

        ResponseEntity<OrderDtoResponse> responseOrder = restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                new HttpEntity<>(order, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        Assertions.assertNotNull(responseOrder.getBody());

        ResponseEntity<GetFreePlacesDtoResponse> responsePlaces1 = restTemplate.exchange(TestUtils.URL_PLACE + '/' + responseOrder.getBody().getIdOrder(),
                HttpMethod.GET, new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieClient)), GetFreePlacesDtoResponse.class);

        Assertions.assertNotNull(responsePlaces1.getBody());
        Assertions.assertEquals(responsePlaces1.getBody().getPlaces().size(),3);


        ChoosePlaceDtoRequest choosePlaceDtoRequest = new ChoosePlaceDtoRequest(responseOrder.getBody().getIdOrder(),
                passenger.getFirstName(), passenger.getLastName(), passenger.getPassport(), 2);

        ResponseEntity<ChoosePlaceDtoResponse> response = restTemplate.exchange(TestUtils.URL_PLACE, HttpMethod.POST,
                new HttpEntity<>(choosePlaceDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieClient)), ChoosePlaceDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getPlaceNumber(),2);
        Assertions.assertEquals(response.getBody().getOrderId(),responseOrder.getBody().getIdOrder());
        Assertions.assertEquals(response.getBody().getPassport(),passenger.getPassport());
    }

    @Test
    public void testCancelOrder() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login11", "password11",
                "email11@gmail.com", "79227883559", "Дмитрий", "Ильев", "Алексеевич");

        ResponseEntity<String> responseClient =
                restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest), String.class);

        Cookie cookieClient = TestUtils.getCookieFromHeader(responseClient.getHeaders());

        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login5", "password5",
                "position5", "Михаил", "Смирнов", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = TestUtils.getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus5", 4);

        restTemplate.exchange(TestUtils.URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022, 11, 15), LocalDate.of(2022, 11, 17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus5", "station3", "station4",
                LocalTime.of(12, 10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(TestUtils.URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());

        restTemplate.exchange(TestUtils.URL_TRIP + '/' + responseAddTrip.getBody().getId() + "/approve", HttpMethod.PUT,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), ApproveTripDtoResponse.class);

        PassengerDtoRequest passenger = new PassengerDtoRequest("имя5", "фамилия5", "11110003");
        List<PassengerDtoRequest> passengers = new ArrayList<>();
        passengers.add(passenger);

        OrderDtoRequest order =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022, 11, 15), passengers);

        OrderDtoRequest order2 =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022, 11, 15), passengers);

        ResponseEntity<OrderDtoResponse> responseOrder = restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                new HttpEntity<>(order, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        ResponseEntity<OrderDtoResponse> responseOrder2 = restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                new HttpEntity<>(order2, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        Assertions.assertNotNull(responseOrder2.getBody());
        Assertions.assertNotNull(responseOrder.getBody());

        ResponseEntity<GetFreePlacesDtoResponse> responsePlaces1 = restTemplate.exchange(TestUtils.URL_PLACE + '/' + responseOrder.getBody().getIdOrder(),
                HttpMethod.GET, new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieClient)), GetFreePlacesDtoResponse.class);

        Assertions.assertNotNull(responsePlaces1.getBody());
        Assertions.assertEquals(responsePlaces1.getBody().getPlaces().size(), 2);

        restTemplate.exchange(TestUtils.URL_ORDER + '/' + responseOrder.getBody().getIdOrder(), HttpMethod.DELETE,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieClient)), String.class);

        ResponseEntity<GetFreePlacesDtoResponse> responsePlaces2 = restTemplate.exchange(TestUtils.URL_PLACE + '/' + responseOrder2.getBody().getIdOrder(),
                HttpMethod.GET, new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieClient)), GetFreePlacesDtoResponse.class);

        Assertions.assertNotNull(responsePlaces2.getBody());
        Assertions.assertEquals(responsePlaces2.getBody().getPlaces().size(), 3);
    }
}
