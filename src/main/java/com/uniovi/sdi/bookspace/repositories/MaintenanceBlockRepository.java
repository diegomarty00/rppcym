package com.uniovi.sdi.bookspace.repositories;

import com.uniovi.sdi.bookspace.entities.BlockStatus;
import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Space;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MaintenanceBlockRepository extends CrudRepository<MaintenanceBlock, Long> {
    List<MaintenanceBlock> findBySpaceAndStatusAndEndDateTimeAfterAndStartDateTimeBefore(
            Space space,
            BlockStatus status,
            LocalDateTime startExclusive,
            LocalDateTime endExclusive
    );

    List<MaintenanceBlock> findBySpaceOrderByStartDateTimeAsc(Space space);
}