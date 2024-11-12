package com.tractive.pet_tracker.models.dtos;

import com.tractive.pet_tracker.models.enums.CatTrackerType;
import com.tractive.pet_tracker.models.enums.DogTrackerType;

import java.util.Map;

public record PetsOutsideZoneDto (Map<CatTrackerType, Long> cats, Map<DogTrackerType, Long> dogs) { }
