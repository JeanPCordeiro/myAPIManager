package com.example.resourceservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testPublicEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ressource publique accessible"));
    }

    @Test
    public void testProtectedEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/resource"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testProtectedEndpointWithUserRole() throws Exception {
        mockMvc.perform(get("/resource"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ressource protégée accessible"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testAdminEndpointWithUserRole() throws Exception {
        mockMvc.perform(get("/admin/resource"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testAdminEndpointWithAdminRole() throws Exception {
        mockMvc.perform(get("/admin/resource"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Ressource admin accessible"));
    }
}
