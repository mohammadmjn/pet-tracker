package com.tractive.pet_tracker.models.entities;

import com.tractive.pet_tracker.models.enums.CatTrackerType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@DiscriminatorValue("CAT")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Entity
public class Cat extends Pet {
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private CatTrackerType trackerType;

    @NotNull
    private Boolean lostTracker;
}
