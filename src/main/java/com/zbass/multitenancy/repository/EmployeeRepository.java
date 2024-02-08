package com.zbass.multitenancy.repository;

import com.zbass.multitenancy.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
