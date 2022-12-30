package net.buscompany.model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Client extends User {

    private String email;

    private String phoneNumber;

    private List<Order> orders;
}
