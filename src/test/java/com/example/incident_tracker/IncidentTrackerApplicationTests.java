package com.example.incident_tracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.example.incident_tracker.repository.IncidentRepository;

import tools.jackson.databind.ObjectMapper;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class IncidentTrackerApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private IncidentRepository incidentRepository;

	@Autowired
	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		incidentRepository.deleteAll();
	}

	// Smoke test - verifies if Spring can start the app context successfully
	@Test
	void contextLoads() {
	}

	// Verify that a valid incident is successfully created
	@Test
	void createIncident_returns201() throws Exception{

		String requestBody = """
				{
					"title": "Server error",
					"description": "The server is throwing an error",
					"reported_by": "Alice",
					"severity":"HIGH"
				}
				""";

		mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isCreated());
	}

	// Verify that a missing required field is rejected and returns a 400 error
	@Test
	void createIncident_missingRequiredField_returns400() throws Exception{

		String requestBody = """
				{
					"description": "Cannot click the save button",
					"reported_by": "Bob",
					"severity":"LOW"
				}
				""";
		mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());
	}

	// Verify that an invalid Severity value is rejected and returns a 400 error
	@Test
	void createIncident_invalidSeverity_returns400() throws Exception{

		String requestBody = """
				{
					"title": "Server error",
					"description": "Cannot click the save button",
					"reported_by": "Bob",
					"severity":"SUPER_HIGH"
				}
				""";
		mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isBadRequest());
	}

	// Verify that requesting all incidents returns a 200 response and a list containing incidents
	@Test
	void getIncidents_returns200() throws Exception{

    	// Create an incident so the GET request has data to return
		String requestBody = """
				{
					"title": "Server error",
					"description": "The server is throwing an error",
					"reported_by": "Karen",
					"severity":"HIGH"
				}
				""";
		
		// POST the test incident
		mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody));

		// GET all the incidents
		 mockMvc.perform(get("/incidents"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Server error"))
            .andExpect(jsonPath("$[0].reportedBy").value("Karen"))
            .andExpect(jsonPath("$[0].severity").value("HIGH"));
	}

	// Verify that requesting all incidents filtered by severity returns a 200 response and a list filtered incidents
	@Test
	void getIncidents_filterBySeverity_returns200() throws Exception{

    	// Create incidents so the GET request has data to return
		String requestBodyHigh = """
				{
					"title": "Bug in Login",
					"description": "Users cannot login",
					"reported_by": "Karen",
					"severity":"HIGH"
				}
				""";
		mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBodyHigh));

		String requestBodyLow = """
				{
					"title": "Typo on Homepage",
					"description": "Spelling mistake",
					"reported_by": "Anne",
					"severity":"LOW"
				}
				""";
		mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBodyLow));

		// GET the incidents filtered
		 mockMvc.perform(get("/incidents").param("severity", "LOW"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].title").value("Typo on Homepage"))
            .andExpect(jsonPath("$[0].reportedBy").value("Anne"))
            .andExpect(jsonPath("$[0].severity").value("LOW"));
	}

	// Verify that requesting one incident with ID returns a 200 response
	@Test
	void getIncident_withId_returns200() throws Exception{

    	// Create an incident so the GET request has data to return
		String requestBody = """
				{
					"title": "Network error",
					"description": "Cannot load",
					"reported_by": "Rebecca",
					"severity":"MEDIUM"
				}
				""";
		
		// POST the test incident and get the content of the response
		String response = mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		
		// Extract the incident ID from the response
		long incidentId = objectMapper.readTree(response).get("id").asLong();

		// GET the incident
		 mockMvc.perform(get("/incidents/" + incidentId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(incidentId))
            .andExpect(jsonPath("$.title").value("Network error"))
            .andExpect(jsonPath("$.reportedBy").value("Rebecca"))
            .andExpect(jsonPath("$.severity").value("MEDIUM"));
	}

	// Verify that requesting one incident with missing ID returns a 404 error
	@Test
	void getIncident_withInvalidId_returns404() throws Exception{
		// GET the incident - but its not found
		 mockMvc.perform(get("/incidents/99999")).andDo(print()).andExpect(status().isNotFound());
	}

	// Verify that patching one incident with ID returns a 200 response
	@Test
	void patchIncident_withId_returns200() throws Exception{

    	// Create an incident so the GET request has data to return
		String requestBody = """
				{
					"title": "Lost my password",
					"description": "Please help me find it",
					"reported_by": "Kathy",
					"severity":"MEDIUM"
				}
				""";
		
		// POST the test incident and get the content of the response
		String response = mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		
		// Extract the incident ID from the response
		long incidentId = objectMapper.readTree(response).get("id").asLong();

		String requestPatchBody = """
				{
					"status": "IN_PROGRESS"
				}
				""";

		// PATCH the incident
		 mockMvc.perform(patch("/incidents/" + incidentId).contentType(MediaType.APPLICATION_JSON).content(requestPatchBody))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(incidentId))
            .andExpect(jsonPath("$.title").value("Lost my password"))
            .andExpect(jsonPath("$.reportedBy").value("Kathy"))
            .andExpect(jsonPath("$.severity").value("MEDIUM"))
			.andExpect(jsonPath("$.status").value("IN_PROGRESS"));
	}

	// Verify that patching one incident with an invalid ID returns a 404 response
	@Test
	void patchIncident_invalidId_returns404() throws Exception{

    	// Create an incident so the GET request has data to return
		String requestBody = """
				{
					"title": "Lost my password",
					"description": "Please help me find it",
					"reported_by": "Kathy",
					"severity":"MEDIUM"
				}
				""";
		
		mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody)).andExpect(status().isCreated());

		String requestPatchBody = """
				{
					"status": "IN_PROGRESS"
				}
				""";

		// PATCH the incident - but couldnt find with that ID
		 mockMvc.perform(patch("/incidents/99999").contentType(MediaType.APPLICATION_JSON).content(requestPatchBody))
            .andDo(print())
            .andExpect(status().isNotFound());
	}

	// Verify that patching one incident with invalid status returns status 400
	@Test
	void patchIncident_invalidStatus_returns400() throws Exception{

    	// Create an incident so the GET request has data to return
		String requestBody = """
				{
					"title": "Lost my password",
					"description": "Please help me find it",
					"reported_by": "Kathy",
					"severity":"MEDIUM"
				}
				""";
		
		// POST the test incident and get the content of the response
		String response = mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody))
			.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
		
		// Extract the incident ID from the response
		long incidentId = objectMapper.readTree(response).get("id").asLong();

		String requestPatchBody = """
				{
					"status": "PENDING"
				}
				""";

		// PATCH the incident
		 mockMvc.perform(patch("/incidents/" + incidentId).contentType(MediaType.APPLICATION_JSON).content(requestPatchBody))
            .andDo(print())
            .andExpect(status().isBadRequest());
	}

	// Verify that deleting an incident with a valid ID returns a 204 No Content response
    @Test
    void deleteIncident_withId_returns204() throws Exception {
        // Create an incident so the delete request has data to use
        String requestBody = """
                {
                    "title": "Server down",
                    "description": "Server is down",
                    "reported_by": "John",
                    "severity": "HIGH"
                }
                """;
        
        String response = mockMvc.perform(post("/incidents").contentType(MediaType.APPLICATION_JSON).content(requestBody))
            .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        
        long incidentId = objectMapper.readTree(response).get("id").asLong();

        // DELETE the incident and expect 204 No Content
        mockMvc.perform(delete("/incidents/" + incidentId)).andDo(print()).andExpect(status().isNoContent());

        // Verify it is actually deleted by trying to GET it and return 404
        mockMvc.perform(get("/incidents/" + incidentId)).andExpect(status().isNotFound());
    }

	// Verify that deleting an incident with an invalid ID returns a 404
    @Test
    void deleteIncident_invalidId_returns404() throws Exception {
        mockMvc.perform(delete("/incidents/9999")).andDo(print()).andExpect(status().isNotFound());
    }
}
