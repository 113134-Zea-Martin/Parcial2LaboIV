package ar.edu.utn.frc.tup.lc.iv.models;

import lombok.Data;

import java.util.List;

@Data
public class Reservation {
    private String id;
    private String status;
    private String flight;
    private List<Passenger> passengers;
}
