package com.announce.AcknowledgeHub_SpringBoot.repository;

import com.announce.AcknowledgeHub_SpringBoot.entity.Company;
import com.announce.AcknowledgeHub_SpringBoot.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    Optional<Department> findByName(String departmentName);

//    @Query("select d from Department d where d.name = ?1 and d.company = ?2")
//    Optional<Department> findByNameAndCompany(String departmentName, Company company);
//@Query("select d from Department d where d.name = ?1")
Optional<Department> findByNameAndCompany(String departmentName, Company company);

    List<Department> findByCompanyId(int companyId);
}
