package com.tractive.pet_tracker.services;

import com.tractive.pet_tracker.models.dtos.CatDto;
import com.tractive.pet_tracker.models.dtos.DogDto;
import com.tractive.pet_tracker.models.dtos.PetDto;
import com.tractive.pet_tracker.models.dtos.PetsOutsideZoneDto;
import com.tractive.pet_tracker.models.entities.Cat;
import com.tractive.pet_tracker.models.entities.Dog;
import com.tractive.pet_tracker.models.entities.Pet;
import com.tractive.pet_tracker.models.enums.CatTrackerType;
import com.tractive.pet_tracker.models.enums.DogTrackerType;
import com.tractive.pet_tracker.models.helpers.PetMapper;
import com.tractive.pet_tracker.repositories.PetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
@Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS)
public class PetTrackerServiceImpTest {

//    private static final Logger logger = LoggerFactory.getLogger(PetTrackerServiceImpTest.class);

    @MockBean
    private PetRepository petRepository;

    @MockBean
    private CatTrackerService catTrackerService;

    @MockBean
    private DogTrackerService dogTrackerService;

    @MockBean
    private PetMapper petMapper;

    @Autowired
    private PetTrackerServiceImp petTrackerService;

    @Nested
    @DisplayName("getPetById")
    class GetPetByIdTests {

        @Test
        @DisplayName("Should return PetDto when pet exists")
        void shouldReturnPetDtoWhenPetExists() {
            long petId = 1L;
            Pet pet = Cat.builder()
                    .id(petId)
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(CatTrackerType.BIG)
                    .lostTracker(false)
                    .build();

            PetDto petDto = CatDto.builder()
                    .id(petId)
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(CatTrackerType.BIG)
                    .lostTracker(false)
                    .build();

            when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
            when(petMapper.mapEntityToDto(pet)).thenReturn(petDto);

            PetDto result = petTrackerService.getPetById(petId);

            assertNotNull(result);
            assertEquals(petId, result.getId());
            assertEquals(101L, result.getOwnerId());
            assertTrue(result.getInZone());
            assertInstanceOf(CatDto.class, result);
            CatDto catDto = (CatDto) result;
            assertEquals(CatTrackerType.BIG, catDto.getTrackerType());

            verify(petRepository, times(1)).findById(petId);
            verify(petMapper, times(1)).mapEntityToDto(pet);
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when pet does not exist")
        void shouldThrowExceptionWhenPetDoesNotExist() {
            long petId = 1000L;
            when(petRepository.findById(petId)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                petTrackerService.getPetById(petId);
            }, "Expected getPetById to throw ResponseStatusException");

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertTrue(Objects.requireNonNull(exception.getReason()).contains("Pet 1000 not found"));

            verify(petRepository, times(1)).findById(petId);
            verify(petMapper, never()).mapEntityToDto(any());
        }
    }

    @Nested
    @DisplayName("getAllPets")
    class GetAllPetsTests {

        @Test
        @DisplayName("Should return paginated list of PetDto")
        void shouldReturnPaginatedListOfPetDto() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());

            Pet firstPet = Cat.builder()
                    .id(1L)
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(CatTrackerType.SMALL)
                    .lostTracker(false)
                    .build();

            Pet secondPet = Dog.builder()
                    .id(2L)
                    .ownerId(102L)
                    .inZone(false)
                    .trackerType(DogTrackerType.BIG)
                    .build();

            PetDto firstPetDto = CatDto.builder()
                    .id(1L)
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(CatTrackerType.SMALL)
                    .lostTracker(false)
                    .build();

            PetDto secondPetDto = DogDto.builder()
                    .id(2L)
                    .ownerId(102L)
                    .inZone(false)
                    .trackerType(DogTrackerType.BIG)
                    .build();

            Page<Pet> paginatedPets = new PageImpl<>(List.of(firstPet, secondPet), pageable, 2);

            when(petRepository.findAll(pageable)).thenReturn(paginatedPets);
            when(petMapper.mapEntityToDto(firstPet)).thenReturn(firstPetDto);
            when(petMapper.mapEntityToDto(secondPet)).thenReturn(secondPetDto);

            Page<PetDto> result = petTrackerService.getAllPets(pageable);

            assertNotNull(result);
            assertEquals(2, result.getTotalElements());
            assertEquals(1, result.getTotalPages());
            assertEquals(0, result.getNumber());
            assertEquals(10, result.getSize());

            List<PetDto> content = result.getContent();
            assertEquals(2, content.size());
            assertEquals(firstPetDto, content.get(0));
            assertEquals(secondPetDto, content.get(1));

            verify(petRepository, times(1)).findAll(pageable);
            verify(petMapper, times(1)).mapEntityToDto(firstPet);
            verify(petMapper, times(1)).mapEntityToDto(secondPet);
        }

        @Test
        @DisplayName("Should return empty page when no pets exist")
        void shouldReturnEmptyPageWhenNoPetsExist() {
            Pageable pageable = PageRequest.of(0, 10, Sort.by("id").ascending());
            Page<Pet> emptyPage = Page.empty(pageable);

            when(petRepository.findAll(pageable)).thenReturn(emptyPage);

            Page<PetDto> result = petTrackerService.getAllPets(pageable);

            assertNotNull(result, "Expected non-null Page of PetDto");
            assertEquals(0, result.getTotalElements(), "Total elements should be 0");
            assertEquals(0, result.getTotalPages(), "Total pages should be 0");
            assertEquals(0, result.getNumber(), "Page number should be 0");
            assertEquals(10, result.getSize(), "Page size should be 10");
            assertTrue(result.getContent().isEmpty(), "Content should be empty");

            verify(petRepository, times(1)).findAll(pageable);
            verify(petMapper, never()).mapEntityToDto(any());
        }
    }

    @Nested
    @DisplayName("countPetsOutsideZoneGroupByType")
    class CountPetsOutsideZoneGroupByTypeTests {

        @Test
        @DisplayName("Should return PetsOutsideZoneDto with counts")
        void shouldReturnPetsOutsideZoneDtoWithCounts() {
            Map<CatTrackerType, Long> catsOutsideZone = new HashMap<>();
            catsOutsideZone.put(CatTrackerType.SMALL, 5L);
            catsOutsideZone.put(CatTrackerType.BIG, 3L);

            Map<DogTrackerType, Long> dogsOutsideZone = new HashMap<>();
            dogsOutsideZone.put(DogTrackerType.SMALL, 2L);
            dogsOutsideZone.put(DogTrackerType.BIG, 4L);

            PetsOutsideZoneDto expectedDto = new PetsOutsideZoneDto(catsOutsideZone, dogsOutsideZone);

            when(catTrackerService.countCatsOutsideZone()).thenReturn(catsOutsideZone);
            when(dogTrackerService.countDogsOutsideZone()).thenReturn(dogsOutsideZone);

            PetsOutsideZoneDto result = petTrackerService.countPetsOutsideZoneGroupByType();

            assertNotNull(result);
            assertEquals(expectedDto, result);

            verify(catTrackerService, times(1)).countCatsOutsideZone();
            verify(dogTrackerService, times(1)).countDogsOutsideZone();
        }

        @Test
        @DisplayName("Should handle empty counts for both cats and dogs")
        void shouldHandleEmptyCountsForBothCatsAndDogs() {
            Map<CatTrackerType, Long> catsOutsideZone = new HashMap<>();
            Map<DogTrackerType, Long> dogsOutsideZone = new HashMap<>();

            PetsOutsideZoneDto expectedDto = new PetsOutsideZoneDto(catsOutsideZone, dogsOutsideZone);

            when(catTrackerService.countCatsOutsideZone()).thenReturn(catsOutsideZone);
            when(dogTrackerService.countDogsOutsideZone()).thenReturn(dogsOutsideZone);

            PetsOutsideZoneDto result = petTrackerService.countPetsOutsideZoneGroupByType();

            assertNotNull(result);
            assertEquals(expectedDto, result);

            verify(catTrackerService, times(1)).countCatsOutsideZone();
            verify(dogTrackerService, times(1)).countDogsOutsideZone();
        }
    }

    @Nested
    @DisplayName("createPet")
    class CreatePetTests {

        @Test
        @DisplayName("Should create a new pet and return PetDto")
        void shouldCreateNewPetAndReturnPetDto() {
            PetDto incomingPetDto = DogDto.builder()
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            Pet mappedPet = Dog.builder()
                    .id(1L)
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            Pet savedPet = Dog.builder()
                    .id(1L)
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            PetDto savedPetDto = DogDto.builder()
                    .id(1L)
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            when(petMapper.mapDtoToEntity(incomingPetDto)).thenReturn(mappedPet);
            when(petRepository.save(mappedPet)).thenReturn(savedPet);
            when(petMapper.mapEntityToDto(savedPet)).thenReturn(savedPetDto);

            PetDto result = petTrackerService.createPet(incomingPetDto);

            assertNotNull(result);
            assertEquals(savedPetDto, result);

            verify(petMapper, times(1)).mapDtoToEntity(incomingPetDto);
            verify(petRepository, times(1)).save(mappedPet);
            verify(petMapper, times(1)).mapEntityToDto(savedPet);
        }

        @Test
        @DisplayName("Should handle save exceptions and propagate")
        void shouldHandleSaveException() {
            PetDto incomingPetDto = DogDto.builder()
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            Pet mappedPet = Dog.builder()
                    .id(1L)
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            when(petMapper.mapDtoToEntity(incomingPetDto)).thenReturn(mappedPet);
            when(petRepository.save(mappedPet)).thenThrow(new RuntimeException("Save failed"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                petTrackerService.createPet(incomingPetDto);
            }, "Expected createPet to throw RuntimeException");

            assertEquals("Save failed", exception.getMessage());

            verify(petMapper, times(1)).mapDtoToEntity(incomingPetDto);
            verify(petRepository, times(1)).save(mappedPet);
            verify(petMapper, never()).mapEntityToDto(any());
        }
    }

    @Nested
    @DisplayName("updatePet")
    class UpdatePetTests {

        @Test
        @DisplayName("Should update existing pet and return updated PetDto")
        void shouldUpdateExistingPetAndReturnUpdatedPetDto() {
            long petId = 1L;
            PetDto updatedPetDto = DogDto.builder()
                    .ownerId(101L)
                    .inZone(false)
                    .trackerType(DogTrackerType.BIG)
                    .build();

            Pet existingPet = Dog.builder()
                    .id(petId)
                    .ownerId(100L)
                    .inZone(true)
                    .trackerType(DogTrackerType.SMALL)
                    .build();

            Pet updatedPet = Dog.builder()
                    .id(petId)
                    .ownerId(101L)
                    .inZone(false)
                    .trackerType(DogTrackerType.BIG)
                    .build();

            PetDto updatedPetDtoResult = DogDto.builder()
                    .id(petId)
                    .ownerId(101L)
                    .inZone(false)
                    .trackerType(DogTrackerType.BIG)
                    .build();

            when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
            doNothing().when(petMapper).mapDtoToEntity(updatedPetDto, existingPet);
            when(petRepository.save(existingPet)).thenReturn(updatedPet);
            when(petMapper.mapEntityToDto(updatedPet)).thenReturn(updatedPetDtoResult);

            PetDto result = petTrackerService.updatePet(petId, updatedPetDto);

            assertNotNull(result, "Expected non-null PetDto");
            assertEquals(updatedPetDtoResult, result, "Expected PetDto to match updatedPetDtoResult");

            verify(petRepository, times(1)).findById(petId);
            verify(petMapper, times(1)).mapDtoToEntity(updatedPetDto, existingPet);
            verify(petRepository, times(1)).save(existingPet);
            verify(petMapper, times(1)).mapEntityToDto(updatedPet);
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when updating non-existent pet")
        void shouldThrowExceptionWhenUpdatingNonExistentPet() {
            long petId = 1000L;
            PetDto updatedPetDto = DogDto.builder()
                    .ownerId(101L)
                    .inZone(false)
                    .trackerType(DogTrackerType.BIG)
                    .build();

            when(petRepository.findById(petId)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                petTrackerService.updatePet(petId, updatedPetDto);
            }, "Expected updatePet to throw ResponseStatusException");

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertTrue(Objects.requireNonNull(exception.getReason()).contains("Pet 1000 not found"));

            verify(petRepository, times(1)).findById(petId);
            verify(petMapper, never()).mapDtoToEntity(any(), any());
            verify(petRepository, never()).save(any());
            verify(petMapper, never()).mapEntityToDto(any());
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when mapping fails")
        void shouldThrowExceptionWhenMappingFails() {
            long petId = 1L;
            PetDto updatedPetDto = DogDto.builder()
                    .ownerId(-1) // Invalid: assuming ownerId must positive
                    .inZone(null)  // Invalid: assuming inZone must not be null
                    .build();

            Pet existingPet = Cat.builder()
                    .id(petId)
                    .ownerId(100L)
                    .inZone(true)
                    .trackerType(CatTrackerType.SMALL)
                    .lostTracker(false)
                    .build();

            when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
            doThrow(new IllegalArgumentException("Invalid PetDto")).when(petMapper).mapDtoToEntity(updatedPetDto, existingPet);

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                petTrackerService.updatePet(petId, updatedPetDto);
            }, "Expected updatePet to throw ResponseStatusException due to mapping failure");

            assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
            assertTrue(Objects.requireNonNull(exception.getReason()).contains("Pet cannot get updated"));

            verify(petRepository, times(1)).findById(petId);
            verify(petMapper, times(1)).mapDtoToEntity(updatedPetDto, existingPet);
            verify(petRepository, never()).save(any());
            verify(petMapper, never()).mapEntityToDto(any());
        }
    }

    @Nested
    @DisplayName("deletePet")
    class DeletePetTests {

        @Test
        @DisplayName("Should delete existing pet successfully")
        void shouldDeleteExistingPetSuccessfully() {
            long petId = 1L;
            Pet existingPet = Cat.builder()
                    .id(petId)
                    .ownerId(100L)
                    .inZone(true)
                    .trackerType(CatTrackerType.SMALL)
                    .lostTracker(false)
                    .build();

            when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
            doNothing().when(petRepository).delete(existingPet);

            assertDoesNotThrow(() -> petTrackerService.deletePet(petId));

            verify(petRepository, times(1)).findById(petId);
            verify(petRepository, times(1)).delete(existingPet);
        }

        @Test
        @DisplayName("Should throw ResponseStatusException when deleting non-existent pet")
        void shouldThrowExceptionWhenDeletingNonExistentPet() {
            long petId = 1000L;
            when(petRepository.findById(petId)).thenReturn(Optional.empty());

            ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
                petTrackerService.deletePet(petId);
            }, "Expected deletePet to throw ResponseStatusException for non-existent pet");

            assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
            assertTrue(Objects.requireNonNull(exception.getReason()).contains("Pet 1000 not found"));

            verify(petRepository, times(1)).findById(petId);
            verify(petRepository, never()).delete(any());
        }
    }
}
