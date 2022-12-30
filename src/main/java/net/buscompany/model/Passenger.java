package net.buscompany.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Passenger {
    private int id;

    private String lastName;
    private String firstName;

    private String passport;
}
