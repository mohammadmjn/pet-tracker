package com.tractive.pet_tracker.services;

import com.tractive.pet_tracker.models.enums.CatTrackerType;
import com.tractive.pet_tracker.models.projections.CatCountProjection;
import com.tractive.pet_tracker.repositories.CatRepository;
import org.junit.jupiter.api.*;
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
public class CatTrackerServiceTest {

    @Mock
    private CatRepository catRepository;

    @Autowired
    private CatTrackerService catTrackerService;

    @Nested
    @DisplayName("countCatsOutsideZone")
    class CountCatsOutsideZoneTests {

        @Test
        @DisplayName("Should return correct counts when cats are outside the zone")
        void shouldReturnCorrectCountsWhenCatsOutsideZone() {
            catTrackerService = new CatTrackerService(catRepository);

            CatCountProjection smallTypeProjection = new CatCountProjection(CatTrackerType.SMALL, 5L);
            CatCountProjection bigTypeProjection = new CatCountProjection(CatTrackerType.BIG, 3L);

            when(catRepository.countNotInZone()).thenReturn(List.of(smallTypeProjection, bigTypeProjection));

            Map<CatTrackerType, Long> result = catTrackerService.countCatsOutsideZone();

            assertEquals(2, result.size());
            assertEquals(5L, result.get(CatTrackerType.SMALL));
            assertEquals(3L, result.get(CatTrackerType.BIG));

            verify(catRepository, times(1)).countNotInZone();
        }

        @Test
        @DisplayName("Should return empty map when no cats are outside the zone")
        void shouldReturnEmptyMapWhenNoCatsOutsideZone() {
            catTrackerService = new CatTrackerService(catRepository);

            when(catRepository.countNotInZone()).thenReturn(List.of());

            Map<CatTrackerType, Long> result = catTrackerService.countCatsOutsideZone();

            assertTrue(result.isEmpty());
            verify(catRepository, times(1)).countNotInZone();
        }

        @Test
        @DisplayName("Should throw RuntimeException if repository throws an exception")
        void shouldThrowExceptionIfRepositoryThrowsException() {
            catTrackerService = new CatTrackerService(catRepository);

            when(catRepository.countNotInZone()).thenThrow(new RuntimeException("Database error"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                catTrackerService.countCatsOutsideZone();
            });

            assertTrue(exception.getMessage().contains("Database error"));
            verify(catRepository, times(1)).countNotInZone();
        }
    }
}
