package com.tractive.pet_tracker.models.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "petType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CatDto.class, name = "cat"),
        @JsonSubTypes.Type(value = DogDto.class, name = "dog")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Data
public abstract sealed class PetDto permits CatDto, DogDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    @Positive
    private long ownerId;

    @NotNull
    private Boolean inZone;
}
