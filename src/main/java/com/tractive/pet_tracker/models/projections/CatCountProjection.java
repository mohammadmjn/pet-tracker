package com.tractive.pet_tracker.models.projections;

import com.tractive.pet_tracker.models.enums.CatTrackerType;

public record CatCountProjection(CatTrackerType trackerType, long count) { }
