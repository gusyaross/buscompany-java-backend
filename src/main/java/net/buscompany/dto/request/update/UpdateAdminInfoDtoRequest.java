package net.buscompany.dto.request.update;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.buscompany.validator.Name;
import net.buscompany.validator.Password;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateAdminInfoDtoRequest {
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
    private String position;
}
