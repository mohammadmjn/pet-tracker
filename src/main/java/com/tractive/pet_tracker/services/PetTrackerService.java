package com.tractive.pet_tracker.services;

import com.tractive.pet_tracker.models.dtos.PetDto;
import com.tractive.pet_tracker.models.dtos.PetsOutsideZoneDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PetTrackerService {
    PetDto getPetById(long id);
    Page<PetDto> getAllPets(Pageable pagination);
    PetsOutsideZoneDto countPetsOutsideZoneGroupByType();
    PetDto createPet(PetDto petDto);
    PetDto updatePet(long id, PetDto updatedPet);
    void deletePet(Long id);
}
