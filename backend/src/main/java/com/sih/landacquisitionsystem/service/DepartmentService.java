package com.sih.landacquisitionsystem.service;

import com.sih.landacquisitionsystem.model.Department;
import com.sih.landacquisitionsystem.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    @PreAuthorize("hasRole('ADMIN')")
    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Department updateDepartment(Department department) {
        return departmentRepository.save(department);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDepartment(Long id) {
        departmentRepository.deleteById(id);
    }
}