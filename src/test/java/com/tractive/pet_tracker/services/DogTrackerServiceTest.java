package com.tractive.pet_tracker.services;

import com.tractive.pet_tracker.models.enums.DogTrackerType;
import com.tractive.pet_tracker.models.projections.DogCountProjection;
import com.tractive.pet_tracker.repositories.DogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class DogTrackerServiceTest {

    @Mock
    private DogRepository dogRepository;

    @Autowired
    private DogTrackerService dogTrackerService;

    @Nested
    @DisplayName("countDogsOutsideZone")
    class CountDogsOutsideZoneTests {

        @Test
        @DisplayName("Should return correct counts when dogs are outside the zone")
        void shouldReturnCorrectCountsWhenDogsOutsideZone() {
            dogTrackerService = new DogTrackerService(dogRepository);

            DogCountProjection smallTypeProjection = new DogCountProjection(DogTrackerType.SMALL, 5L);
            DogCountProjection bigTypeProjection = new DogCountProjection(DogTrackerType.BIG, 3L);

            when(dogRepository.countNotInZone()).thenReturn(List.of(smallTypeProjection, bigTypeProjection));

            Map<DogTrackerType, Long> result = dogTrackerService.countDogsOutsideZone();

            assertEquals(2, result.size());
            assertEquals(5L, result.get(DogTrackerType.SMALL));
            assertEquals(3L, result.get(DogTrackerType.BIG));

            verify(dogRepository, times(1)).countNotInZone();
        }

        @Test
        @DisplayName("Should return empty map when no dogs are outside the zone")
        void shouldReturnEmptyMapWhenNoDogsOutsideZone() {
            dogTrackerService = new DogTrackerService(dogRepository);

            when(dogRepository.countNotInZone()).thenReturn(List.of());

            Map<DogTrackerType, Long> result = dogTrackerService.countDogsOutsideZone();

            assertTrue(result.isEmpty());
            verify(dogRepository, times(1)).countNotInZone();
        }

        @Test
        @DisplayName("Should throw RuntimeException if repository throws an exception")
        void shouldThrowExceptionIfRepositoryThrowsException() {
            dogTrackerService = new DogTrackerService(dogRepository);

            when(dogRepository.countNotInZone()).thenThrow(new RuntimeException("Database error"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                dogTrackerService.countDogsOutsideZone();
            });

            assertTrue(exception.getMessage().contains("Database error"));
            verify(dogRepository, times(1)).countNotInZone();
        }
    }
}
