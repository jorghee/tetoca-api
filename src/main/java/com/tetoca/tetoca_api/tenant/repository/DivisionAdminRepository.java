package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.tenant.model.DivisionAdmin;

@Repository
public interface DivisionAdminRepository extends JpaRepository<DivisionAdmin, Integer> {

  /**
   * Verifica si un trabajador es administrador de una división específica.
   */
  boolean existsByWorker_IdAndDivision_IdAndRecordStatus(Integer workerId, Integer divisionId, String recordStatus);
}
