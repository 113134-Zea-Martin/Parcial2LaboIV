package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.PassengerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ReservationEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.SeatEntity;
import ar.edu.utn.frc.tup.lc.iv.models.Passenger;
import ar.edu.utn.frc.tup.lc.iv.models.Reservation;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ReservationServiceImplTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private FlightRepository flightRepository;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReservation_success() {
        Reservation reservation = new Reservation();
        reservation.setStatus("READY-TO-CHECK-IN");
        reservation.setFlight("flight1");
        reservation.setPassengers(new ArrayList<>());

        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setSeat_map(new ArrayList<>());

        when(flightRepository.findById("flight1")).thenReturn(Optional.of(flightEntity));
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(new ReservationEntity());

        Reservation result = reservationService.createReservation(reservation);

        assertNotNull(result);
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
    }

    @Test
    void createReservation_notEnoughSeats() {
        Reservation reservation = new Reservation();
        reservation.setStatus("READY-TO-CHECK-IN");
        reservation.setFlight("flight1");

        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Passenger passenger = new Passenger();
            passengers.add(passenger);
        }
        reservation.setPassengers(passengers);

        FlightEntity flightEntity = new FlightEntity();
        List<SeatEntity> seatEntities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SeatEntity seatEntity = new SeatEntity();
            seatEntity.setSeat("A" + i);
            seatEntity.setStatus("available");
            seatEntities.add(seatEntity);
        }
        flightEntity.setSeat_map(seatEntities);

        when(flightRepository.findById("flight1")).thenReturn(Optional.of(flightEntity));

        assertThrows(RuntimeException.class, () -> reservationService.createReservation(reservation));
    }

    @Test
    void getReservationById_success() {
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId("reservation1");
        List<PassengerEntity> passengerEntities = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            PassengerEntity passengerEntity = new PassengerEntity();
            passengerEntity.setName("Passenger " + i);
            passengerEntity.setSeat("A" + i);
            passengerEntities.add(passengerEntity);
        }
        reservationEntity.setPassengers(passengerEntities);

        when(reservationRepository.findById("reservation1")).thenReturn(Optional.of(reservationEntity));

        Reservation result = reservationService.getReservationById("reservation1");

        assertNotNull(result);
        assertEquals("reservation1", result.getId());
    }

    @Test
    void getReservationById_notFound() {
        when(reservationRepository.findById("reservation1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservationService.getReservationById("reservation1"));
    }

    @Test
    void updateReservation_success() {
        Reservation reservation = new Reservation();
        reservation.setId("reservation1");
        reservation.setStatus("READY-TO-CHECK-IN");
        reservation.setFlight("flight1");
        reservation.setPassengers(new ArrayList<>());

        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId("reservation1");
        reservationEntity.setStatus("READY-TO-CHECK-IN");
        reservationEntity.setPassengers(new ArrayList<>());

        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setSeat_map(new ArrayList<>());
        flightEntity.setDeparture(LocalDateTime.now().plusDays(1));

        when(reservationRepository.findById("reservation1")).thenReturn(Optional.of(reservationEntity));
        when(flightRepository.findById("flight1")).thenReturn(Optional.of(flightEntity));
        when(reservationRepository.save(any(ReservationEntity.class))).thenReturn(reservationEntity);

        Reservation result = reservationService.updateReservation("reservation1", reservation);

        assertNotNull(result);
        verify(reservationRepository, times(1)).save(any(ReservationEntity.class));
    }

    @Test
    void updateReservation_notFound() {
        Reservation reservation = new Reservation();
        reservation.setId("reservation1");

        when(reservationRepository.findById("reservation1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservationService.updateReservation("reservation1", reservation));
    }

    @Test
    void checkAvailableSeats_success() {
        Reservation reservation = new Reservation();
        reservation.setFlight("flight1");
        reservation.setPassengers(new ArrayList<>());

        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setSeat_map(new ArrayList<>());

        when(flightRepository.findById("flight1")).thenReturn(Optional.of(flightEntity));

        boolean result = reservationService.checkAvailableSeats(reservation);

        assertTrue(result);
    }

    @Test
    void checkAvailableSeats_flightNotFound() {
        Reservation reservation = new Reservation();
        reservation.setFlight("flight1");

        when(flightRepository.findById("flight1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> reservationService.checkAvailableSeats(reservation));
    }

    @Test
    void checkStatus_success() {
        Reservation reservation = new Reservation();
        reservation.setStatus("READY-TO-CHECK-IN");

        boolean result = reservationService.checkStatus(reservation);

        assertTrue(result);
    }

    @Test
    void checkStatus_invalidStatus() {
        Reservation reservation = new Reservation();
        reservation.setStatus("INVALID");

        boolean result = reservationService.checkStatus(reservation);

        assertFalse(result);
    }

    @Test
    void createReservation_invalidStatus() {
        Reservation reservation = new Reservation();
        reservation.setStatus("INVALID");
        reservation.setFlight("flight1");
        reservation.setPassengers(new ArrayList<>());

        assertThrows(RuntimeException.class, () -> reservationService.createReservation(reservation));
    }

    @Test
    void updateReservation_invalidStatus() {
        Reservation reservation = new Reservation();
        reservation.setId("reservation1");
        reservation.setStatus("INVALID");
        reservation.setFlight("flight1");
        reservation.setPassengers(new ArrayList<>());

        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId("reservation1");
        reservationEntity.setStatus("READY-TO-CHECK-IN");
        reservationEntity.setPassengers(new ArrayList<>());

        when(reservationRepository.findById("reservation1")).thenReturn(Optional.of(reservationEntity));

        assertThrows(RuntimeException.class, () -> reservationService.updateReservation("reservation1", reservation));
    }

    @Test
    void updateReservation_flightDeparted() {
        Reservation reservation = new Reservation();
        reservation.setId("reservation1");
        reservation.setStatus("READY-TO-CHECK-IN");
        reservation.setFlight("flight1");
        reservation.setPassengers(new ArrayList<>());

        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId("reservation1");
        reservationEntity.setStatus("READY-TO-CHECK-IN");
        reservationEntity.setPassengers(new ArrayList<>());

        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setSeat_map(new ArrayList<>());
        flightEntity.setDeparture(LocalDateTime.now().minusDays(1));

        when(reservationRepository.findById("reservation1")).thenReturn(Optional.of(reservationEntity));
        when(flightRepository.findById("flight1")).thenReturn(Optional.of(flightEntity));

        assertThrows(RuntimeException.class, () -> reservationService.updateReservation("reservation1", reservation));
    }

    @Test
    void checkAvailableSeats_notEnoughSeats() {
        Reservation reservation = new Reservation();
        reservation.setFlight("flight1");

        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Passenger passenger = new Passenger();
            passengers.add(passenger);
        }
        reservation.setPassengers(passengers);

        FlightEntity flightEntity = new FlightEntity();
        List<SeatEntity> seatEntities = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SeatEntity seatEntity = new SeatEntity();
            seatEntity.setSeat("A" + i);
            seatEntity.setStatus("available");
            seatEntities.add(seatEntity);
        }
        flightEntity.setSeat_map(seatEntities);

        when(flightRepository.findById("flight1")).thenReturn(Optional.of(flightEntity));

        boolean result = reservationService.checkAvailableSeats(reservation);

        assertFalse(result);
    }

    @Test
    void checkAllSeatsFounded_seatNotFound() {
        Reservation reservation = new Reservation();
        reservation.setFlight("flight1");

        List<Passenger> passengers = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Passenger passenger = new Passenger();
            passenger.setSeat("A" + i);
            passengers.add(passenger);
        }
        reservation.setPassengers(passengers);

        FlightEntity flightEntity = new FlightEntity();
        List<SeatEntity> seatEntities = new ArrayList<>();
        for (int i = 0; i < 1; i++) {
            SeatEntity seatEntity = new SeatEntity();
            seatEntity.setSeat("A" + i);
            seatEntity.setStatus("available");
            seatEntities.add(seatEntity);
        }
        flightEntity.setSeat_map(seatEntities);

        when(flightRepository.findById("flight1")).thenReturn(Optional.of(flightEntity));

        boolean result = reservationService.checkAllSeatsFounded(reservation);

        assertFalse(result);
    }
}