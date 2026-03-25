package com.example.artwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.artwork.model.Judge;

@Repository
public interface JudgeRepository extends JpaRepository<Judge, Long> {
	
	Judge findFirstByOrderByIdAsc();

}


