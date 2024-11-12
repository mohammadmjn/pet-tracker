package com.tractive.pet_tracker.models.helpers;

import com.tractive.pet_tracker.models.dtos.CatDto;
import com.tractive.pet_tracker.models.dtos.DogDto;
import com.tractive.pet_tracker.models.dtos.PetDto;
import com.tractive.pet_tracker.models.entities.Cat;
import com.tractive.pet_tracker.models.entities.Dog;
import com.tractive.pet_tracker.models.entities.Pet;
import com.tractive.pet_tracker.models.enums.CatTrackerType;
import com.tractive.pet_tracker.models.enums.DogTrackerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class PetMapperTest {
    @InjectMocks
    private PetMapper petMapper;

    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();
        petMapper = new PetMapper(modelMapper);
    }

    @Nested
    @DisplayName("mapDtoToEntity")
    class MapDtoToEntityTests {

        @Test
        @DisplayName("Should map CatDto to Cat")
        void shouldMapCatDtoToCat() {
            CatDto catDto = CatDto.builder()
                    .ownerId(100L)
                    .inZone(true)
                    .trackerType(CatTrackerType.SMALL)
                    .lostTracker(false)
                    .build();

            Pet catEntity = petMapper.mapDtoToEntity(catDto);

            assertInstanceOf(Cat.class, catEntity);
            assertEquals(catDto.getOwnerId(), catEntity.getOwnerId());
            assertEquals(catDto.getInZone(), catEntity.getInZone());
            assertEquals(catDto.getTrackerType(), ((Cat) catEntity).getTrackerType());
            assertEquals(catDto.getLostTracker(), ((Cat) catEntity).getLostTracker());
        }

        @Test
        @DisplayName("Should map DogDto to Dog")
        void shouldMapDogDtoToDog() {
            DogDto dogDto = DogDto.builder()
                    .ownerId(101L)
                    .inZone(false)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            Pet dogEntity = petMapper.mapDtoToEntity(dogDto);

            assertInstanceOf(Dog.class, dogEntity);
            assertEquals(dogDto.getOwnerId(), dogEntity.getOwnerId());
            assertEquals(dogDto.getInZone(), dogEntity.getInZone());
            assertEquals(dogDto.getTrackerType(), ((Dog) dogEntity).getTrackerType());
        }
    }

    @Nested
    @DisplayName("mapEntityToDto")
    class MapEntityToDtoTests {

        @Test
        @DisplayName("Should map Cat to CatDto")
        void shouldMapCatToCatDto() {
            Cat catEntity = Cat.builder()
                    .ownerId(100L)
                    .inZone(true)
                    .trackerType(CatTrackerType.SMALL)
                    .lostTracker(false)
                    .build();

            PetDto catDto = petMapper.mapEntityToDto(catEntity);

            assertInstanceOf(CatDto.class, catDto);
            assertEquals(catEntity.getOwnerId(), catDto.getOwnerId());
            assertEquals(catEntity.getInZone(), catDto.getInZone());
            assertEquals(catEntity.getTrackerType(), ((CatDto) catDto).getTrackerType());
            assertEquals(catEntity.getLostTracker(), ((CatDto) catDto).getLostTracker());
        }

        @Test
        @DisplayName("Should map Dog to DogDto")
        void shouldMapDogToDogDto() {
            Dog dogEntity = Dog.builder()
                    .ownerId(101L)
                    .inZone(false)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            PetDto dogDto = petMapper.mapEntityToDto(dogEntity);

            assertInstanceOf(DogDto.class, dogDto);
            assertEquals(dogEntity.getOwnerId(), dogDto.getOwnerId());
            assertEquals(dogEntity.getInZone(), dogDto.getInZone());
            assertEquals(dogEntity.getTrackerType(), ((DogDto) dogDto).getTrackerType());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for unknown Pet type")
        void shouldThrowExceptionForUnknownPetType() {
            Pet unknownPet = new Pet() {};

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                petMapper.mapEntityToDto(unknownPet);
            });

            assertTrue(exception.getMessage().contains("Unknown Pet type"));
        }
    }

    @Nested
    @DisplayName("mapDtoToEntity (with target entity)")
    class MapDtoToEntityWithTargetEntityTests {

        @Test
        @DisplayName("Should map CatDto to existing Cat instance")
        void shouldMapCatDtoToExistingCatInstance() {
            CatDto catDto = CatDto.builder()
                    .ownerId(100L)
                    .inZone(true)
                    .trackerType(CatTrackerType.SMALL)
                    .lostTracker(false)
                    .build();

            // A Cat with different initial values for some fields
            Cat catEntity = Cat.builder()
                    .ownerId(100L)
                    .inZone(false)
                    .trackerType(CatTrackerType.BIG)
                    .lostTracker(false)
                    .build();

            petMapper.mapDtoToEntity(catDto, catEntity);

            assertEquals(catDto.getOwnerId(), catEntity.getOwnerId());
            assertEquals(catDto.getInZone(), catEntity.getInZone());
            assertEquals(catDto.getTrackerType(), catEntity.getTrackerType());
            assertEquals(catDto.getLostTracker(), catEntity.getLostTracker());
        }

        @Test
        @DisplayName("Should map DogDto to existing Dog instance")
        void shouldMapDogDtoToExistingDogInstance() {
            DogDto dogDto = DogDto.builder()
                    .ownerId(101L)
                    .inZone(true)
                    .trackerType(DogTrackerType.MEDIUM)
                    .build();

            // A Cat with different initial values for some fields
            Dog dogEntity = Dog.builder()
                    .ownerId(101L)
                    .inZone(false)
                    .trackerType(DogTrackerType.SMALL)
                    .build();

            petMapper.mapDtoToEntity(dogDto, dogEntity);

            assertEquals(dogDto.getOwnerId(), dogEntity.getOwnerId());
            assertEquals(dogDto.getInZone(), dogEntity.getInZone());
            assertEquals(dogDto.getTrackerType(), dogEntity.getTrackerType());
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException if PetDto and Pet type do not match")
        void shouldThrowExceptionIfPetDtoAndPetTypeDoNotMatch() {
            DogDto dogDto = DogDto.builder().ownerId(101L).build();
            Cat catEntity = Cat.builder().ownerId(200L).build();

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                petMapper.mapDtoToEntity(dogDto, catEntity);
            });

            assertTrue(exception.getMessage().contains("Unknown pet type"));
        }
    }
}
