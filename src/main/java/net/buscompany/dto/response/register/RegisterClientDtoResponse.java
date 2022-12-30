package net.buscompany.dto.response.register;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterClientDtoResponse {
    private int id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private String userType;
    private String email;
    private String phoneNumber;
}
