package com.flight.bookingapp.service;

import com.flight.bookingapp.entity.Booking;

public interface BookingService {

    // used for the ticket booking process. 
	// Booking object contains passenger details
    Booking bookTicket(Long flightId, Booking booking);

}