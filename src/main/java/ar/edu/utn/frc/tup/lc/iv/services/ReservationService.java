package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.models.Reservation;
import org.springframework.stereotype.Service;

@Service
public interface ReservationService {
    Reservation createReservation(Reservation reservation);
    Reservation getReservationById(String idReservation);
    Reservation updateReservation(String idReserva, Reservation reservation);
}
