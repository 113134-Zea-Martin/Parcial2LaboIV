package ar.edu.utn.frc.tup.lc.iv.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ar.edu.utn.frc.tup.lc.iv.entities.AirportEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.SeatEntity;
import ar.edu.utn.frc.tup.lc.iv.models.Airport;
import ar.edu.utn.frc.tup.lc.iv.models.Flight;
import ar.edu.utn.frc.tup.lc.iv.models.Seat;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FlightServiceImplTest {

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private FlightServiceImpl flightServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFlight_validFlight_createsFlight() {
        Flight flight = new Flight();
        flight.setId("1");
        flight.setAircraft("Boeing 737");
        flight.setDeparture(LocalDateTime.now().plusDays(1));

        Airport airport = new Airport();
        airport.setName("Ezeiza");
        airport.setCode("EZE");
        airport.setLocation("Buenos Aires");

        flight.setAirport(airport);

        List<Seat> seats = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Seat seat = new Seat();
            seat.setSeat("A" + i);
            seat.setStatus("available");
            seats.add(seat);
        }

        flight.setSeat_map(seats);

        when(flightRepository.save(any(FlightEntity.class))).thenReturn(new FlightEntity());

        Flight createdFlight = flightServiceImpl.createFlight(flight);

        assertNotNull(createdFlight);
        verify(flightRepository, times(1)).save(any(FlightEntity.class));
    }

    @Test
    void createFlight_invalidSeatStatus_throwsException() {
        Flight flight = new Flight();
        flight.setId("1");
        flight.setAircraft("Boeing 737");
        flight.setDeparture(LocalDateTime.now().plusDays(1));
        // Set other necessary fields and invalid seat map

        assertThrows(NullPointerException.class, () -> flightServiceImpl.createFlight(flight));
    }

    @Test
    void createFlight_invalidFlightDate_throwsException() {
        Flight flight = new Flight();
        flight.setId("1");
        flight.setAircraft("Boeing 737");
        flight.setDeparture(LocalDateTime.now().plusHours(1));
        // Set other necessary fields and valid seat map

        assertThrows(NullPointerException.class, () -> flightServiceImpl.createFlight(flight));
    }

    @Test
    void getFlightById_existingId_returnsFlight() {
        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setId("1");
        AirportEntity airportEntity = new AirportEntity();
        airportEntity.setName("Ezeiza");
        airportEntity.setCode("EZE");
        airportEntity.setLocation("Buenos Aires");
        flightEntity.setAirport(airportEntity);

        List<SeatEntity> seatEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SeatEntity seatEntity = new SeatEntity();
            seatEntity.setSeat("A" + i);
            seatEntity.setStatus("available");
            seatEntities.add(seatEntity);
        }
        flightEntity.setSeat_map(seatEntities);

        when(flightRepository.findById("1")).thenReturn(Optional.of(flightEntity));

        Flight flight = flightServiceImpl.getFlightById("1");

        assertNotNull(flight);
        assertEquals("1", flight.getId());
    }

    @Test
    void getFlightById_nonExistingId_throwsException() {
        when(flightRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> flightServiceImpl.getFlightById("1"));
    }
}