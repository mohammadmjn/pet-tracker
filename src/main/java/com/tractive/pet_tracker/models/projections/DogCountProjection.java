package com.tractive.pet_tracker.models.projections;

import com.tractive.pet_tracker.models.enums.DogTrackerType;

public record DogCountProjection(DogTrackerType trackerType, long count) { }
