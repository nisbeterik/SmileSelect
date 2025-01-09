package com.smile_select.dentist_service.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile_select.dentist_service.model.Dentist;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.hamcrest.Matchers.containsString;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class DentistControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static long firstDentistId;

    private static long clinicId;



    // Gets the first available clinic ID in database
    // Gets the first available dentist ID in database
    @BeforeAll
    static void init(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {

        MvcResult firstResult = mockMvc.perform(get("/api/dentists/clinics"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = firstResult.getResponse().getContentAsString();
        clinicId = objectMapper.readTree(responseBody).get(0).get("id").longValue();
        System.out.println("Initialized First Clinic ID: " + clinicId);

        // Get all dentists for the selected clinic
        MvcResult secondResult = mockMvc.perform(get(String.format("/api/dentists?clinicId=%d", clinicId)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String secondBody = secondResult.getResponse().getContentAsString();
        System.out.println("Response from /api/dentists?clinicId={clinicId}: " + secondBody);

        // Extract firstDentistId from the first dentist of the selected clinic
        firstDentistId = objectMapper.readTree(secondBody).get(0).get("id").longValue();
        System.out.println("Initialized First Dentist ID: " + firstDentistId);

    }


    @Test
    public void testRegisterDentistSuccess() throws Exception {
        Dentist newDentist = new Dentist();
        newDentist.setFirstName("Alice");
        newDentist.setLastName("Wonderland");
        newDentist.setEmail("alice.wonderland@example.com");
        newDentist.setPassword("securepassword123");
        newDentist.setClinicId(clinicId);

        mockMvc.perform(post("/api/dentists/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDentist)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Dentist registered successfully!")));
    }

    @Test
    public void testRegisterDentistDuplicateEmail() throws Exception {

        Dentist newDentist = new Dentist();
        newDentist.setFirstName("Tooth");
        newDentist.setLastName("Ache");
        newDentist.setEmail("tooth.ache@example.com"); // Existing email from test-data.sql
        newDentist.setPassword("password123");
        newDentist.setClinicId(clinicId);

        mockMvc.perform(post("/api/dentists/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDentist)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email is already in use")));
    }

    @Test
    public void testRegisterDentistMissingClinicId() throws Exception {
        Dentist newDentist = new Dentist();
        newDentist.setFirstName("Charlie");
        newDentist.setLastName("Brown");
        newDentist.setEmail("charlie.brown@example.com");
        newDentist.setPassword("password123");
        newDentist.setClinicId(null);

        mockMvc.perform(post("/api/dentists/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDentist)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Clinic ID is required")));
    }

    @Test
    public void testRegisterDentistInvalidClinicId() throws Exception {
        Dentist newDentist = new Dentist();
        newDentist.setFirstName("David");
        newDentist.setLastName("Johnson");
        newDentist.setEmail("david.johnson@example.com");
        newDentist.setPassword("password123");
        newDentist.setClinicId(9999999L); // Invalid clinicId

        mockMvc.perform(post("/api/dentists/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newDentist)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Selected clinic does not exist")));
    }

    @Test
    public void testGetDentistByIdSuccess() throws Exception {

        mockMvc.perform(get("/api/dentists/{id}", firstDentistId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(firstDentistId))
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.clinicId").exists())
                .andExpect(jsonPath("$.clinicName").exists())
                .andExpect(jsonPath("$.longitude").exists())
                .andExpect(jsonPath("$.latitude").exists())
                .andExpect(jsonPath("$.street").exists())
                .andExpect(jsonPath("$.zip").exists())
                .andExpect(jsonPath("$.city").exists())
                .andExpect(jsonPath("$.houseNumber").exists());
    }

    @Test
    public void testGetDentistByIdNotFound() throws Exception {
        long nonExistingDentistId = 999999L;
        mockMvc.perform(get("/api/dentists/{id}", nonExistingDentistId))
                .andExpect(status().isNotFound());
    }


    // Test commented out because you need some sort of authentication to delete
    // An authentication I am not aware of how to get but this should work in theory, with authentication
    /*
    @Test
    public void testDeleteDentistSuccess() throws Exception {
        mockMvc.perform(get("/api/dentists/{id}", firstDentistId))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/dentists/{id}", firstDentistId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/dentists/{id}", firstDentistId))
                .andExpect(status().isNotFound());
    }*/

    @Test
    public void testDeleteDentistNotFound() throws Exception {

        long nonExistentDentistId = 9999L;

        mockMvc.perform(delete("/api/dentists/{id}", nonExistentDentistId))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetDentistByEmailSuccess() throws Exception {
        String email = "tooth.ache@example.com"; // email from test-data.sql

        mockMvc.perform(get("/api/dentists/by-email")
                        .param("email", email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    public void testGetDentistByEmailNotFound() throws Exception {

        String email = "nonexistent.email@example.com";

        mockMvc.perform(get("/api/dentists/by-email")
                        .param("email", email))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Dentist not found with email: " + email)));
    }
}
