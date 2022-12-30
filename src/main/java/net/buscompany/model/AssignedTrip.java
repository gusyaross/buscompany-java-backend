package net.buscompany.model;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AssignedTrip {
    private int id;

    private Trip trip;

    private LocalDate dateTrip;

    private int freePlaces;

    private List<Place> places;

    public AssignedTrip(Trip trip, LocalDate date, int placeCount) {
        this.trip = trip;
        this.dateTrip = date;
        this.freePlaces = placeCount;
    }
}
