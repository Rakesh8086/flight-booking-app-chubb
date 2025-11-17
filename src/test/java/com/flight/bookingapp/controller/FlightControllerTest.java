package com.flight.bookingapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.bookingapp.dto.FlightSearchRequest;
import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.service.FlightService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightController.class)
public class FlightControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to simulate HTTP requests

    @Autowired
    private ObjectMapper objectMapper; // Used to convert objects to JSON strings

    @MockBean
    private FlightService flightService;

    private Flight testFlight;
    private FlightSearchRequest validSearchRequest;

    @BeforeEach
    void setUp() {
        testFlight = new Flight();
        testFlight.setId(101L);
        testFlight.setAirlineName("Air India");
        testFlight.setFromPlace("DEL");
        testFlight.setToPlace("BOM");
        testFlight.setScheduleDate(LocalDate.of(2025, 12, 25));
        testFlight.setDepartureTime(LocalTime.of(8, 0));
        testFlight.setArrivalTime(LocalTime.of(10, 0));
        testFlight.setPrice(5000.00);
        testFlight.setTotalSeats(150);
        
        validSearchRequest = new FlightSearchRequest();
        validSearchRequest.setFromPlace("DEL");
        validSearchRequest.setToPlace("BOM");
        validSearchRequest.setJourneyDate(LocalDate.of(2025, 12, 25));
    }
    
    // Testing POST /api/v1.0/flight/search

    @Test
    void searchFlights_Success_Returns200AndResults() throws Exception {
        List<Flight> mockResults = List.of(testFlight);
        when(flightService.searchFlights(any(), any(), any()))
                .thenReturn(mockResults);

        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSearchRequest)))
                .andExpect(status().isOk()) 
                .andExpect(jsonPath("$", hasSize(1))) 
                .andExpect(jsonPath("$[0].fromPlace", is("DEL"))); 
    }

    @Test
    void searchFlights_NoResults_Returns404NotFound() throws Exception {
        when(flightService.searchFlights(any(), any(), any()))
                .thenReturn(Collections.emptyList());

        // Controller logic should throw Exception, handled by GlobalErrorHandler
        mockMvc.perform(post("/api/v1.0/flight/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validSearchRequest)))
                .andExpect(status().isNotFound()); 
    }

}