package net.buscompany.service;

import lombok.AllArgsConstructor;
import net.buscompany.dao.AdminDao;
import net.buscompany.dao.UserDao;
import net.buscompany.dto.request.admin.AddBusDtoRequest;
import net.buscompany.dto.request.admin.AddTripDtoRequest;
import net.buscompany.dto.request.admin.UpdateTripDtoRequest;
import net.buscompany.dto.request.register.RegisterAdminDtoRequest;
import net.buscompany.dto.request.update.UpdateAdminInfoDtoRequest;
import net.buscompany.dto.response.admin.*;
import net.buscompany.model.*;
import net.buscompany.dto.response.info.GetInfoClientDtoResponse;
import net.buscompany.dto.response.register.RegisterAdminDtoResponse;
import net.buscompany.dto.response.update.UpdateAdminInfoDtoResponse;
import net.buscompany.exception.ErrorCode;
import net.buscompany.exception.ServerException;
import net.buscompany.mapper.mapstruct.AdminMapperMapstruct;
import net.buscompany.mapper.mapstruct.BusMapperMapstruct;
import net.buscompany.mapper.mapstruct.ClientMapperMapstruct;
import net.buscompany.mapper.mapstruct.TripMapperMapstruct;
import net.buscompany.utils.ServiceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminService {

    private final Logger LOGGER = LoggerFactory.getLogger(AdminService.class);

    private final AdminDao adminDao;
    private final UserDao userDao;

    private final ServiceUtils serviceUtils;

    @Transactional
    public ResponseEntity<RegisterAdminDtoResponse> registerAdmin(RegisterAdminDtoRequest request) {
        Admin admin = AdminMapperMapstruct.INSTANCE.registerAdminDtoToAdmin(request);
        admin.setUserType(UserType.ADMIN);

        try {
            userDao.insertUser(admin);
        } catch (DuplicateKeyException e) {
            throw new ServerException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String uuid = UUID.randomUUID().toString();
        userDao.insertSession(admin, uuid);

        adminDao.insertAdmin(admin);
        adminDao.updateUserType(admin);

        ResponseCookie cookie = serviceUtils.createResponseCookie(uuid);

        RegisterAdminDtoResponse response = AdminMapperMapstruct.INSTANCE.adminToRegisterAdminDto(admin);

        LOGGER.info("Admin with login " + admin.getLogin() + " successfully registered");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<GetClientListDtoResponse> getClientList(String cookieValue) {
        if (adminDao.getAdminBySession(cookieValue) == null) {
            throw new ServerException(ErrorCode.PERMISSION_DENIED);
        }

        List<GetInfoClientDtoResponse> clientsDto = new ArrayList<>();
        List<Client> clients = adminDao.getAllClients();

        for (Client client : clients) {
            clientsDto.add(ClientMapperMapstruct.INSTANCE.clientToClientInfoDto(client));
        }

        GetClientListDtoResponse response = new GetClientListDtoResponse(clientsDto);

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<UpdateAdminInfoDtoResponse> updateAdminInfo(String cookieValue, UpdateAdminInfoDtoRequest request) {
        Admin admin = adminDao.getAdminBySession(cookieValue);

        if (admin == null) {
            throw new ServerException(ErrorCode.USER_NOT_EXISTS);
        }

        if (!admin.getPassword().equals(request.getOldPassword())) {
            throw new ServerException(ErrorCode.INVALID_PASSWORD);
        }

        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setPatronymic(request.getPatronymic());
        admin.setPassword(request.getNewPassword());
        admin.setPosition(request.getPosition());

        userDao.updateUser(admin);
        adminDao.updateAdmin(admin);

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        UpdateAdminInfoDtoResponse response = AdminMapperMapstruct.INSTANCE.adminToUpdateAdminDto(admin);

        LOGGER.info("Admin with login " + admin.getLogin() + " successfully update info");

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<BusDtoResponse> addBus(String cookieValue, AddBusDtoRequest request) {
        if (adminDao.getAdminBySession(cookieValue) == null) {
            throw new ServerException(ErrorCode.PERMISSION_DENIED);
        }

        Bus bus = BusMapperMapstruct.INSTANCE.busDtoToBus(request);

        adminDao.insertBus(bus);

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        BusDtoResponse response = BusMapperMapstruct.INSTANCE.busToBusDto(bus);

        LOGGER.info("successfully add bus with name " + bus.getName());

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<GetAllBusesDtoResponse> getBusList(String cookieValue) {
        if (adminDao.getAdminBySession(cookieValue) == null) {
            throw new ServerException(ErrorCode.PERMISSION_DENIED);
        }

        List<BusDtoResponse> listResponse = new ArrayList<>();
        List<Bus> buses = adminDao.getAllBuses();

        for (Bus bus : buses) {
            listResponse.add(BusMapperMapstruct.INSTANCE.busToBusDto(bus));
        }

         ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        GetAllBusesDtoResponse response = new GetAllBusesDtoResponse(listResponse);

         return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<AddTripDtoResponse> addTrip(String cookieValue, AddTripDtoRequest request) {
        if (adminDao.getAdminBySession(cookieValue) == null) {
            throw new ServerException(ErrorCode.PERMISSION_DENIED);
        }

        Bus bus = userDao.getBusByName(request.getBusName());
        if (bus == null) {
            throw new ServerException(ErrorCode.BUS_NOT_FOUND);
        }

        Trip trip = TripMapperMapstruct.INSTANCE.tripDtoRequestToTrip(request);
        trip.setBus(bus);

        AddTripDtoResponse response;
        BusDtoResponse busDtoResponse = BusMapperMapstruct.INSTANCE.busToBusDto(bus);
        List<AssignedTrip> assignedTrips = new ArrayList<>();

        if (request.getScheduleDtoRequest() != null) {
            Schedule schedule = TripMapperMapstruct.INSTANCE.scheduleDtoRequestToSchedule(request.getScheduleDtoRequest());

            List<LocalDate> dates = serviceUtils.createDatesFromSchedule(schedule);
            for (LocalDate date : dates) {
                assignedTrips.add(new AssignedTrip(trip, date, trip.getBus().getPlaceCount()));
            }

            trip.setAssignedTrips(assignedTrips);

            adminDao.addTrip(trip);
            adminDao.addSchedule(schedule, trip);

            for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
                adminDao.addAssignedTrip(assignedTrip, trip);
            }

            response = TripMapperMapstruct.INSTANCE.tripToAddTripDtoResponse(trip);
            ScheduleDtoResponse scheduleDtoResponse = TripMapperMapstruct.INSTANCE.scheduleToScheduleDtoResponse(schedule);

            response.setBus(busDtoResponse);
            response.setScheduleDtoResponse(scheduleDtoResponse);
            response.setDates(dates);
        }
        else {
            List<LocalDate> dates = request.getDates();

            for (LocalDate date : dates) {
                assignedTrips.add(new AssignedTrip(trip, date, trip.getBus().getPlaceCount()));
            }

            trip.setAssignedTrips(assignedTrips);

            adminDao.addTrip(trip);
            for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
                adminDao.addAssignedTrip(assignedTrip, trip);
            }

            response = TripMapperMapstruct.INSTANCE.tripToAddTripDtoResponse(trip);
            response.setBus(busDtoResponse);
            response.setDates(request.getDates());
        }

        for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
            for (int placeNumber = 0; placeNumber < bus.getPlaceCount(); placeNumber++) {
                adminDao.addPlace(assignedTrip, placeNumber);
            }
        }

        LOGGER.info("successfully add trip with bus " + bus.getName());

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<UpdateTripDtoResponse> updateTrip(String cookieValue, int tripId, UpdateTripDtoRequest request) {
        if (adminDao.getAdminBySession(cookieValue) == null) {
            throw new ServerException(ErrorCode.PERMISSION_DENIED);
        }

        Trip trip = userDao.getTripById(tripId);

        if (trip == null) {
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        Bus bus = userDao.getBusByName(request.getBusName());
        if (bus == null) {
            throw new ServerException(ErrorCode.BUS_NOT_FOUND);
        }

        if (trip.isApproved()) {
            throw new ServerException(ErrorCode.TRIP_ALREADY_APPROVED);
        }

        trip.setBus(bus);
        trip.setFromStation(request.getFromStation());
        trip.setToStation(request.getToStation());
        trip.setStartTime(request.getStartTime());
        trip.setDurationInMinutes(request.getDurationInMinutes());
        trip.setPrice(request.getPrice());

        adminDao.deleteAssignedTrips(trip);

        List<AssignedTrip> assignedTrips = new ArrayList<>();

        UpdateTripDtoResponse response;
        BusDtoResponse busDtoResponse = BusMapperMapstruct.INSTANCE.busToBusDto(bus);

        if (request.getDates() != null) {
            List<LocalDate> dates = request.getDates();

            for (LocalDate date : dates) {
                assignedTrips.add(new AssignedTrip(trip, date, trip.getBus().getPlaceCount()));
            }

            trip.setAssignedTrips(assignedTrips);

            for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
                adminDao.addAssignedTrip(assignedTrip, trip);
            }

            response = TripMapperMapstruct.INSTANCE.tripToUpdateTripDtoResponse(trip);
            response.setDates(request.getDates());
            response.setBus(busDtoResponse);
        }
        else {
            Schedule schedule = TripMapperMapstruct.INSTANCE.scheduleDtoRequestToSchedule(request.getScheduleDtoRequest());

            List<LocalDate> dates = serviceUtils.createDatesFromSchedule(schedule);

            for (LocalDate date : dates) {
                assignedTrips.add(new AssignedTrip(trip, date, trip.getBus().getPlaceCount()));
            }

            trip.setAssignedTrips(assignedTrips);

            for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
                adminDao.addAssignedTrip(assignedTrip, trip);
            }

            adminDao.updateSchedule(schedule, trip);

            ScheduleDtoResponse scheduleDtoResponse = TripMapperMapstruct.INSTANCE.scheduleToScheduleDtoResponse(schedule);

            response = TripMapperMapstruct.INSTANCE.tripToUpdateTripDtoResponse(trip);
            response.setScheduleDtoResponse(scheduleDtoResponse);
            response.setBus(busDtoResponse);
            response.setDates(request.getDates());
        }

        adminDao.updateTrip(trip);

        LOGGER.info("successfully update trip with bus " + bus.getName());

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<DeleteTripDtoResponse> deleteTrip(String cookieValue, int tripId) {
        if (adminDao.getAdminBySession(cookieValue) == null) {
            throw new ServerException(ErrorCode.PERMISSION_DENIED);
        }

        Trip trip = userDao.getTripById(tripId);

        if (trip == null) {
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        adminDao.deleteTrip(trip);

        LOGGER.info("successfully delete trip with bus " + trip.getBus().getName());

        return ResponseEntity.ok().body(new DeleteTripDtoResponse());
    }

    @Transactional
    public ResponseEntity<GetTripInfoDtoResponse> getTripInfo(String cookieValue, int tripId) {
        if (adminDao.getAdminBySession(cookieValue) == null) {
            throw new ServerException(ErrorCode.PERMISSION_DENIED);
        }

        Trip trip = userDao.getTripById(tripId);

        if (trip == null) {
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        Bus bus = trip.getBus();
        BusDtoResponse busDtoResponse = BusMapperMapstruct.INSTANCE.busToBusDto(bus);

        GetTripInfoDtoResponse response = TripMapperMapstruct.INSTANCE.tripToGetTripInfoDtoResponse(trip);
        response.setBus(busDtoResponse);

        List<LocalDate> dates = new ArrayList<>();
        for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
            dates.add(assignedTrip.getDateTrip());
        }
        response.setDates(dates);

        Schedule schedule = trip.getSchedule();

        if (schedule != null) {
            ScheduleDtoResponse scheduleDtoResponse = TripMapperMapstruct.INSTANCE.scheduleToScheduleDtoResponse(schedule);
            response.setScheduleDtoResponse(scheduleDtoResponse);
        }

        LOGGER.info("successfully get trip info with bus " + bus.getName());

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }

    @Transactional
    public ResponseEntity<ApproveTripDtoResponse> approveTrip(String cookieValue, int tripId) {
        if (adminDao.getAdminBySession(cookieValue) == null) {
            throw new ServerException(ErrorCode.PERMISSION_DENIED);
        }

        Trip trip = userDao.getTripById(tripId);

        if (trip == null) {
            throw new ServerException(ErrorCode.TRIP_NOT_EXISTS);
        }

        trip.setApproved(true);

        adminDao.approveTrip(trip);

        Bus bus = trip.getBus();
        BusDtoResponse busDtoResponse = BusMapperMapstruct.INSTANCE.busToBusDto(bus);

        ApproveTripDtoResponse response = TripMapperMapstruct.INSTANCE.tripToApproveTripDtoResponse(trip);
        response.setBus(busDtoResponse);

        List<LocalDate> dates = new ArrayList<>();
        for (AssignedTrip assignedTrip : trip.getAssignedTrips()) {
            dates.add(assignedTrip.getDateTrip());
        }
        response.setDates(dates);

        Schedule schedule = trip.getSchedule();

        if (schedule != null) {
            ScheduleDtoResponse scheduleDtoResponse = TripMapperMapstruct.INSTANCE.scheduleToScheduleDtoResponse(schedule);
            response.setScheduleDtoResponse(scheduleDtoResponse);
        }

        LOGGER.info("successfully approved trip with bus " + bus.getName());

        ResponseCookie cookie = serviceUtils.createResponseCookie(cookieValue);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }
}
