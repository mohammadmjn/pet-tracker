package com.tractive.pet_tracker.models.dtos;

import com.tractive.pet_tracker.models.enums.CatTrackerType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public non-sealed class CatDto extends PetDto {
    @NotNull
    private CatTrackerType trackerType;

    @NotNull
    private Boolean lostTracker;
}
