package net.buscompany.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.buscompany.dto.response.admin.BusDtoResponse;
import net.buscompany.dto.response.admin.ScheduleDtoResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetTripDtoResponse {
    private int id;

    private BusDtoResponse bus;

    private String fromStation;
    private String toStation;

    private LocalTime startTime;

    private int durationInMinutes;

    private int price;

    private ScheduleDtoResponse scheduleDtoResponse;

    private List<LocalDate> dates;
}
