package com.esunbank.seat_system.repository;

import com.esunbank.seat_system.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Procedure(procedureName = "UpdateEmployeeSeat")
    void updateEmployeeSeat(@Param("EmpID") String empId, @Param("NewSeatSeq") Integer newSeatSeq);
}
