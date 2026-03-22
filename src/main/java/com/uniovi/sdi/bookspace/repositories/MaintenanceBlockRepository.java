package com.uniovi.sdi.bookspace.repositories;

import com.uniovi.sdi.bookspace.entities.BlockStatus;
import com.uniovi.sdi.bookspace.entities.MaintenanceBlock;
import com.uniovi.sdi.bookspace.entities.Space;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

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


    @Query("SELECT b FROM MaintenanceBlock b " +
            "WHERE b.space.id = :spaceId " +
            "AND b.status = 'ACTIVE' " +
            "AND b.startDateTime < :endDate " +
            "AND b.endDateTime > :startDate")
    List<MaintenanceBlock> findActiveOverlappingBlocks(
            @Param("spaceId") Long spaceId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}