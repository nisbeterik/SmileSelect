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


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
