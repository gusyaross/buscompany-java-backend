package net.buscompany.dto.response.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class GetInfoAdminDtoResponse extends GetInfoUserDtoResponse{
    private String position;

    public GetInfoAdminDtoResponse(int id, String firstName, String lastName, String patronymic, String userType, String position) {
        super(id, firstName, lastName, patronymic, userType);
        this.position = position;
    }
}
