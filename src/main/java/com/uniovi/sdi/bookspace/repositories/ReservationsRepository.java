package com.uniovi.sdi.bookspace.repositories;

import com.uniovi.sdi.bookspace.entities.Reservation;
import com.uniovi.sdi.bookspace.entities.ReservationStatus;
import com.uniovi.sdi.bookspace.entities.Space;
import com.uniovi.sdi.bookspace.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationsRepository extends CrudRepository<Reservation, Long> {
    List<Reservation> findByUserOrderByStartDateTimeAsc(User user);

    List<Reservation> findByUserAndStatusOrderByStartDateTimeAsc(User user, ReservationStatus status);

    List<Reservation> findBySpaceAndStatusAndEndDateTimeAfterAndStartDateTimeBefore(
            Space space,
            ReservationStatus status,
            LocalDateTime startExclusive,
            LocalDateTime endExclusive
    );

    long countByUserAndStatusAndEndDateTimeAfter(User user,
                                                 ReservationStatus status,
                                                 LocalDateTime endExclusive);

    @Query("""
            select r
            from Reservation r
            where r.space = :space
              and r.status = com.uniovi.sdi.bookspace.entities.ReservationStatus.ACTIVE
              and r.endDateTime > :from
              and r.startDateTime < :to
            order by r.startDateTime asc
            """)
    List<Reservation> findActiveReservationsInRange(@Param("space") Space space,
                                                    @Param("from") LocalDateTime from,
                                                    @Param("to") LocalDateTime to);

    @Query("""
            select r
            from Reservation r
            where (:spaceId is null or r.space.id = :spaceId)
                  and r.endDateTime > :from
                  and r.startDateTime < :to
                order by r.startDateTime asc
            
            """)
    List<Reservation> findGlobalFiltered(@Param("spaceId") Long spaceId,
                                         @Param("from") LocalDateTime from,
                                         @Param("to") LocalDateTime to);

    default Page<Reservation> findGlobalFilteredPage(Long spaceId,
                                                     LocalDateTime from,
                                                     LocalDateTime to,
                                                     Pageable pageable) {
        List<Reservation> all = findGlobalFiltered(spaceId, from, to);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), all.size());
        List<Reservation> content = start >= all.size() ? List.of() : all.subList(start, end);
        return new org.springframework.data.domain.PageImpl<>(content, pageable, all.size());
    }
}
