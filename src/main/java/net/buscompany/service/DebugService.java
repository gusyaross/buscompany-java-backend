package net.buscompany.service;

import lombok.AllArgsConstructor;
import net.buscompany.dto.response.debug.ClearDatabaseDtoResponse;
import net.buscompany.dao.AdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DebugService {
    private final Logger LOGGER = LoggerFactory.getLogger(DebugService.class);

    private final AdminDao adminDao;

    @Transactional
    public ResponseEntity<ClearDatabaseDtoResponse> clearDatabase() {
        adminDao.deleteUsers();
        adminDao.deleteTrips();
        adminDao.deleteBuses();

        LOGGER.debug("Clear database");

        return ResponseEntity.ok().body(new ClearDatabaseDtoResponse());
    }
}
