package com.portfolio.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.portfolio.demo.model.JobTable;

public interface JobTableRepository extends JpaRepository<JobTable, Long>{

    List<JobTable> findByJobNameContainingIgnoreCase(String jobName);
    
}
