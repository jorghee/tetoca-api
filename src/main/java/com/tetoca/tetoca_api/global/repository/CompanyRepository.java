package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {
}
