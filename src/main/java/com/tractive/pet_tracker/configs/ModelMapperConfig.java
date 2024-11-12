package com.tractive.pet_tracker.configs;

import com.tractive.pet_tracker.models.dtos.CatDto;
import com.tractive.pet_tracker.models.dtos.DogDto;
import com.tractive.pet_tracker.models.dtos.PetDto;
import com.tractive.pet_tracker.models.entities.Cat;
import com.tractive.pet_tracker.models.entities.Dog;
import com.tractive.pet_tracker.models.entities.Pet;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        var modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setAmbiguityIgnored(true)
                .setFieldMatchingEnabled(true)
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        // Configure modelMapper to prevent changing entity id
        modelMapper.typeMap(CatDto.class, Cat.class)
            .addMappings(mapping -> mapping.skip(Cat::setId));
        modelMapper.typeMap(DogDto.class, Dog.class)
            .addMappings(mapping -> mapping.skip(Dog::setId));

        return modelMapper;
    }
}
