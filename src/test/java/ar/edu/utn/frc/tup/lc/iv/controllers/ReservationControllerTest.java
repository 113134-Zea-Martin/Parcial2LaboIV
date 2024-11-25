package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.models.Reservation;
import ar.edu.utn.frc.tup.lc.iv.services.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class ReservationControllerTest {

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private ReservationController reservationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createReservation() {
        Reservation reservation = new Reservation();
        when(reservationService.createReservation(reservation)).thenReturn(reservation);

        ResponseEntity<Reservation> response = reservationController.createReservation(reservation);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reservation, response.getBody());
    }

    @Test
    void getReservationById() {
        String idReservation = "1";
        Reservation reservation = new Reservation();
        when(reservationService.getReservationById(idReservation)).thenReturn(reservation);

        ResponseEntity<Reservation> response = reservationController.getReservationById(idReservation);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reservation, response.getBody());
    }

    @Test
    void updateReservation() {
        String idReservation = "1";
        Reservation reservation = new Reservation();
        when(reservationService.updateReservation(idReservation, reservation)).thenReturn(reservation);

        ResponseEntity<Reservation> response = reservationController.updateReservation(idReservation, reservation);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(reservation, response.getBody());
    }

    @Test
    void createReservationWithNullBody() {
        ResponseEntity<Reservation> response = reservationController.createReservation(null);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void getReservationByIdNotFound() {
        String idReservation = "1";
        when(reservationService.getReservationById(idReservation)).thenReturn(null);

        ResponseEntity<Reservation> response = reservationController.getReservationById(idReservation);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void updateReservationNotFound() {
        String idReservation = "1";
        Reservation reservation = new Reservation();
        when(reservationService.updateReservation(idReservation, reservation)).thenReturn(null);

        ResponseEntity<Reservation> response = reservationController.updateReservation(idReservation, reservation);

        assertEquals(200, response.getStatusCodeValue());
    }
}