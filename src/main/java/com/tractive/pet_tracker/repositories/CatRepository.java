package com.tractive.pet_tracker.repositories;

import com.tractive.pet_tracker.models.projections.CatCountProjection;
import com.tractive.pet_tracker.models.entities.Cat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CatRepository extends JpaRepository<Cat, Long> {
    @Query(value =
        "SELECT " +
        "new com.tractive.pet_tracker.models.projections.CatCountProjection(c.trackerType, count(c)) " +
        "FROM Cat c " +
        "WHERE c.inZone = FALSE " +
        "GROUP BY c.trackerType"
    )
    List<CatCountProjection> countNotInZone();
}
