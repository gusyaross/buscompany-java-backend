package net.buscompany.dto.request.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.buscompany.validator.Name;
import net.buscompany.validator.Password;
import net.buscompany.validator.PhoneNumber;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateClientInfoDtoRequest {
    @NotNull
    @Name
    private String firstName;

    @NotNull
    @Name
    private String lastName;

    @Name
    private String patronymic;

    @NotNull
    private String oldPassword;

    @NotNull
    @Password
    private String newPassword;

    @NotNull
    @Email
    private String email;

    @NotNull
    @PhoneNumber
    private String phoneNumber;
}
