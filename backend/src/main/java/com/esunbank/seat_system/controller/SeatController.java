package com.esunbank.seat_system.controller;

import com.esunbank.seat_system.entity.Employee;
import com.esunbank.seat_system.entity.SeatingChart;
import com.esunbank.seat_system.service.SeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class SeatController {

    @Autowired
    private SeatService seatService;

    @GetMapping("/seating-chart")
    public List<SeatingChart> getSeatingChart() {
        return seatService.getAllSeats();
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        return seatService.getAllEmployees();
    }

    @PostMapping("/update-seat")
    public String updateSeat(@RequestBody Map<String, Object> payload) {
        String empId = (String) payload.get("empId");
        Integer newSeatSeq = (Integer) payload.get("newSeatSeq");

        seatService.updateSeatAssignment(empId, newSeatSeq);
        return "更新成功！";
    }
}
