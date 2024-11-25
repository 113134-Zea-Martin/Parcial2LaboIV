package ar.edu.utn.frc.tup.lc.iv.controllers;

import ar.edu.utn.frc.tup.lc.iv.models.Reservation;
import ar.edu.utn.frc.tup.lc.iv.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.createReservation(reservation));
    }

    @GetMapping("/{idReservation}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable String idReservation) {
        Reservation reservation = reservationService.getReservationById(idReservation);
        return ResponseEntity.ok(reservation);
    }

    @PutMapping("/{idReservation}")
    public ResponseEntity<Reservation> updateReservation(@PathVariable String idReservation, @RequestBody Reservation reservation) {
        return ResponseEntity.ok(reservationService.updateReservation(idReservation, reservation));
    }
}
