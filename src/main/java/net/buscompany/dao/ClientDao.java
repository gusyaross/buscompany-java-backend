package net.buscompany.dao;

import net.buscompany.model.AssignedTrip;
import net.buscompany.model.Client;
import net.buscompany.model.Order;
import net.buscompany.model.Passenger;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ClientDao {
    void insertClient(@Param("client") Client client);

    void updateUserType(@Param("client") Client client);

    Client getClientByLogin(@Param("login") String login);

    Client getClientBySession(@Param("uuid") String uuid);

    void updateClient(@Param("client") Client client);

    void insertOrder(@Param("order") Order order, @Param("client") Client client);

    List<Integer> getFreePlacesList(@Param("assignedTrip") AssignedTrip assignedTrip);

    int updateFreePlacesCount(@Param("assignedTrip") AssignedTrip assignedTrip, @Param("passengerCount") int passengerCount);

    void insertPassenger(@Param("passenger") Passenger passenger, @Param("order") Order order);

    int updatePlaceWithPassenger(@Param("passenger") Passenger passenger,
                                  @Param("assignedTrip") AssignedTrip assignedTrip,
                                  @Param("placeNumber") Integer placeNumber);

    void deleteOrder(@Param("order") Order order);

    void cancelPassengerPlace(@Param("passenger") Passenger passenger, @Param("assignedTrip") AssignedTrip assignedTrip);
}
