package net.buscompany.utils;

import net.buscompany.Cookies;
import net.buscompany.model.Schedule;
import net.buscompany.exception.ErrorCode;
import net.buscompany.exception.ServerException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class ServiceUtils {

    @Value("${user_idle_timeout}")
    private int cookieMaxAge;

    private final Map<String, DayOfWeek> dayOfWeekMap = new HashMap<>();
    {
        dayOfWeekMap.put("Mon",DayOfWeek.MONDAY);
        dayOfWeekMap.put("Tue",DayOfWeek.TUESDAY);
        dayOfWeekMap.put("Wen",DayOfWeek.WEDNESDAY);
        dayOfWeekMap.put("Thu",DayOfWeek.THURSDAY);
        dayOfWeekMap.put("Fri",DayOfWeek.FRIDAY);
        dayOfWeekMap.put("Sat",DayOfWeek.SATURDAY);
        dayOfWeekMap.put("Sun",DayOfWeek.SUNDAY);
    }

    public ResponseCookie createResponseCookie(String cookieValue) {
        return ResponseCookie.from(Cookies.COOKIE_NAME, cookieValue).maxAge(cookieMaxAge).build();
    }

    public List<LocalDate> createDatesFromSchedule(Schedule schedule) {
        List<LocalDate> dates = new ArrayList<>();

        switch (schedule.getPeriod()) {
            case "daily":
                for (LocalDate date = schedule.getFromDate(); date.isBefore(schedule.getToDate());
                     date = date.plus(1, ChronoUnit.DAYS)) {
                    dates.add(date);
                }
                break;
            case "odd":
                for (LocalDate date = schedule.getFromDate(); date.isBefore(schedule.getToDate());
                     date = date.plus(1, ChronoUnit.DAYS)) {

                    if (date.getDayOfMonth() % 2 == 1)
                        dates.add(date);
                }
                break;
            case "even":
                for (LocalDate date = schedule.getFromDate(); date.isBefore(schedule.getToDate());
                     date = date.plus(1, ChronoUnit.DAYS)) {

                    if (date.getDayOfMonth() % 2 == 0)
                        dates.add(date);
                }
                break;
        }

        if (!dates.isEmpty()) {
            return dates;
        }

        Set<String> daysPeriod = Set.of(schedule.getPeriod().replaceAll("\\s+","").split(","));

        if (daysPeriod.stream().allMatch(str -> dayOfWeekMap.get(str) != null)) {
            for (LocalDate date = schedule.getFromDate(); date.isBefore(schedule.getToDate());
                 date = date.plus(1, ChronoUnit.DAYS)) {

                if (checkDayOfWeek(date, daysPeriod))
                    dates.add(date);
            }
        }
        else {
            List<Integer> daysNumber = new ArrayList<>();

            for (String day : daysPeriod) {
                daysNumber.add(Integer.parseInt(day));
            }

            for (LocalDate date = schedule.getFromDate(); date.isBefore(schedule.getToDate());
                 date = date.plus(1, ChronoUnit.DAYS)) {

                if (daysNumber.contains(date.getDayOfMonth())) {
                    dates.add(date);
                }
            }
        }

        if (dates.isEmpty()) {
            throw new ServerException(ErrorCode.ERROR_CREATE_DATES);
        }

        return dates;
    }

    private boolean checkDayOfWeek(LocalDate date, Set<String> days) {
        for (String day : days) {
            DayOfWeek dayOfWeek = dayOfWeekMap.get(day);
            if (date.getDayOfWeek().equals(dayOfWeek)) {
                return true;
            }
        }
        return false;
    }
}
