package net.buscompany.dao;

import net.buscompany.model.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Mapper
@Repository
public interface UserDao {
    void insertUser(@Param("user") User user);

    void insertSession(@Param("user") User user, @Param("uuid") String uuid);

    void deleteSession(@Param("uuid") String uuid);

    int getAdminCount();

    void deleteUser(@Param("user") User user);

    void updateUser(@Param("user") User user);

    Bus getBusByName(@Param("name") String busName);

    Trip getTripById(@Param("tripId") int tripId);

    List<Trip> getTripsFromStation(@Param("fromStation") String fromStation);

    List<Trip> getTripsToStation(@Param("toStation") String toStation);

    List<Trip> getTripsByBus(@Param("busName") String busName);

    List<Trip> getTripsFromDate(@Param("fromDate") LocalDate fromDate);

    List<Trip> getTripsToDate(@Param("toDate") LocalDate toDate);

    List<Trip> getAllTrips();

    Order getOrderById(@Param("orderId") int orderId);

    List<Order> getOrdersByClientId(@Param("clientId") int clientId);

    Order getOrderByIdAndClientId(@Param("orderId") int orderId, @Param("clientId") int clientId);

    List<Order> getOrdersFromStation(@Param("fromStation") String fromStation);

    List<Order> getOrdersToStation(@Param("toStation") String toStation);

    List<Order> getOrdersByBus(@Param("busName") String busName);

    List<Order> getOrdersFromDate(@Param("fromDate") LocalDate fromDate);

    List<Order> getOrdersToDate(@Param("toDate") LocalDate toDate);

    List<Order> getAllOrders();

    Passenger getPassengerByPlaceNumber(@Param("placeNumber") int numberPlace, @Param("assignedTrip") AssignedTrip assignedTrip);
}
