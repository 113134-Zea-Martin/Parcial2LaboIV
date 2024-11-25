package ar.edu.utn.frc.tup.lc.iv.services.impl;

import ar.edu.utn.frc.tup.lc.iv.entities.AirportEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.FlightEntity;
import ar.edu.utn.frc.tup.lc.iv.entities.SeatEntity;
import ar.edu.utn.frc.tup.lc.iv.models.Airport;
import ar.edu.utn.frc.tup.lc.iv.models.Flight;
import ar.edu.utn.frc.tup.lc.iv.models.Seat;
import ar.edu.utn.frc.tup.lc.iv.repositories.FlightRepository;
import ar.edu.utn.frc.tup.lc.iv.services.FlightService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;

    public FlightServiceImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    @Override
    public Flight createFlight(Flight flight) {

        if (!checkSeatStatus(flight)) {
            throw new IllegalArgumentException("Invalid seat status");
        }

        if (!checkFlightDate(flight)) {
            throw new IllegalArgumentException("Invalid flight date");
        }

        FlightEntity flightEntity = getFlightEntity(flight);
        flightRepository.save(flightEntity);

        return getFlight(flightEntity);
    }

    public FlightEntity getFlightEntity(Flight flight) {
        FlightEntity flightEntity = new FlightEntity();
        flightEntity.setId(flight.getId());
        flightEntity.setAircraft(flight.getAircraft());
        flightEntity.setDeparture(flight.getDeparture());

        AirportEntity airportEntity = new AirportEntity();
        airportEntity.setName(flight.getAirport().getName());
        airportEntity.setCode(flight.getAirport().getCode());
        airportEntity.setLocation(flight.getAirport().getLocation());

        flightEntity.setAirport(airportEntity);

        List<SeatEntity> seatEntities = new ArrayList<>();
        List<Seat> seats = flight.getSeat_map();
        for (Seat seat : seats) {
            SeatEntity seatEntity = new SeatEntity();
            seatEntity.setSeat(seat.getSeat());
            seatEntity.setStatus(seat.getStatus());
            seatEntity.setFlight(flightEntity);
            seatEntities.add(seatEntity);
        }

        flightEntity.setSeat_map(seatEntities);
        return flightEntity;
    }

    @Override
    public Flight getFlightById(String idFlight) {
        FlightEntity flightEntity = flightRepository.findById(idFlight).orElse(null);
        if (flightEntity == null) {
            throw new IllegalArgumentException("Flight not found");
        }

        return getFlight(flightEntity);
    }

    public Flight getFlight(FlightEntity flightEntity) {
        Flight flight = new Flight();
        flight.setId(flightEntity.getId());
        flight.setAircraft(flightEntity.getAircraft());
        flight.setDeparture(flightEntity.getDeparture());

        Airport airport = new Airport();
        airport.setName(flightEntity.getAirport().getName());
        airport.setCode(flightEntity.getAirport().getCode());
        airport.setLocation(flightEntity.getAirport().getLocation());

        flight.setAirport(airport);

        List<Seat> seats = new ArrayList<>();
        for (SeatEntity seatEntity : flightEntity.getSeat_map()) {
            Seat seat = new Seat();
            seat.setSeat(seatEntity.getSeat());
            seat.setStatus(seatEntity.getStatus());
            seats.add(seat);
        }

        flight.setSeat_map(seats);
        return flight;
    }

    public boolean checkSeatStatus(Flight flight) {
        List<Seat> seats = flight.getSeat_map();
        for (Seat seat : seats) {
            if (!seat.getStatus().equals("reserved") && !seat.getStatus().equals("available")) {
                return false;
            }
        }
        return true;
    }

    public boolean checkFlightDate(Flight flight) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = flight.getDeparture();
        if (departure.isBefore(now.plusHours(6))) {
            return false;
        }
        return true;
    }
}
