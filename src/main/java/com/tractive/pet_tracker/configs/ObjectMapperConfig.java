package com.tractive.pet_tracker.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)    // Ignores case sensitivity for trackerType
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_VALUES)   // ignores case sensitivity for petType
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .build();
    }
}
