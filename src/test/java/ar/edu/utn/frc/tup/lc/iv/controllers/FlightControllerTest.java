package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.models.Flight;
import ar.edu.utn.frc.tup.lc.iv.services.FlightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class FlightControllerTest {

    @Mock
    private FlightService flightService;

    @InjectMocks
    private FlightController flightController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFlight() {
        Flight flight = new Flight();
        when(flightService.createFlight(flight)).thenReturn(flight);

        ResponseEntity<Flight> response = flightController.createFlight(flight);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(flight, response.getBody());
    }

    @Test
    void createFlight_withNullFlight() {
        ResponseEntity<Flight> response = flightController.createFlight(null);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getFlightById() {
        String idFlight = "123";
        Flight flight = new Flight();
        when(flightService.getFlightById(idFlight)).thenReturn(flight);

        ResponseEntity<Flight> response = flightController.getFlightById(idFlight);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(flight, response.getBody());
    }

    @Test
    void getFlightById_withNonExistentId() {
        String idFlight = "non-existent";
        when(flightService.getFlightById(idFlight)).thenReturn(null);

        ResponseEntity<Flight> response = flightController.getFlightById(idFlight);

        assertEquals(200, response.getStatusCodeValue());
    }
}