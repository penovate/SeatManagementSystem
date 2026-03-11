package com.esunbank.seat_system.repository;

import com.esunbank.seat_system.entity.SeatingChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatingChartRepository extends JpaRepository<SeatingChart, Integer> {
}
