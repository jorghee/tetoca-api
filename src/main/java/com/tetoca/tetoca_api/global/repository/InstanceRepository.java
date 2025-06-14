package com.tetoca.tetoca_api.global.repository;

import com.tetoca.tetoca_api.global.entity.InstanceEntity;
import com.tetoca.tetoca_api.global.entity.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {

  Optional<InstanceEntity> findByCompanyNameIgnoreCase(String name);

  Optional<InstanceEntity> findByCompanyRuc(String ruc);

  // Search by FK (Company)
  Optional<InstanceEntity> findByCompany(CompanyEntity company);
}
