package com.portfolio.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.demo.model.WorkTable;

public interface WorkTableRepository extends JpaRepository<WorkTable, Long> {
    List<WorkTable> findByWorkNameContainingIgnoreCase(String workName);
}
