package com.esunbank.seat_system.service;

import com.esunbank.seat_system.entity.Employee;
import com.esunbank.seat_system.entity.SeatingChart;
import com.esunbank.seat_system.repository.EmployeeRepository;
import com.esunbank.seat_system.repository.SeatingChartRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeatService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SeatingChartRepository seatingChartRepository;

    public List<SeatingChart> getAllSeats() {
        return seatingChartRepository.findAll();
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Transactional
    public void updateSeatAssignment(String empId, Integer newSeatSeq) {
        employeeRepository.updateEmployeeSeat(empId, newSeatSeq);
    }
}
