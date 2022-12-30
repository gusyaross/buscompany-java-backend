package net.buscompany.dto.response.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.buscompany.dto.response.admin.BusDtoResponse;
import net.buscompany.dto.response.admin.ScheduleDtoResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class GetAdminTripDtoResponse extends GetTripDtoResponse {
    private boolean approved;

    public GetAdminTripDtoResponse(int id, BusDtoResponse busDtoResponse, String fromStation, String toStation,
                                   LocalTime startTime, int durationInMinutes, int price, ScheduleDtoResponse scheduleDtoResponse,
                                   List<LocalDate> dates, boolean approved) {

        super(id,busDtoResponse,fromStation,toStation,startTime,durationInMinutes,price,scheduleDtoResponse,dates);
        this.approved = approved;
    }
}
