package com.decisify.decision.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.decisify.decision.domain.Alternative;

public interface AlternativeRepository extends JpaRepository<Alternative, UUID>{
    
}
