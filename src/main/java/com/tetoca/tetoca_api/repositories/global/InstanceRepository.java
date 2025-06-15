package com.tetoca.tetoca_api.repositories.global;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.models.global.CompanyEntity;
import com.tetoca.tetoca_api.models.global.InstanceEntity;

import java.util.Optional;

@Repository
public interface InstanceRepository extends JpaRepository<InstanceEntity, Long> {

  Optional<InstanceEntity> findByCompanyNameIgnoreCase(String name);

  Optional<InstanceEntity> findByCompanyRuc(String ruc);

  // Search by FK (Company)
  Optional<InstanceEntity> findByCompany(CompanyEntity company);
}
