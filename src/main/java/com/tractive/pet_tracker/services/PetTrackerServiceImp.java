package com.tractive.pet_tracker.services;

import com.tractive.pet_tracker.models.dtos.PetDto;
import com.tractive.pet_tracker.models.dtos.PetsOutsideZoneDto;
import com.tractive.pet_tracker.models.entities.Pet;
import com.tractive.pet_tracker.models.enums.CatTrackerType;
import com.tractive.pet_tracker.models.enums.DogTrackerType;
import com.tractive.pet_tracker.models.helpers.PetMapper;
import com.tractive.pet_tracker.repositories.PetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Slf4j
@Service
public class PetTrackerServiceImp implements PetTrackerService {
    private final PetRepository petRepository;
    private final CatTrackerService catTrackerService;
    private final DogTrackerService dogTrackerService;
    private final PetMapper petMapper;

    @Autowired
    public PetTrackerServiceImp(
        PetRepository petRepository,
        CatTrackerService catTrackerService,
        DogTrackerService dogTrackerService,
        PetMapper petMapper
    ) {
        this.petRepository = petRepository;
        this.catTrackerService = catTrackerService;
        this.dogTrackerService = dogTrackerService;
        this.petMapper = petMapper;
    }

    @Override
    public PetDto getPetById(long id) {
        Pet pet = findPetById(id);
        return petMapper.mapEntityToDto(pet);
    }

    @Override
    public Page<PetDto> getAllPets(Pageable pagination) {
        Page<PetDto> petsDtoPage =  petRepository.findAll(pagination).map(petMapper::mapEntityToDto);

        // There a bug with Jackson serializer to include @JsonTypeInfo in Page case.
        // To solve it, I used this workaround to have petType in paginated JSON.
        return new PageImpl<>(petsDtoPage.getContent(), pagination, petsDtoPage.getTotalElements()) {};
    }

    @Override
    public PetsOutsideZoneDto countPetsOutsideZoneGroupByType() {
        Map<CatTrackerType, Long> catsOutsideZone = catTrackerService.countCatsOutsideZone();
        Map<DogTrackerType, Long> dogsOutsideZone = dogTrackerService.countDogsOutsideZone();

        return new PetsOutsideZoneDto(
            catsOutsideZone,
            dogsOutsideZone
        );
    }

    @Override
    public PetDto createPet(PetDto incomingPet) {
        Pet pet = petRepository.save(petMapper.mapDtoToEntity(incomingPet));
        log.info("Created pet {}", pet);

        return petMapper.mapEntityToDto(pet);
    }

    @Override
    public PetDto updatePet(long id, PetDto updatedPet) {
        var pet = updatePetEntry(updatedPet, findPetById(id));
        return petMapper.mapEntityToDto(pet);
    }

    @Override
    public void deletePet(Long id) {
        petRepository.delete(findPetById(id));
    }

    /**
     * Retrieve a Pet using ID
     *
     * @param id The id of the required Pet
     * @return Pet
     *
     * @throws ResponseStatusException if the pet is not found
     */
    private Pet findPetById(long id) {
        return petRepository.findById(id).orElseThrow(() -> {
            String message = String.format("Pet %s not found", id);
            log.info(message);
            return new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    message
            );
        });
    }

    /**
     * Retrieve a Pet using ID
     *
     * @param updatedPet The DTO of the updated Pet data
     * @param existingPet The Pet entry already exists in DB
     * @return Pet
     *
     * @throws ResponseStatusException if the pet is not found
     */
    private Pet updatePetEntry(PetDto updatedPet, Pet existingPet) {
        try {
            petMapper.mapDtoToEntity(updatedPet, existingPet);
            return petRepository.save(existingPet);
        } catch (IllegalArgumentException e) {
            log.error("Error updating pet {}", existingPet.getId(), e);
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Pet cannot get updated"
            );
        }
    }
}
