package net.buscompany.controller;

import net.buscompany.dto.response.debug.ClearDatabaseDtoResponse;
import net.buscompany.service.DebugService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    private final DebugService debugService;

    public DebugController(DebugService debugService) { this.debugService = debugService; }

    @PostMapping(path = "/clear", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ClearDatabaseDtoResponse> clearDatabase() {
        return debugService.clearDatabase();
    }
}
