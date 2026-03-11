package com.esunbank.seat_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Employee")
public class Employee {

    @Id
    @Column(name = "EMP_ID", length = 5)
    private String empId;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @OneToOne
    @JoinColumn(name = "FLOOR_SEAT_SEQ", referencedColumnName = "FLOOR_SEAT_SEQ")
    private SeatingChart seatingChart;
}
