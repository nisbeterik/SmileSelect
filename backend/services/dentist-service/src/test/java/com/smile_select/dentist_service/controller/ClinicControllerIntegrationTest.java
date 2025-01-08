package com.smile_select.dentist_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile_select.dentist_service.dto.ClinicDTO;
import com.smile_select.dentist_service.dto.ClinicUpdateDTO;
import com.smile_select.dentist_service.model.Clinic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class ClinicControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static long firstClinicId;


    // Gets the first available clinic ID in database
    @BeforeAll
    static void init(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {

        MvcResult result = mockMvc.perform(get("/api/dentists/clinics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        firstClinicId = objectMapper.readTree(responseBody).get(0).get("id").longValue();
        System.out.println("Initialized First Clinic ID: " + firstClinicId);
    }

    // GET for all clinics
    @Test
    void shouldFetchAllClinics() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/dentists/clinics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(0))))
                .andReturn();


        String responseBody = result.getResponse().getContentAsString();
        firstClinicId = objectMapper.readTree(responseBody).get(0).get("id").longValue();

    }


    // POST clinic
    @Test
    void shouldCreateNewClinic() throws Exception {
        ClinicDTO clinicDTO = new ClinicDTO();
        clinicDTO.setName("Smile Select");
        clinicDTO.setLatitude(37.7749);
        clinicDTO.setLongitude(-122.4194);
        clinicDTO.setStreet("Market Street");
        clinicDTO.setHouseNumber("101");
        clinicDTO.setCity("San Francisco");
        clinicDTO.setZip(94103);

        mockMvc.perform(post("/api/dentists/clinics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clinicDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/dentists/clinics/")))
                .andExpect(jsonPath("$.name", is("Smile Select")))
                .andExpect(jsonPath("$.latitude", is(37.7749)))
                .andExpect(jsonPath("$.longitude", is(-122.4194)))
                .andExpect(jsonPath("$.street", is("Market Street")))
                .andExpect(jsonPath("$.houseNumber", is("101")))
                .andExpect(jsonPath("$.city", is("San Francisco")))
                .andExpect(jsonPath("$.zip", is(94103)));
    }


    // PUT clinic
    @Test
    void shouldUpdateClinic() throws Exception {
        ClinicUpdateDTO clinicUpdateDTO = new ClinicUpdateDTO();
        clinicUpdateDTO.setName("Updated Smile Select");
        clinicUpdateDTO.setLongitude(-122.4194);
        clinicUpdateDTO.setLatitude(37.7749);
        clinicUpdateDTO.setStreet("Updated Market Street");
        clinicUpdateDTO.setZip(94103);
        clinicUpdateDTO.setCity("Updated San Francisco");
        clinicUpdateDTO.setHouseNumber("101");

        MvcResult result = mockMvc.perform(put("/api/dentists/clinics/{id}", firstClinicId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clinicUpdateDTO)))
                .andExpect(status().isOk())
                .andReturn();

        System.out.println("Response body: " + result.getResponse().getContentAsString());
    }


    // Deletes a clinic
    @Test
    void shouldDeleteClinic() throws Exception {
        mockMvc.perform(delete("/api/dentists/clinics/{id}", firstClinicId))
                .andExpect(status().isNoContent());
    }

    // Update clinic name with PATCH
    // Rest of DTO fields simulate request body
    @Test
    void shouldPartiallyUpdateClinic() throws Exception {
        ClinicUpdateDTO clinicPatchDTO = new ClinicUpdateDTO();
        clinicPatchDTO.setName("Partially Updated Name");
        clinicPatchDTO.setLongitude(-122.4194);
        clinicPatchDTO.setLatitude(37.7749);
        clinicPatchDTO.setCity("San Francisco");
        clinicPatchDTO.setStreet("Market Street");
        clinicPatchDTO.setHouseNumber("101");
        clinicPatchDTO.setZip(94103);


        mockMvc.perform(patch("/api/dentists/clinics/{id}", firstClinicId + 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clinicPatchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Partially Updated Name")));
    }

}
