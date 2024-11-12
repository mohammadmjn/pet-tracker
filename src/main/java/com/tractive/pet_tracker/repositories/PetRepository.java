package com.tractive.pet_tracker.repositories;

import com.tractive.pet_tracker.models.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> { }
