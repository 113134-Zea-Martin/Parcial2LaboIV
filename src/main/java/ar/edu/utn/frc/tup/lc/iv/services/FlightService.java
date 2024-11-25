package ar.edu.utn.frc.tup.lc.iv.services;

import ar.edu.utn.frc.tup.lc.iv.models.Flight;
import org.springframework.stereotype.Service;

@Service
public interface FlightService {
    Flight createFlight(Flight flight);
    Flight getFlightById(String idFlight);
}
