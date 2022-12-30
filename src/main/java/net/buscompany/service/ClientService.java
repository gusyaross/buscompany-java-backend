package net.buscompany.service;

import lombok.AllArgsConstructor;
import net.buscompany.dao.ClientDao;
import net.buscompany.dao.UserDao;
import net.buscompany.dto.request.client.ChoosePlaceDtoRequest;
import net.buscompany.dto.request.client.OrderDtoRequest;
import net.buscompany.dto.request.register.RegisterClientDtoRequest;
import net.buscompany.dto.request.update.UpdateClientInfoDtoRequest;
import net.buscompany.dto.response.client.CancelOrderDtoResponse;
import net.buscompany.dto.response.client.ChoosePlaceDtoResponse;
import net.buscompany.dto.response.client.GetFreePlacesDtoResponse;
import net.buscompany.dto.response.client.OrderDtoResponse;
import net.buscompany.dto.response.register.RegisterClientDtoResponse;
import net.buscompany.dto.response.update.UpdateClientInfoDtoResponse;
import net.buscompany.exception.ErrorCode;
import net.buscompany.exception.ServerException;
import net.buscompany.mapper.mapstruct.ClientMapperMapstruct;
import net.buscompany.mapper.mapstruct.OrderMapperMapstruct;
import net.buscompany.model.*;
import net.buscompany.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ClientService {

    private final Logger LOGGER = LoggerFactory.getLogger(ClientService.class);

    private final ClientDao clientDao;
    private final UserDao userDao;

    private final ServiceUtils serviceUtils;

    @Transactional
    public ResponseEntity<RegisterClientDtoResponse> registerClient(RegisterClientDtoRequest request) {
        Client client = ClientMapperMapstruct.INSTANCE.registerClientDtoToClient(request);
        client.setUserType(UserType.CLIENT);

        try {
            userDao.insertUser(client);
        } catch (DuplicateKeyException e) {
            throw new ServerException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String uuid = UUID.randomUUID().toString();
        userDao.insertSession(client, uuid);

        clientDao.insertClient(client);
        clientDao.updateUserType(client);

        ResponseCookie cookie = serviceUtils.createResponseCookie(uuid);

        RegisterClientDtoResponse response = ClientMapperMapstruct.INSTANCE.clientToRegisterClientDto(client);

        LOGGER.info("Client with login " + client.getLogin() + " successfully registered");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<UpdateClientInfoDtoResponse> updateClientInfo(String cookieValue, UpdateClientInfoDtoRequest request) {
        Client client = clientDao.getClientBySession(cookieValue);

        if (client == null) {
            throw new ServerException(ErrorCode.USER_NOT_EXISTS);
        }

        if (!client.getPassword().equals(request.getOldPassword())) {
            throw new ServerException(ErrorCode.INVALID_PASSWORD);
        }

        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setPatronymic(request.getPatronymic());
        client.setPassword(request.getNewPassword());
        client.setEmail(request.getEmail());
        client.setPhoneNumber(request.getPhoneNumber());

        userDao.updateUser(client);
        clientDao.updateClient(client);

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        UpdateClientInfoDtoResponse response = ClientMapperMapstruct.INSTANCE.clientToUpdateClientDto(client);

        LOGGER.info("Client with login " + client.getLogin() + " successfully update info");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<OrderDtoResponse> createOrder(String cookieValue, OrderDtoRequest request) {
        Client client = clientDao.getClientBySession(cookieValue);
        if (client == null) {
            throw new ServerException(ErrorCode.USER_NOT_EXISTS);
        }

        Order order = OrderMapperMapstruct.INSTANCE.orderDtoRequestToOrder(request);

        Trip trip = userDao.getTripById(request.getIdTrip());

        if (trip == null) {
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        if (!trip.isApproved()) {
            throw new ServerException(ErrorCode.TRIP_NOT_APPROVED);
        }

        int passengerCount = order.getPassengers().size();

        AssignedTrip assignedTrip = trip.getAssignedTrips().stream().
                filter(assignedTripStream -> assignedTripStream.getDateTrip().equals(request.getDateTrip())).
                findAny().orElseThrow(() -> new ServerException(ErrorCode.TRIP_NOT_FOUND_AT_THIS_DATE));

        if (clientDao.updateFreePlacesCount(assignedTrip, passengerCount) == 0) {
            throw new ServerException(ErrorCode.NO_FREE_PLACES);
        }

        List<Integer> freePlacesList = new LinkedList<>(clientDao.getFreePlacesList(assignedTrip));

        assignedTrip.setTrip(trip);
        order.setAssignedTrip(assignedTrip);

        clientDao.insertOrder(order, client);

        Iterator<Integer> iterator = freePlacesList.listIterator();
        for (Passenger passenger : order.getPassengers()) {
            clientDao.insertPassenger(passenger,order);
            if (clientDao.updatePlaceWithPassenger(passenger,assignedTrip,iterator.next()) == 0) {
                throw new ServerException(ErrorCode.NOT_FREE_PLACE);
            }
        }

        OrderDtoResponse orderDtoResponse = OrderMapperMapstruct.INSTANCE.orderToOrderDtoResponse(order);
        orderDtoResponse.setTotalPrice(passengerCount*trip.getPrice());

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        LOGGER.info("Client with login " + client.getLogin() + " successfully create order");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(orderDtoResponse);
    }

    @Transactional
    public ResponseEntity<CancelOrderDtoResponse> cancelOrder(String cookieValue, int orderId) {
        Client client = clientDao.getClientBySession(cookieValue);
        if (client == null) {
            throw new ServerException(ErrorCode.USER_NOT_EXISTS);
        }

        Order order = userDao.getOrderById(orderId);
        
        if (order == null) {
            throw new ServerException(ErrorCode.ORDER_NOT_EXISTS);
        }

        Order clientOrder = userDao.getOrderByIdAndClientId(orderId,client.getId());

        if (order.getId() != clientOrder.getId()) {
            throw new ServerException(ErrorCode.NOT_CLIENT_ORDER);
        }

        clientDao.deleteOrder(order);

        LOGGER.info("Client with login " + client.getLogin() + " successfully delete order");

        return ResponseEntity.ok().body(new CancelOrderDtoResponse());
    }

    @Transactional
    public ResponseEntity<GetFreePlacesDtoResponse> getFreePlaces(String cookieValue, int orderId) {
        Client client = clientDao.getClientBySession(cookieValue);
        if (client == null) {
            throw new ServerException(ErrorCode.USER_NOT_EXISTS);
        }

        Order order = userDao.getOrderById(orderId);

        if (order == null) {
            throw new ServerException(ErrorCode.ORDER_NOT_EXISTS);
        }

        List<Integer> freePlaces = clientDao.getFreePlacesList(order.getAssignedTrip());

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        LOGGER.info("Client with login " + client.getLogin() + " successfully get places");

        GetFreePlacesDtoResponse response = new GetFreePlacesDtoResponse(freePlaces);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<ChoosePlaceDtoResponse> choosePlace(String cookieValue, ChoosePlaceDtoRequest request) {
        Client client = clientDao.getClientBySession(cookieValue);
        if (client == null) {
            throw new ServerException(ErrorCode.USER_NOT_EXISTS);
        }

        Order order = userDao.getOrderById(request.getOrderId());

        if (order == null) {
            throw new ServerException(ErrorCode.ORDER_NOT_EXISTS);
        }

        Passenger passengerOnNeedPlace = userDao.getPassengerByPlaceNumber(request.getPlaceNumber(), order.getAssignedTrip());

        if (passengerOnNeedPlace != null) {
            throw new ServerException(ErrorCode.NOT_FREE_PLACE);
        }

        Passenger passenger = order.getPassengers().stream().
                filter(passengerInStream -> passengerInStream.getPassport().equals(request.getPassport())).findAny().
                orElseThrow(() -> new ServerException(ErrorCode.WRONG_PASSENGER));

        clientDao.cancelPassengerPlace(passenger, order.getAssignedTrip());
        clientDao.updatePlaceWithPassenger(passenger, order.getAssignedTrip(), request.getPlaceNumber());

        ChoosePlaceDtoResponse response = OrderMapperMapstruct.INSTANCE.choosePlaceDtoRequestToResponse(request);

        String ticket = "Билет " + request.getOrderId() + '_' + request.getPlaceNumber();
        response.setTicket(ticket);

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        LOGGER.info("Client with login " + client.getLogin() + " successfully choose place");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }
}
