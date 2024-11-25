package ar.edu.utn.frc.tup.lc.iv.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Flight {

    private String id;
    private String aircraft;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime departure;
    private Airport airport;
    private List<Seat> seat_map;
}
