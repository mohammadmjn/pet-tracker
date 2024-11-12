package com.tractive.pet_tracker.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tractive.pet_tracker.models.dtos.CatDto;
import com.tractive.pet_tracker.models.dtos.DogDto;
import com.tractive.pet_tracker.models.dtos.PetDto;
import com.tractive.pet_tracker.models.enums.CatTrackerType;
import com.tractive.pet_tracker.models.enums.DogTrackerType;
import com.tractive.pet_tracker.services.CatTrackerService;
import com.tractive.pet_tracker.services.DogTrackerService;
import com.tractive.pet_tracker.services.PetTrackerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = {"/insert_pets.sql"})
@Sql(scripts = {"/clean.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class PetTrackerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private PetTrackerService petTrackerService;

    @MockBean
    CatTrackerService catTrackerService;

    @MockBean
    DogTrackerService dogTrackerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("GET /api/v1/pet-tracker/{id}")
    class GetPetById {

        @Test
        @DisplayName("Should return CatDto when cat exists")
        void shouldReturnCatDtoWhenCatExists() throws Exception {
            long catId = 1L;
            mockMvc.perform(get("/api/v1/pet-tracker/{id}", catId).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(catId))
                    .andExpect(jsonPath("$.petType").value("cat"))
                    .andExpect(jsonPath("$.trackerType").value("SMALL"))
                    .andExpect(jsonPath("$.ownerId").value(1))
                    .andExpect(jsonPath("$.inZone").value(true))
                    .andExpect(jsonPath("$.lostTracker").value(false));

            verify(petTrackerService, times(1)).getPetById(catId);
        }

        @Test
        @DisplayName("Should return DogDto when dog exists")
        void shouldReturnDogDtoWhenDogExists() throws Exception {
            long dogId = 6L;
            mockMvc.perform(get("/api/v1/pet-tracker/{id}", dogId).accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(dogId))
                    .andExpect(jsonPath("$.petType").value("dog"))
                    .andExpect(jsonPath("$.trackerType").value("BIG"))
                    .andExpect(jsonPath("$.ownerId").value(22))
                    .andExpect(jsonPath("$.inZone").value(false));

            verify(petTrackerService, times(1)).getPetById(dogId);
        }

        @Test
        @DisplayName("Should return 404 when pet does not exist")
        void shouldReturn404WhenPetDoesNotExist() throws Exception {
            long petId = 100L;

            mockMvc.perform(get("/api/v1/pet-tracker/{id}", petId))
                    .andExpect(status().isNotFound());

            verify(petTrackerService, times(1)).getPetById(petId);
        }
    }

    @Nested
    @DisplayName("GET /api/v1/pet-tracker")
    class GetAllPets {

        @Test
        @DisplayName("Should return paginated list of PetDto")
        void shouldReturnPaginatedListOfPetDto() throws Exception {
            mockMvc.perform(get("/api/v1/pet-tracker")
                            .param("page", "0")
                            .param("size", "10")
                            .param("sort", "id,asc"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", Matchers.hasSize(10)))
                    .andExpect(jsonPath("$.content[0].id").value(1))
                    .andExpect(jsonPath("$.content[1].id").value(2))
                    .andExpect(jsonPath("$.totalElements").value(10))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(petTrackerService, times(1)).getAllPets(any(Pageable.class));
        }

        @Test
        @DisplayName("Should return empty page when requested page is greater than pets exist")
        void shouldReturnEmptyPageWhenNoPetsExist() throws Exception {
            mockMvc.perform(get("/api/v1/pet-tracker")
                            .param("page", "3")
                            .param("size", "10")
                            .param("sort", "id,asc"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.content", Matchers.hasSize(0)))
                    .andExpect(jsonPath("$.totalElements").value(10))
                    .andExpect(jsonPath("$.totalPages").value(1));

            verify(petTrackerService, times(1)).getAllPets(any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/pet-tracker/zone-info")
    class GetPetsOutsideZoneCount {

        @Test
        @DisplayName("Should return PetsOutsideZoneDto with counts")
        void shouldReturnPetsOutsideZoneDtoWithCounts() throws Exception {
            Mockito.when(catTrackerService.countCatsOutsideZone()).thenReturn(
                Map.of(CatTrackerType.BIG, 1L, CatTrackerType.SMALL, 1L)
            );
            Mockito.when(dogTrackerService.countDogsOutsideZone()).thenReturn(
                Map.of(DogTrackerType.BIG, 2L, DogTrackerType.MEDIUM, 5L, DogTrackerType.SMALL, 1L)
            );

            mockMvc.perform(get("/api/v1/pet-tracker/zone-info"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                        {"cats":{"BIG":1,"SMALL":1},"dogs":{"BIG":2,"MEDIUM":5,"SMALL":1}}
                        """)
                    );

            verify(petTrackerService, times(1)).countPetsOutsideZoneGroupByType();
        }

        @Test
        @DisplayName("Should return zero counts when no pets are outside the zone")
        void shouldReturnZeroCountsWhenNoPetsAreOutsideZone() throws Exception {
            Mockito.when(catTrackerService.countCatsOutsideZone()).thenReturn(
                    Map.of(CatTrackerType.BIG, 0L, CatTrackerType.SMALL, 0L)
            );
            Mockito.when(dogTrackerService.countDogsOutsideZone()).thenReturn(
                    Map.of(DogTrackerType.BIG, 0L, DogTrackerType.MEDIUM, 0L, DogTrackerType.SMALL, 0L)
            );

            mockMvc.perform(get("/api/v1/pet-tracker/zone-info"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(content().json("""
                        {"cats":{},"dogs":{}}
                        """)
                    );

            verify(petTrackerService, times(1)).countPetsOutsideZoneGroupByType();
        }

        @Nested
        @DisplayName("POST /api/v1/pet-tracker")
        class CreatePet {

            @Test
            @DisplayName("Should create a new Cat and return CatDto")
            void shouldCreateNewCatAndReturnCatDto() throws Exception {
                var newTestCat = """
                    {"petType":"cat","ownerId":101,"inZone":true,"trackerType":"BIG","lostTracker":false}
                """;

                mockMvc.perform(post("/api/v1/pet-tracker")
                                .content(newTestCat)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(11))
                        .andExpect(jsonPath("$.petType").value("cat"))
                        .andExpect(jsonPath("$.trackerType").value("BIG"))
                        .andExpect(jsonPath("$.ownerId").value(101))
                        .andExpect(jsonPath("$.inZone").value(true))
                        .andExpect(jsonPath("$.lostTracker").value(false));

                verify(petTrackerService, times(1)).createPet(any(PetDto.class));
            }

            @Test
            @DisplayName("Should create a new Dog and return DogDto")
            void shouldCreateNewDogAndReturnDogDto() throws Exception {
                var newTestDog = """
                    {"petType":"dog","ownerId":101,"inZone":true,"trackerType":"MEDIUM"}
                """;

                mockMvc.perform(post("/api/v1/pet-tracker")
                                .content(newTestDog)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.id").value(11))
                        .andExpect(jsonPath("$.petType").value("dog"))
                        .andExpect(jsonPath("$.trackerType").value("MEDIUM"))
                        .andExpect(jsonPath("$.ownerId").value(101))
                        .andExpect(jsonPath("$.inZone").value(true));

                verify(petTrackerService, times(1)).createPet(any(PetDto.class));
            }

            @Test
            @DisplayName("Should return 400 Bad Request when input is invalid")
            void shouldReturn400WhenInputIsInvalid() throws Exception {
                var newTestDog = """
                    {"petType":"dog","ownerId":101,"inZone":true,"trackerType":"MEDIUM","lostTracker":false}
                """;

                mockMvc.perform(post("/api/v1/pet-tracker")
                                .content(newTestDog)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());

                verify(petTrackerService, times(0)).createPet(any(PetDto.class));
            }
        }

        @Nested
        @DisplayName("PUT /api/v1/pet-tracker/{id}")
        class UpdatePet {

            @Test
            @DisplayName("Should update existing Cat and return updated CatDto")
            void shouldUpdateExistingCatAndReturnUpdatedCatDto() throws Exception {
                var updatingCat = """
                {"petType":"cat","ownerId":101,"inZone":true,"trackerType":"SMALL","lostTracker":false}
                """;

                mockMvc.perform(put("/api/v1/pet-tracker/1").content(updatingCat)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.petType").value("cat"))
                        .andExpect(jsonPath("$.trackerType").value("SMALL"))
                        .andExpect(jsonPath("$.ownerId").value(101))
                        .andExpect(jsonPath("$.inZone").value(true))
                        .andExpect(jsonPath("$.lostTracker").value(false));

                verify(petTrackerService, times(1)).updatePet(eq(1L), any(CatDto.class));
            }

            @Test
            @DisplayName("Should update existing Dog and return updated DogDto")
            void shouldUpdateExistingDogAndReturnUpdatedDogDto() throws Exception {
                var updatingDog = """
                {"petType":"dog","ownerId":2,"inZone":true,"trackerType":"SMALL"}
                """;

                mockMvc.perform(put("/api/v1/pet-tracker/5").content(updatingDog)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(5))
                        .andExpect(jsonPath("$.petType").value("dog"))
                        .andExpect(jsonPath("$.trackerType").value("SMALL"))
                        .andExpect(jsonPath("$.ownerId").value(2))
                        .andExpect(jsonPath("$.inZone").value(true));

                verify(petTrackerService, times(1)).updatePet(eq(5L), any(DogDto.class));
            }

            @Test
            @DisplayName("Should return 404 when updating non-existent pet")
            void shouldReturn404WhenUpdatingNonExistentPet() throws Exception {
                var updatingCat = """
                {"petType":"cat","ownerId":101,"inZone":true,"trackerType":"SMALL","lostTracker":false}
                """;

                long notExistingPetId = 500L;

                mockMvc.perform(put("/api/v1/pet-tracker/{id}", notExistingPetId)
                                .content(updatingCat)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());

                verify(petTrackerService, times(1)).updatePet(eq(notExistingPetId), any(PetDto.class));
            }

            @Test
            @DisplayName("Should return 400 Bad Request when input is invalid")
            void shouldReturn400WhenUpdateInputIsInvalid() throws Exception {
                var updatingCat = """
                {"petType":"cat","ownerId":101,"inZone":true,"trackerType":"MEDIUM","lostTracker":false}
                """;

                mockMvc.perform(put("/api/v1/pet-tracker/1")
                                .content(updatingCat)
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                        .andExpect(status().isBadRequest());

                verify(petTrackerService, times(0)).updatePet(anyLong(), any(PetDto.class));
            }
        }

        @Nested
        @DisplayName("DELETE /api/v1/pet-tracker/{id}")
        class DeletePet {

            @Test
            @DisplayName("Should delete existing Cat and return 204 No Content")
            void shouldDeleteExistingCatAndReturn204() throws Exception {
                long petId = 1L;

                doNothing().when(petTrackerService).deletePet(petId);

                mockMvc.perform(delete("/api/v1/pet-tracker/{id}", petId))
                        .andExpect(status().isNoContent());

                verify(petTrackerService, times(1)).deletePet(petId);
            }

            @Test
            @DisplayName("Should delete existing Dog and return 204 No Content")
            void shouldDeleteExistingDogAndReturn204() throws Exception {
                long petId = 5L;

                doNothing().when(petTrackerService).deletePet(petId);

                mockMvc.perform(delete("/api/v1/pet-tracker/{id}", petId))
                        .andExpect(status().isNoContent());

                verify(petTrackerService, times(1)).deletePet(petId);
            }

            @Test
            @DisplayName("Should return 404 when deleting non-existent pet")
            void shouldReturn404WhenDeletingNonExistentPet() throws Exception {
                long petId = 1000L;

                mockMvc.perform(delete("/api/v1/pet-tracker/{id}", petId))
                        .andExpect(status().isNotFound());

                verify(petTrackerService, times(1)).deletePet(petId);
            }
        }
    }
}
