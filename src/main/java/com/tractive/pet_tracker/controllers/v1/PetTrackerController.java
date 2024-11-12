package com.tractive.pet_tracker.controllers.v1;

import com.tractive.pet_tracker.models.dtos.PetDto;
import com.tractive.pet_tracker.models.dtos.PetsOutsideZoneDto;
import com.tractive.pet_tracker.services.PetTrackerService;
import com.tractive.pet_tracker.services.PetTrackerServiceImp;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/pet-tracker", produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
public class PetTrackerController {
    private final PetTrackerService petTrackerService;

    @GetMapping(path = "/{id}")
    public PetDto getPetById(@PathVariable("id") long id) {
        return petTrackerService.getPetById(id);
    }

    @GetMapping()
    public Page<PetDto> getAllPets(Pageable pagination) {
        return petTrackerService.getAllPets(pagination);
    }

    @GetMapping(path = "/zone-info")
    public PetsOutsideZoneDto getPetsOutsideZoneCount() {
        return petTrackerService.countPetsOutsideZoneGroupByType();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public PetDto createPet(@RequestBody @Valid PetDto incomingPetDto) {
        return petTrackerService.createPet(incomingPetDto);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public PetDto updatePet(
        @PathVariable("id") long id,
        @RequestBody @Valid PetDto updatePetDto
    ) {
        return petTrackerService.updatePet(id, updatePetDto);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePet(@PathVariable("id") long id) {
        petTrackerService.deletePet(id);
    }
}
