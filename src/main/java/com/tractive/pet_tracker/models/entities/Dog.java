package com.tractive.pet_tracker.models.entities;

import com.tractive.pet_tracker.models.enums.DogTrackerType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@DiscriminatorValue("DOG")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Entity
public class Dog extends Pet {
    @Enumerated(value = EnumType.STRING)
    @NotNull
    private DogTrackerType trackerType;
}
