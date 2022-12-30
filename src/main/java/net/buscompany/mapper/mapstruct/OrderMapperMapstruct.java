package net.buscompany.mapper.mapstruct;

import net.buscompany.dto.request.client.ChoosePlaceDtoRequest;
import net.buscompany.dto.request.client.OrderDtoRequest;
import net.buscompany.dto.request.client.PassengerDtoRequest;
import net.buscompany.dto.response.client.ChoosePlaceDtoResponse;
import net.buscompany.dto.response.client.OrderDtoResponse;
import net.buscompany.dto.response.client.PassengerDtoResponse;
import net.buscompany.dto.response.user.GetOrderDtoResponse;
import net.buscompany.model.Order;
import net.buscompany.model.Passenger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface OrderMapperMapstruct {

    OrderMapperMapstruct INSTANCE = Mappers.getMapper(OrderMapperMapstruct.class);

    @Mapping(target = "id", source = "idTrip", ignore = true)
    @Mapping(target = "assignedTrip", ignore = true)
    @Mapping(target = "passengers", source = "passengers", qualifiedByName = "passengersDtoToPassengers")
    Order orderDtoRequestToOrder(OrderDtoRequest request);

    @Mapping(target = "id", ignore = true)
    Passenger passengerDtoToPassenger(PassengerDtoRequest request);

    PassengerDtoResponse passengerToPassengerDto(Passenger passenger);

    @Mapping(target = "idOrder", source = "id")
    @Mapping(target = "passengers", source = "passengers", qualifiedByName = "passengersToDtoPassengers")
    @Mapping(target = "idTrip", source = "assignedTrip.trip.id")
    @Mapping(target = "fromStation", source = "assignedTrip.trip.fromStation")
    @Mapping(target = "toStation", source = "assignedTrip.trip.toStation")
    @Mapping(target = "busName", source = "assignedTrip.trip.bus.name")
    @Mapping(target = "dateTrip", source = "assignedTrip.dateTrip")
    @Mapping(target = "startTime", source = "assignedTrip.trip.startTime")
    @Mapping(target = "durationInMinutes", source = "assignedTrip.trip.durationInMinutes")
    @Mapping(target = "price", source = "assignedTrip.trip.price")
    OrderDtoResponse orderToOrderDtoResponse(Order order);

    @Mapping(target = "idOrder", source = "id")
    @Mapping(target = "passengers", source = "passengers", qualifiedByName = "passengersToDtoPassengers")
    @Mapping(target = "idTrip", source = "assignedTrip.trip.id")
    @Mapping(target = "fromStation", source = "assignedTrip.trip.fromStation")
    @Mapping(target = "toStation", source = "assignedTrip.trip.toStation")
    @Mapping(target = "busName", source = "assignedTrip.trip.bus.name")
    @Mapping(target = "dateTrip", source = "assignedTrip.dateTrip")
    @Mapping(target = "startTime", source = "assignedTrip.trip.startTime")
    @Mapping(target = "durationInMinutes", source = "assignedTrip.trip.durationInMinutes")
    @Mapping(target = "price", source = "assignedTrip.trip.price")
    GetOrderDtoResponse orderToGetOrderDtoResponse(Order order);

    @Mapping(target = "ticket", ignore = true)
    ChoosePlaceDtoResponse choosePlaceDtoRequestToResponse(ChoosePlaceDtoRequest request);

    @Named("passengersDtoToPassengers")
    default List<Passenger> passengersDtoToPassengers(List<PassengerDtoRequest> requestList) {
        List<Passenger> passengers = new ArrayList<>();
        for (PassengerDtoRequest passengerDtoRequest : requestList) {
            passengers.add(passengerDtoToPassenger(passengerDtoRequest));
        }
        return passengers;
    }

    @Named("passengersToDtoPassengers")
    default List<PassengerDtoResponse> passengersToDtoPassengers(List<Passenger> passengerList) {
        List<PassengerDtoResponse> passengersDto = new ArrayList<>();
        for (Passenger passenger : passengerList) {
            passengersDto.add(passengerToPassengerDto(passenger));
        }
        return passengersDto;
    }

}
