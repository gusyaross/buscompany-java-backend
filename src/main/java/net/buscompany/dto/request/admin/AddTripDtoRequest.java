package net.buscompany.dto.request.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.buscompany.validator.Schedule;
import net.buscompany.validator.Dates;
import net.buscompany.validator.ScheduleOrDates;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ScheduleOrDates
public class AddTripDtoRequest {
    @NotNull
    private String busName;

    @NotNull
    private String fromStation;

    @NotNull
    private String toStation;

    @NotNull
    private LocalTime startTime;

    @Positive
    private int durationInMinutes;

    @Positive
    private int price;

    @Schedule
    private ScheduleDtoRequest scheduleDtoRequest;

    @Dates
    private List<LocalDate> dates;
}
