package com.tetoca.tetoca_api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.global.model.CompanyEntity;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, Integer> {

  Optional<CompanyEntity> findByNameIgnoreCase(String name);

  boolean existsByNameIgnoreCase(String name);

  boolean existsByRuc(String ruc);
}
