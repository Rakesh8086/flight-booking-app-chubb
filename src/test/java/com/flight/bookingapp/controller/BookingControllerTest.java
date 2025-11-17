package com.flight.bookingapp.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flight.bookingapp.entity.Booking;
import com.flight.bookingapp.entity.Passenger;
import com.flight.bookingapp.exception.BookingNotFoundException;
import com.flight.bookingapp.exception.CancellationNotPossibleException;
import com.flight.bookingapp.service.BookingService;
import com.flight.bookingapp.service.FlightService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc; 

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the dependencies the Controller relies on
    @MockBean
    private BookingService bookingService;
    @MockBean
    private FlightService flightService; // Needed if BookingController constructor requires it

    private Booking mockBooking;
    private final String TEST_PNR = "TESTPNR123";
    private final String TEST_EMAIL = "user@test.com";
    private final Long TEST_FLIGHT_ID = 101L;

    @BeforeEach
    void setUp() {
        Passenger p1 = new Passenger();
        p1.setName("John Doe");
        p1.setAge(30);

        mockBooking = new Booking();
        mockBooking.setPnr(TEST_PNR);
        mockBooking.setFlightId(TEST_FLIGHT_ID);
        mockBooking.setUserEmail(TEST_EMAIL);
        mockBooking.setBookingDate(LocalDateTime.now());
        mockBooking.setNumberOfSeats(1);
        mockBooking.setTotalCost(100.00);
        mockBooking.setPassengers(List.of(p1));
    }    


    // GET /api/v1.0/flight/ticket/{pnr} 

    @Test
    void getTicketByPnr_Success_Returns200Ok() throws Exception {
        when(bookingService.getTicketByPnr(TEST_PNR)).thenReturn(mockBooking);

        mockMvc.perform(get("/api/v1.0/flight/ticket/{pnr}", TEST_PNR)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verify HTTP 200
                .andExpect(jsonPath("$.userEmail", is(TEST_EMAIL)));
    }

    @Test
    void getTicketByPnr_Failure_Returns404NotFound() throws Exception {
        when(bookingService.getTicketByPnr(anyString())).thenThrow(new BookingNotFoundException("PNR not found."));

        mockMvc.perform(get("/api/v1.0/flight/ticket/{pnr}", "FAKEPNR"))
                .andExpect(status().isNotFound()) // Verify HTTP 404
                .andExpect(content().string(is("PNR not found.")));
    }
    
    // GET /api/v1.0/flight/booking/history/{emailId}

    @Test
    void getBookingHistory_Success_Returns200Ok() throws Exception {
        when(bookingService.getBookingHistoryByEmail(TEST_EMAIL)).thenReturn(List.of(mockBooking));

        mockMvc.perform(get("/api/v1.0/flight/booking/history/{emailId}", TEST_EMAIL)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Verify HTTP 200
                .andExpect(jsonPath("$[0].pnr", is(TEST_PNR)));
    }

    // DELETE /api/v1.0/flight/booking/cancel/{pnr}
    
    @Test
    void cancelTicket_Failure_Returns400BadRequest() throws Exception {
        doThrow(new CancellationNotPossibleException("Cancellation deadline exceeded.")).when(bookingService).cancelTicket(anyString());

        mockMvc.perform(delete("/api/v1.0/flight/booking/cancel/{pnr}", "LATEPNR"))
                .andExpect(status().isBadRequest()) // Verify HTTP 400
                .andExpect(content().string(is("Cancellation deadline exceeded.")));
    }
}
