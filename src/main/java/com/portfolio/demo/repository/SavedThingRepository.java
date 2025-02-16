package com.portfolio.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.demo.model.SavedThing;

public interface SavedThingRepository extends JpaRepository<SavedThing, Long> {
    
}
