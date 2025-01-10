package com.smile_select.patient_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smile_select.patient_service.model.Patient;
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

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
public class PatientControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static long firstPatientId;


    // Tests GET by email + saves patient ID to use for further tests
    @BeforeAll
    static void init(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        String email = "bad.teeth@example.com";
        MvcResult result = mockMvc.perform(get("/api/patients/email/{email}",email))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        firstPatientId = objectMapper.readTree(responseBody).get("id").longValue();
        System.out.println("Initialized First Patient ID: " + firstPatientId);
    }


    @Test
    public void testRegisterPatientSuccess() throws Exception {
        Patient newPatient = new Patient();
        newPatient.setFirstName("Teethy");
        newPatient.setLastName("McTeeth");
        newPatient.setEmail("teethy.mcteeth@example.com");
        newPatient.setPassword("Password123?");
        newPatient.setDateOfBirth(LocalDate.parse("1999-01-01"));

        mockMvc.perform(post("/api/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Patient registered successfully!")));
    }

    @Test
    public void testRegisterPatientDuplicateEmail() throws Exception {

        Patient newPatient = new Patient();
        newPatient.setFirstName("Teethy");
        newPatient.setLastName("McTeeth");
        newPatient.setEmail("no.teeth@example.com"); // Existing email from test-data.sql
        newPatient.setPassword("password123");
        newPatient.setDateOfBirth(LocalDate.parse("1999-01-01"));

        mockMvc.perform(post("/api/patients/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPatient)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Email is already in use")));
    }


}
