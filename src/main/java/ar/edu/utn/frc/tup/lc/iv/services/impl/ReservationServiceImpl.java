package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.PassengerEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.ReservationEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.SeatEntity;
import ar.edu.utn.frc.tup.lc.iv.models.Flight;
import ar.edu.utn.frc.tup.lc.iv.models.Passenger;
import ar.edu.utn.frc.tup.lc.iv.models.Reservation;
import ar.edu.utn.frc.tup.lc.iv.models.Seat;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.repositories.ReservationRepository;
import ar.edu.utn.frc.tup.lc.iv.services.FlightService;
import ar.edu.utn.frc.tup.lc.iv.services.ReservationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final FlightRepository flightRepository;

    public ReservationServiceImpl(ReservationRepository reservationRepository, FlightRepository flightRepository) {
        this.reservationRepository = reservationRepository;
        this.flightRepository = flightRepository;
    }

    @Override
    public Reservation createReservation(Reservation reservation) {

        if (!checkAvailableSeats(reservation)) {
            throw new RuntimeException("Not enough available seats");
        }

        if (!checkStatus(reservation)) {
            throw new RuntimeException("Invalid status");
        }

        ReservationEntity reservationEntity = getReservationEntity(reservation);
        reservationRepository.save(reservationEntity);

        return getReservation(reservationEntity);
    }

    public Reservation getReservation(ReservationEntity reservationEntity) {
        Reservation createdReservation = new Reservation();
        createdReservation.setId(reservationEntity.getId());
        createdReservation.setStatus(reservationEntity.getStatus());
        createdReservation.setFlight(reservationEntity.getFlight());

        List<Passenger> createdPassengers = new ArrayList<>();
        for (PassengerEntity passengerEntity : reservationEntity.getPassengers()) {
            Passenger passenger = new Passenger();
            passenger.setName(passengerEntity.getName());
            passenger.setSeat(passengerEntity.getSeat());
            createdPassengers.add(passenger);
        }
        createdReservation.setPassengers(createdPassengers);

        return createdReservation;
    }

    public ReservationEntity getReservationEntity(Reservation reservation) {
        ReservationEntity reservationEntity = new ReservationEntity();
        reservationEntity.setId(reservation.getId());
        reservationEntity.setStatus(reservation.getStatus());
        reservationEntity.setFlight(reservation.getFlight());

        List<Passenger> passengers = reservation.getPassengers();
        List<PassengerEntity> passengerEntities = new ArrayList<>();
        for (Passenger passenger : passengers) {
            PassengerEntity passengerEntity = new PassengerEntity();
            passengerEntity.setName(passenger.getName());
            passengerEntity.setSeat(passenger.getSeat());
            passengerEntity.setReservation(reservationEntity);
            passengerEntities.add(passengerEntity);
        }

        reservationEntity.setPassengers(passengerEntities);
        return reservationEntity;
    }

    @Override
    public Reservation getReservationById(String idReservation) {
        ReservationEntity reservationEntity = reservationRepository.findById(idReservation).orElse(null);
        if (reservationEntity == null) {
            throw new RuntimeException("Reservation not found");
        }

        return getReservation(reservationEntity);
    }

    public boolean checkAvailableSeats(Reservation reservation) {
        FlightEntity flightEntity = flightRepository.findById(reservation.getFlight()).orElse(null);
        if (flightEntity == null) {
            throw new RuntimeException("Flight not found");
        }

        int solicitedSeats = reservation.getPassengers().size();
        int availableSeats = 0;

        for (SeatEntity seatEntity : flightEntity.getSeat_map()) {
            if (seatEntity.getStatus().equals("available")) {
                availableSeats++;
            }
        }
        return availableSeats >= solicitedSeats;
    }

    public boolean checkStatus(Reservation reservation) {
        return reservation.getStatus().equals("READY-TO-CHECK-IN");
    }

    @Override
    public Reservation updateReservation(String idReserva, Reservation reservation) {

        ReservationEntity reservationEntity = reservationRepository.findById(reservation
                .getId()).orElse(null);
        if (reservationEntity == null) {
            throw new RuntimeException("Reservation not found");
        }

        if (!checkAllSeatsFounded(reservation)) {
            throw new RuntimeException("Seat not found");
        }

        if (!checkStatusReservation(reservationEntity)) {
            throw new RuntimeException("Invalid status");
        }

        reservationEntity.setId(reservation.getId());

        if (!checkDateFlight(reservation)) {
            reservationEntity.setStatus("DUE");
            reservationRepository.save(reservationEntity);
            throw new RuntimeException("Flight has already departed");
        }

        reservationEntity.setStatus(reservation.getStatus());
        reservationEntity.setFlight(reservation.getFlight());

        List<PassengerEntity> passengerEntities = reservationEntity.getPassengers();
        passengerEntities.clear();

        for (Passenger passenger : reservation.getPassengers()) {
            PassengerEntity passengerEntity = new PassengerEntity();
            passengerEntity.setName(passenger.getName());
            passengerEntity.setSeat(passenger.getSeat());
            passengerEntity.setReservation(reservationEntity);
            passengerEntities.add(passengerEntity);
        }

        reservationEntity.setPassengers(passengerEntities);
        reservationRepository.save(reservationEntity);

        FlightEntity flightEntity = flightRepository.findById(reservation
                .getFlight()).orElse(null);
        List<SeatEntity> seatEntities = flightEntity.getSeat_map();


        for (Passenger passenger : reservation.getPassengers()) {
            for (SeatEntity seatEntity : flightEntity.getSeat_map()) {
                if (seatEntity.getSeat().equals(passenger.getSeat())) {
                    seatEntities.remove(seatEntity);
                    seatEntity.setSeat(passenger.getSeat());
                    seatEntity.setStatus("reserved");
                    seatEntity.setFlight(flightEntity);
                    seatEntities.add(seatEntity);
                }
            }
        }
        flightEntity.setSeat_map(seatEntities);
        flightRepository.save(flightEntity);

        return getReservation(reservationEntity);
    }

    public boolean checkDateFlight(Reservation reservation) {
        FlightEntity flightEntity = flightRepository.findById(reservation
                .getFlight()).orElse(null);
        if (flightEntity == null) {
            throw new RuntimeException("Flight not found");
        }
        return !flightEntity.getDeparture().isBefore(LocalDateTime.now());
    }

    public boolean checkStatusReservation(ReservationEntity reservationEntity) {
        return reservationEntity.getStatus().equals("READY-TO-CHECK-IN");
    }

    public boolean checkAllSeatsFounded(Reservation reservation) {
        FlightEntity flightEntity = flightRepository.findById(reservation
                .getFlight()).orElse(null);
        List<Passenger> passengers = reservation.getPassengers();
        List<SeatEntity> seatEntities = flightEntity.getSeat_map();
        List<Seat> seats = new ArrayList<>();
        for (Passenger passenger : passengers) {
            Seat seat = new Seat();
            seat.setSeat(passenger.getSeat());
            seats.add(seat);
        }
        for (Seat seat : seats) {
            boolean found = false;
            for (SeatEntity seatEntity : seatEntities) {
                if (seatEntity.getSeat().equals(seat.getSeat())) {
                    found = true;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

}
