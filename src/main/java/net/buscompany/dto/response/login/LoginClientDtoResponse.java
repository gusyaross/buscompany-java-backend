package net.buscompany.dto.response.login;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginClientDtoResponse extends LoginUserDtoResponse {
    private String email;
    private String phoneNumber;

    public LoginClientDtoResponse(int id, String firstName, String lastName, String patronymic, String userType, String email, String phoneNumber) {
        super(id, firstName, lastName, patronymic, userType);
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
