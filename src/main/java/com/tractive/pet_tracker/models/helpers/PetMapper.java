package com.tractive.pet_tracker.models.helpers;

import com.tractive.pet_tracker.models.dtos.CatDto;
import com.tractive.pet_tracker.models.dtos.DogDto;
import com.tractive.pet_tracker.models.dtos.PetDto;
import com.tractive.pet_tracker.models.entities.Cat;
import com.tractive.pet_tracker.models.entities.Dog;
import com.tractive.pet_tracker.models.entities.Pet;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PetMapper {
    private final ModelMapper modelMapper;

    /**
     * Maps the {@code petDto} to corresponding entity type
     *
     * @param petDto the PetDto
     * @return PetDto
     *
     * @throws IllegalArgumentException if the {@code petDto} type is not known
     */
    public Pet mapDtoToEntity(PetDto petDto) {
        return modelMapper.map(petDto, getPetClass(petDto));
    }


    /**
     * Maps the {@code petDto} to {@code pet}
     *
     * @param petDto the PetDto
     * @param pet The Pet Entity
     *
     * @throws IllegalArgumentException if the {@code petDto} and {@code pet} type is the same
     */
    public void mapDtoToEntity(PetDto petDto, Pet pet) {
        if (getPetClass(petDto).isInstance(pet)) {
            modelMapper.map(petDto, pet);
            return;
        }

        throw new IllegalArgumentException("Unknown pet type: " + pet.getClass().getSimpleName());
    }

    /**
     * Maps the {@code pet} to corresponding DTO type
     *
     * @param pet The Pet Entity
     * @return PetDto
     *
     * @throws IllegalArgumentException if the {@code pet} type is not known
     */
    public PetDto mapEntityToDto(Pet pet) {
        return modelMapper.map(pet, getPetDtoClass(pet));
    }

    /**
     * Gets the {@code petDto}'s corresponding entity class
     *
     * @param petDto the PetDto
     * @return Pet (Cat or Dog)
     *
     * @throws IllegalArgumentException if the {@code petDto} type is not known
     */
    private Class<? extends Pet> getPetClass(PetDto petDto) {
        if (petDto instanceof CatDto) {
            return Cat.class;
        } else if (petDto instanceof DogDto) {
            return Dog.class;
        }

        throw new IllegalArgumentException("Unknown PetDto type: " + petDto.getClass().getSimpleName());
    }

    /**
     * Gets the {@code petDto}'s corresponding entity class
     *
     * @param pet The Pet Entity
     * @return PetDto (CatDto or DogDto)
     *
     * @throws IllegalArgumentException if the {@code pet} type is not known
     */
    private Class<? extends PetDto> getPetDtoClass(Pet pet) {
        if (pet instanceof Cat) {
            return CatDto.class;
        } else if (pet instanceof Dog) {
            return DogDto.class;
        }

        throw new IllegalArgumentException("Unknown Pet type: " + pet.getClass().getSimpleName());
    }
}
