package com.flight.bookingapp.service.impl;

import com.flight.bookingapp.service.FlightService;
import com.flight.bookingapp.entity.Flight;
import com.flight.bookingapp.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FlightServiceImpl implements FlightService {
	
	@Autowired
    FlightRepository flightRepository;

    @Override
    public Flight addFlight(Flight flight) {
    	flight.setAvailableSeats(flight.getTotalSeats());
        
        return flightRepository.save(flight);
    }
}
