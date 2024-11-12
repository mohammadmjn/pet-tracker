package com.tractive.pet_tracker.services;

import com.tractive.pet_tracker.models.enums.DogTrackerType;
import com.tractive.pet_tracker.models.projections.DogCountProjection;
import com.tractive.pet_tracker.repositories.DogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DogTrackerService {
    private final DogRepository dogRepository;

    public Map<DogTrackerType, Long> countDogsOutsideZone() {
        return dogRepository.countNotInZone().stream()
            .collect(
                Collectors.toUnmodifiableMap(DogCountProjection::trackerType, DogCountProjection::count)
            );
    }
}
