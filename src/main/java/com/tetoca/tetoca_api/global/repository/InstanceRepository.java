package com.tetoca.tetoca_api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.global.model.Instance;

import java.util.Optional;

@Repository
public interface InstanceRepository extends JpaRepository<Instance, Long> {

  Optional<Instance> findByTenantIdIgnoreCase(String tenantId);

  Optional<Instance> findByCompany_Id(Integer companyId);

  boolean existsByTenantIdIgnoreCase(String tenantId);
}
