package net.buscompany.dto.request.register;

import lombok.*;
import net.buscompany.validator.Name;
import net.buscompany.validator.Password;
import net.buscompany.validator.PhoneNumber;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RegisterClientDtoRequest {

    @NotNull
    private String login;

    @Password
    private String password;

    @Email
    private String email;

    @NotNull
    @PhoneNumber
    private String phoneNumber;

    @NotNull
    @Name
    private String firstName;

    @NotNull
    @Name
    private String lastName;

    @Name
    private String patronymic;
}
