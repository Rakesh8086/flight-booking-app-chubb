package com.flight.bookingapp.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.service.FlightService;

@RestController
@RequestMapping("/api/v1.0/flight")
public class FlightController {
	
	@Autowired
    FlightService flightService;

    @PostMapping("/airline/inventory/add")
    public ResponseEntity<Flight> addFlightInventory(@Valid @RequestBody Flight flight) {
        
        Flight savedFlight = flightService.addFlight(flight);
        
        return new ResponseEntity<>(savedFlight, HttpStatus.CREATED);
    }
 
}