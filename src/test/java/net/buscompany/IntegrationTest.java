package net.buscompany;

import net.buscompany.dto.request.client.ChoosePlaceDtoRequest;
import net.buscompany.dto.request.register.RegisterAdminDtoRequest;
import net.buscompany.dto.request.register.RegisterClientDtoRequest;
import net.buscompany.dto.response.client.ChoosePlaceDtoResponse;
import net.buscompany.dto.response.client.GetFreePlacesDtoResponse;
import net.buscompany.dto.response.debug.ClearDatabaseDtoResponse;
import net.buscompany.dto.request.admin.AddBusDtoRequest;
import net.buscompany.dto.request.admin.AddTripDtoRequest;
import net.buscompany.dto.request.client.OrderDtoRequest;
import net.buscompany.dto.request.client.PassengerDtoRequest;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    public void clearDatabase() {
        String DEBUG_CLEAR_URL = "/api/debug/clear";
        restTemplate.postForEntity(DEBUG_CLEAR_URL, null, ClearDatabaseDtoResponse.class);
    }

    @Test
    public void integrationTest() {
        RegisterClientDtoRequest clientDtoRequest = new RegisterClientDtoRequest("login1", "password1",
                "email1@gmail.com", "79227883559", "Дмитрий", "Ильев", "Алексеевич");

        ResponseEntity<String> responseClient =
                restTemplate.exchange(TestUtils.URL_CLIENT, HttpMethod.POST, new HttpEntity<>(clientDtoRequest), String.class);

        Cookie cookieClient = TestUtils.getCookieFromHeader(responseClient.getHeaders());

        RegisterAdminDtoRequest adminDtoRequest = new RegisterAdminDtoRequest("login2", "password2",
                "position2", "Михаил", "Смирнов", "Алексеевич");

        ResponseEntity<String> responseAdmin =
                restTemplate.exchange(TestUtils.URL_ADMIN, HttpMethod.POST, new HttpEntity<>(adminDtoRequest), String.class);

        Cookie cookieAdmin = TestUtils.getCookieFromHeader(responseAdmin.getHeaders());

        AddBusDtoRequest addBusDtoRequest = new AddBusDtoRequest("nameBus10", 4);

        restTemplate.exchange(TestUtils.URL_BUS, HttpMethod.POST, new HttpEntity<>(addBusDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), String.class);

        List<LocalDate> localDates = new ArrayList<>
                (Arrays.asList(LocalDate.of(2022, 11, 15), LocalDate.of(2022, 11, 17)));

        AddTripDtoRequest addTripDtoRequest = new AddTripDtoRequest("nameBus10", "station6", "station7",
                LocalTime.of(12, 10), 180, 500, null, localDates);

        ResponseEntity<AddTripDtoResponse> responseAddTrip = restTemplate.exchange(TestUtils.URL_TRIP, HttpMethod.POST,
                new HttpEntity<>(addTripDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), AddTripDtoResponse.class);

        Assertions.assertNotNull(responseAddTrip.getBody());

        restTemplate.exchange(TestUtils.URL_TRIP + '/' + responseAddTrip.getBody().getId() + "/approve", HttpMethod.PUT,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieAdmin)), ApproveTripDtoResponse.class);

        PassengerDtoRequest passenger = new PassengerDtoRequest("имя", "фамилия", "11110003");
        PassengerDtoRequest passenger3 = new PassengerDtoRequest("имя3", "фамилия3", "11110005");
        List<PassengerDtoRequest> passengers = new ArrayList<>();
        passengers.add(passenger);
        passengers.add(passenger3);

        OrderDtoRequest order =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022, 11, 15), passengers);

        PassengerDtoRequest passenger2 = new PassengerDtoRequest("имя2", "фамилия2", "11110004");
        List<PassengerDtoRequest> passengers2 = new ArrayList<>();
        passengers2.add(passenger2);

        OrderDtoRequest order2 =
                new OrderDtoRequest(responseAddTrip.getBody().getId(), LocalDate.of(2022, 11, 15), passengers2);

        ResponseEntity<OrderDtoResponse> responseOrder = restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                new HttpEntity<>(order, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        Assertions.assertNotNull(responseOrder.getBody());

        ResponseEntity<OrderDtoResponse> responseOrder2 = restTemplate.exchange(TestUtils.URL_ORDER, HttpMethod.POST,
                new HttpEntity<>(order2, TestUtils.getHttpHeadersWithCookie(cookieClient)), OrderDtoResponse.class);

        Assertions.assertNotNull(responseOrder2.getBody());

        ResponseEntity<GetFreePlacesDtoResponse> responsePlaces = restTemplate.exchange(TestUtils.URL_PLACE + '/' + responseOrder2.getBody().getIdOrder(),
                HttpMethod.GET, new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieClient)), GetFreePlacesDtoResponse.class);

        Assertions.assertNotNull(responsePlaces.getBody());
        Assertions.assertEquals(responsePlaces.getBody().getPlaces().size(), 1);

        ChoosePlaceDtoRequest choosePlaceDtoRequest = new ChoosePlaceDtoRequest(responseOrder2.getBody().getIdOrder(),
                passenger2.getFirstName(), passenger2.getLastName(), passenger2.getPassport(), 1);

        ResponseEntity<ErrorDtoResponse> errorResponse = restTemplate.exchange(TestUtils.URL_PLACE, HttpMethod.POST,
                new HttpEntity<>(choosePlaceDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieClient)), ErrorDtoResponse.class);

        Assertions.assertNotNull(errorResponse.getBody());
        Assertions.assertEquals(errorResponse.getBody().getErrors().get(0).getErrorCode(), ErrorCode.NOT_FREE_PLACE.toString());

        restTemplate.exchange(TestUtils.URL_ORDER + '/' + responseOrder.getBody().getIdOrder(), HttpMethod.DELETE,
                new HttpEntity<>(null, TestUtils.getHttpHeadersWithCookie(cookieClient)), String.class);

        ResponseEntity<ChoosePlaceDtoResponse> response = restTemplate.exchange(TestUtils.URL_PLACE, HttpMethod.POST,
                new HttpEntity<>(choosePlaceDtoRequest, TestUtils.getHttpHeadersWithCookie(cookieClient)), ChoosePlaceDtoResponse.class);

        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(response.getBody().getPlaceNumber(),1);
    }
}
