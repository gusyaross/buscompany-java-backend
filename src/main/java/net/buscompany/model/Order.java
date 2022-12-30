package net.buscompany.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Order {
    private int id;

    private AssignedTrip assignedTrip;

    private List<Passenger> passengers;
}
