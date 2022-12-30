package net.buscompany.dao;

import net.buscompany.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AdminDao {
    void insertAdmin(@Param("admin") Admin admin);

    void updateUserType(@Param("admin") Admin admin);

    Admin getAdminByLogin(@Param("login") String login);

    Admin getAdminBySession(@Param("uuid") String uuid);

    List<Client> getAllClients();

    void updateAdmin(@Param("admin") Admin admin);

    void insertBus(@Param("bus") Bus bus);

    List<Bus> getAllBuses();

    void addTrip(@Param("trip") Trip trip);

    void addAssignedTrip(@Param("assignedTrip") AssignedTrip assignedTrip, @Param("trip") Trip trip);

    void addSchedule(@Param("schedule") Schedule schedule, @Param("trip") Trip trip);

    void updateSchedule(@Param("schedule") Schedule schedule, @Param("trip") Trip trip);

    void updateTrip(@Param("trip") Trip trip);

    void deleteAssignedTrips(@Param("trip") Trip trip);

    void addPlace(@Param("assignedTrip") AssignedTrip trip, @Param("placeNumber") int placeNumber);

    void deleteTrip(@Param("trip") Trip trip);

    void approveTrip(@Param("trip") Trip trip);

    void deleteBuses();

    void deleteTrips();

    void deleteUsers();
}
