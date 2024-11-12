package com.tractive.pet_tracker.repositories;

import com.tractive.pet_tracker.models.projections.DogCountProjection;
import com.tractive.pet_tracker.models.entities.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {
    @Query(value =
            "SELECT " +
            "new com.tractive.pet_tracker.models.projections.DogCountProjection(d.trackerType, count(d)) " +
            "FROM Dog d " +
            "WHERE d.inZone = FALSE " +
            "GROUP BY d.trackerType"
    )
    List<DogCountProjection> countNotInZone();
}
