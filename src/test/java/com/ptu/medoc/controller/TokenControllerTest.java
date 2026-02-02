package com.ptu.medoc.controller;

import com.ptu.medoc.allocation.TokenEngine;
import com.ptu.medoc.dto.BookedTokenResponse;
import com.ptu.medoc.dto.TokenRequest;
import com.ptu.medoc.service.TokenService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TokenController.class)
class TokenControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private TokenEngine tokenEngine;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void allocateToken_success() throws Exception {
        TokenRequest request = new TokenRequest();
        request.setDoctorName("Dr John");
        request.setNameOfPatient("Alice");
        request.setPhoneNumber("9999999999");
        request.setSource("ONLINE");

        Mockito.when(tokenService.allocate(Mockito.any()))
                .thenReturn(Map.of("status", "BOOKED"));

        mockMvc.perform(post("/api/v1/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("BOOKED"));
    }

    @Test
    void cancelToken_success() throws Exception {
        Mockito.when(tokenEngine.cancelToken(1L))
                .thenReturn(Map.of("status", "CANCELLED"));

        mockMvc.perform(delete("/api/v1/token/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void getBookedTokens_success() throws Exception {
        BookedTokenResponse dto = new BookedTokenResponse();
        dto.setPatientName("Bob");
        dto.setCreatedAt(LocalDateTime.now());

        Page<BookedTokenResponse> page =
                new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1);

        Mockito.when(tokenService.getBookedTokens(
                        Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/token/booked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..patientName").value("Bob"));
    }

    @Test
    void markNoShow_success() throws Exception {
        Mockito.when(tokenEngine.markNoShowAndReassign(5L))
                .thenReturn(Map.of("status", "NO_SHOW"));

        mockMvc.perform(post("/api/v1/token/5/no-show"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("NO_SHOW"));
    }
}
