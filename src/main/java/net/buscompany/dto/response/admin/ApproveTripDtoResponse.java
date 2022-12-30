package net.buscompany.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApproveTripDtoResponse {
    private int id;

    private BusDtoResponse bus;

    private String fromStation;
    private String toStation;

    private LocalTime startTime;

    private int durationInMinutes;

    private boolean approved;

    private int price;

    private ScheduleDtoResponse scheduleDtoResponse;

    private List<LocalDate> dates;
}
