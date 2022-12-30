package net.buscompany.service;

import lombok.AllArgsConstructor;
import net.buscompany.dao.AdminDao;
import net.buscompany.dao.ClientDao;
import net.buscompany.dao.UserDao;
import net.buscompany.dto.request.login.LoginUserDtoRequest;
import net.buscompany.dto.response.admin.BusDtoResponse;
import net.buscompany.dto.response.admin.ScheduleDtoResponse;
import net.buscompany.dto.response.info.GetInfoUserDtoResponse;
import net.buscompany.dto.response.login.LoginUserDtoResponse;
import net.buscompany.dto.response.logout.LogoutUserDtoResponse;
import net.buscompany.dto.response.settings.GetSettingsDtoResponse;
import net.buscompany.dto.response.unregister.UnregisterUserDtoResponse;
import net.buscompany.dto.response.user.*;
import net.buscompany.exception.ErrorCode;
import net.buscompany.exception.ServerException;
import net.buscompany.mapper.mapstruct.*;
import net.buscompany.model.*;
import net.buscompany.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@AllArgsConstructor
public class UserService {
    private final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao;
    private final AdminDao adminDao;
    private final ClientDao clientDao;

    private final ServiceUtils serviceUtils;

    @Value("${max_name_length}")
    private static int maxNameLength;

    @Value("${min_password_length}")
    private static int minPasswordLength;

    @Transactional
    public ResponseEntity<LoginUserDtoResponse> loginUser(String cookieValue, LoginUserDtoRequest request) {
        User user = adminDao.getAdminByLogin(request.getLogin());
        if (user == null) {
            user = clientDao.getClientByLogin(request.getLogin());
            if (user == null) {
                throw new ServerException(ErrorCode.USER_NOT_EXISTS);
            }
        }

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ServerException(ErrorCode.INVALID_LOGIN_OR_PASSWORD);
        }

        String uuid = UUID.randomUUID().toString();
        userDao.insertSession(user, uuid);

        UserType userType = user.getUserType();

        LoginUserDtoResponse response;
        if (userType == UserType.ADMIN) {
            Admin admin = (Admin) user;
            response = AdminMapperMapstruct.INSTANCE.adminToLoginAdminDto(admin);
        } else {
            Client client = (Client) user;
            response = ClientMapperMapstruct.INSTANCE.clientToLoginClientDto(client);
        }

        ResponseCookie cookie = serviceUtils.createResponseCookie(uuid);

        LOGGER.info("User with login " + user.getLogin() + " successfully logged in");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<LogoutUserDtoResponse> logoutUser(String cookieValue) {
        User user = adminDao.getAdminBySession(cookieValue);
        if (user == null) {
            user = clientDao.getClientBySession(cookieValue);
            if (user == null) {
                throw new ServerException(ErrorCode.USER_NOT_EXISTS);
            }
        }

        userDao.deleteSession(cookieValue);

        LOGGER.info("User with login " + user.getLogin() + " successfully logged out");

        return ResponseEntity.ok().body(new LogoutUserDtoResponse());
    }

    @Transactional
    public ResponseEntity<UnregisterUserDtoResponse> unregisterUser(String cookieValue) {
        User user = adminDao.getAdminBySession(cookieValue);
        if (user == null) {
            user = clientDao.getClientBySession(cookieValue);
            if (user == null) {
                throw new ServerException(ErrorCode.USER_NOT_EXISTS);
            }
        }

        if (user.getUserType() == UserType.ADMIN) {
            if (userDao.getAdminCount() == 1) {
                throw new ServerException(ErrorCode.ADMIN_COUNT);
            }
        }

        userDao.deleteUser(user);

        LOGGER.info("User with login " + user.getLogin() + " successfully unregistered");

        return ResponseEntity.ok().body(new UnregisterUserDtoResponse());
    }

    @Transactional
    public ResponseEntity<GetInfoUserDtoResponse> getInfoUser(String cookieValue) {
        User user = adminDao.getAdminBySession(cookieValue);
        if (user == null) {
            user = clientDao.getClientBySession(cookieValue);
            if (user == null) {
                throw new ServerException(ErrorCode.USER_NOT_EXISTS);
            }
        }

        UserType userType = user.getUserType();

        GetInfoUserDtoResponse response;
        if (userType == UserType.ADMIN) {
            Admin admin = (Admin) user;
            response = AdminMapperMapstruct.INSTANCE.adminToAdminInfoDto(admin);
        } else {
            Client client = (Client) user;
            response = ClientMapperMapstruct.INSTANCE.clientToClientInfoDto(client);
        }

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        LOGGER.info("User with login " + user.getLogin() + " successfully get info");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    public ResponseEntity<GetSettingsDtoResponse> getSettings(String cookieValue) {
        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);
        GetSettingsDtoResponse response = new GetSettingsDtoResponse(maxNameLength, minPasswordLength);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<GetTripsDtoResponse> getTripsWithParams(String cookieValue, String fromStation, String toStation,
                                                                  String busName, String fromDate, String toDate) {

        User user = adminDao.getAdminBySession(cookieValue);
        if (user == null) {
            user = clientDao.getClientBySession(cookieValue);
            if (user == null) {
                throw new ServerException(ErrorCode.USER_NOT_EXISTS);
            }
        }

        Set<Trip> trips = new HashSet<>(userDao.getAllTrips());
        if (fromStation != null) {
            trips.retainAll(userDao.getTripsFromStation(fromStation));
        }
        if (toStation != null) {
            trips.retainAll(userDao.getTripsToStation(toStation));
        }
        if (busName != null) {
            trips.retainAll(userDao.getTripsByBus(busName));
        }
        if (fromDate != null) {
            trips.retainAll(userDao.getTripsFromDate(LocalDate.parse(fromDate)));
        }
        if (toDate != null) {
            trips.retainAll(userDao.getTripsToDate(LocalDate.parse(toDate)));
        }

        List<GetTripDtoResponse> responseList = new ArrayList<>();
        UserType userType = user.getUserType();

        if (userType == UserType.ADMIN) {
            for (Trip trip : trips) {
                Bus bus = trip.getBus();
                BusDtoResponse busDtoResponse = BusMapperMapstruct.INSTANCE.busToBusDto(bus);

                GetAdminTripDtoResponse getAdminTripDtoResponse = TripMapperMapstruct.INSTANCE.tripToGetAdminTripDtoResponse(trip);

                List<LocalDate> dates = new ArrayList<>();
                for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
                    dates.add(assignedTrip.getDateTrip());
                }
                getAdminTripDtoResponse.setDates(dates);
                getAdminTripDtoResponse.setBus(busDtoResponse);

                Schedule schedule = trip.getSchedule();
                if (schedule != null) {
                    ScheduleDtoResponse scheduleDtoResponse = TripMapperMapstruct.INSTANCE.scheduleToScheduleDtoResponse(schedule);
                    getAdminTripDtoResponse.setScheduleDtoResponse(scheduleDtoResponse);
                }
                responseList.add(getAdminTripDtoResponse);
            }
        }
        else {
            for (Trip trip : trips) {
                if (trip.isApproved()) {
                    Bus bus = trip.getBus();
                    BusDtoResponse busDtoResponse = BusMapperMapstruct.INSTANCE.busToBusDto(bus);

                    GetTripDtoResponse getTripDtoResponse = TripMapperMapstruct.INSTANCE.tripToGetTripDtoResponse(trip);

                    List<LocalDate> dates = new ArrayList<>();
                    for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
                        dates.add(assignedTrip.getDateTrip());
                    }
                    getTripDtoResponse.setDates(dates);
                    getTripDtoResponse.setBus(busDtoResponse);

                    Schedule schedule = trip.getSchedule();
                    if (schedule != null) {
                        ScheduleDtoResponse scheduleDtoResponse = TripMapperMapstruct.INSTANCE.scheduleToScheduleDtoResponse(schedule);
                        getTripDtoResponse.setScheduleDtoResponse(scheduleDtoResponse);
                    }
                    responseList.add(getTripDtoResponse);
                }
            }
        }
        LOGGER.info("successfully get trips with user login " + user.getLogin());

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new GetTripsDtoResponse(responseList));
    }

    @Transactional
    public ResponseEntity<GetOrdersDtoResponse> getOrdersWithParams(String cookieValue, String fromStation, String toStation, String busName, String fromDate, String toDate, String clientId) {
        User user = adminDao.getAdminBySession(cookieValue);
        if (user == null) {
            user = clientDao.getClientBySession(cookieValue);
            if (user == null) {
                throw new ServerException(ErrorCode.USER_NOT_EXISTS);
            }
        }

        Set<Order> orders = new HashSet<>(userDao.getAllOrders());

        if (fromStation != null) {
            orders.retainAll(userDao.getOrdersFromStation(fromStation));
        }
        if (toStation != null) {
            orders.retainAll(userDao.getOrdersToStation(toStation));
        }
        if (busName != null) {
            orders.retainAll(userDao.getOrdersByBus(busName));
        }
        if (fromDate != null) {
            orders.retainAll(userDao.getOrdersFromDate(LocalDate.parse(fromDate)));
        }
        if (toDate != null) {
            orders.retainAll(userDao.getOrdersToDate(LocalDate.parse(toDate)));
        }

        UserType userType = user.getUserType();
        if (userType == UserType.ADMIN) {
            if (clientId != null) {
                try{
                    int id = Integer.parseInt(clientId);
                    orders.retainAll(userDao.getOrdersByClientId(id));
                }
                catch (NumberFormatException ex){
                    throw new ServerException(ErrorCode.WRONG_PARSE_ID);
                }
            }

        }
        else {
            orders.retainAll(userDao.getOrdersByClientId(user.getId()));
        }

        List<GetOrderDtoResponse> responseList = new ArrayList<>();
        for (Order order : orders) {
            GetOrderDtoResponse orderDtoResponse = OrderMapperMapstruct.INSTANCE.orderToGetOrderDtoResponse(order);
            Trip trip = order.getAssignedTrip().getTrip();
            orderDtoResponse.setTotalPrice(trip.getPrice()*order.getPassengers().size());
            responseList.add(orderDtoResponse);
        }

        LOGGER.info("successfully get orders with user login " + user.getLogin());

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(new GetOrdersDtoResponse(responseList));
    }
}
