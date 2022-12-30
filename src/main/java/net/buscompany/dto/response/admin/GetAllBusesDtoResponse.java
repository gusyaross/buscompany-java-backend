package net.buscompany.dto.response.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GetAllBusesDtoResponse {
    List<BusDtoResponse> buses;
}
