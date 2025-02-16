package com.portfolio.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.portfolio.demo.model.Picture;

public interface PictureRepository extends JpaRepository<Picture, Long>{
    
}
