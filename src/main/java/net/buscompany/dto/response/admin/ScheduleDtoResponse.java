package net.buscompany.dto.response.admin;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ScheduleDtoResponse {
    private String fromDate;
    private String toDate;

    private String period;
}
