package com.tractive.pet_tracker.models.dtos;

import com.tractive.pet_tracker.models.enums.DogTrackerType;
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
public non-sealed class DogDto extends PetDto {
    @NotNull
    private DogTrackerType trackerType;
}
