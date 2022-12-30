package net.buscompany.model;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public abstract class User {
    private int id;

    private String login;

    private String password;

    private UserType userType;

    private String firstName;

    private String lastName;

    private String patronymic;

}
