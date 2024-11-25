package ar.edu.utn.frc.tup.lc.iv.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class FlightEntity {
    @Id
    private String id;

    private String aircraft;

    private LocalDateTime departure;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "airport_id", nullable = false)
    private AirportEntity airport;

    @OneToMany(mappedBy = "flight", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SeatEntity> seat_map;
}
