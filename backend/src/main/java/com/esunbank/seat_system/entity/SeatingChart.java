package com.esunbank.seat_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "SeatingChart")
public class SeatingChart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FLOOR_SEAT_SEQ")
    private Integer floorSeatSeq;

    @Column(name = "FLOOR_NO")
    private Integer floorNo;

    @Column(name = "SEAT_NO")
    private Integer seatNo;
}
