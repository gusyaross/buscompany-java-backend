package net.buscompany.mapper.mapstruct;

import net.buscompany.dto.response.admin.*;
import net.buscompany.model.Schedule;
import net.buscompany.model.Trip;
import net.buscompany.dto.request.admin.AddTripDtoRequest;
import net.buscompany.dto.request.admin.ScheduleDtoRequest;
import net.buscompany.dto.response.user.GetAdminTripDtoResponse;
import net.buscompany.dto.response.user.GetTripDtoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TripMapperMapstruct {

    TripMapperMapstruct INSTANCE = Mappers.getMapper(TripMapperMapstruct.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "assignedTrips",ignore = true)
    Trip tripDtoRequestToTrip(AddTripDtoRequest request);

    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "scheduleDtoResponse", source = "schedule", ignore = true)
    AddTripDtoResponse tripToAddTripDtoResponse(Trip trip);

    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "scheduleDtoResponse", source = "schedule", ignore = true)
    UpdateTripDtoResponse tripToUpdateTripDtoResponse(Trip trip);

    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "scheduleDtoResponse", source = "schedule", ignore = true)
    GetTripInfoDtoResponse tripToGetTripInfoDtoResponse(Trip trip);

    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "scheduleDtoResponse", source = "schedule", ignore = true)
    ApproveTripDtoResponse tripToApproveTripDtoResponse(Trip trip);

    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "scheduleDtoResponse", source = "schedule", ignore = true)
    GetAdminTripDtoResponse tripToGetAdminTripDtoResponse(Trip trip);

    @Mapping(target = "bus", ignore = true)
    @Mapping(target = "scheduleDtoResponse", source = "schedule", ignore = true)
    GetTripDtoResponse tripToGetTripDtoResponse(Trip trip);


    ScheduleDtoResponse scheduleToScheduleDtoResponse(Schedule schedule);

    Schedule scheduleDtoRequestToSchedule(ScheduleDtoRequest request);
}
