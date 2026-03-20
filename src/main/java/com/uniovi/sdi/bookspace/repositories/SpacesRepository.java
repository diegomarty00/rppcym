package com.uniovi.sdi.bookspace.repositories;

import com.uniovi.sdi.bookspace.entities.Space;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpacesRepository extends CrudRepository<Space, Long> {
    List<Space> findByActiveTrue();

    List<Space> findAll();

    @Query("SELECT r FROM Space r WHERE (LOWER(r.name) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR LOWER(r.type) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Space> searchByNameOrType(Pageable pageable, @Param("busqueda") String searchtext);
}
