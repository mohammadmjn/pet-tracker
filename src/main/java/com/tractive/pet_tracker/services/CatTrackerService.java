package com.tractive.pet_tracker.services;

import com.tractive.pet_tracker.models.enums.CatTrackerType;
import com.tractive.pet_tracker.models.projections.CatCountProjection;
import com.tractive.pet_tracker.repositories.CatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CatTrackerService {
    private final CatRepository catRepository;

    public Map<CatTrackerType, Long> countCatsOutsideZone() {
        return catRepository.countNotInZone().stream()
            .collect(
                Collectors.toUnmodifiableMap(CatCountProjection::trackerType, CatCountProjection::count)
            );
    }
}
