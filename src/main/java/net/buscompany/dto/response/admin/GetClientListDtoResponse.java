package net.buscompany.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.buscompany.dto.response.info.GetInfoClientDtoResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetClientListDtoResponse {
    private List<GetInfoClientDtoResponse> clients;
}
