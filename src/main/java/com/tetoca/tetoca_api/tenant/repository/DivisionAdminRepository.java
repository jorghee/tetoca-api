package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.tenant.model.DivisionAdmin;

import java.util.List;

@Repository
public interface DivisionAdminRepository extends JpaRepository<DivisionAdmin, Integer> {

  /**
   * Verifica si un trabajador es administrador de una división específica.
   */
  boolean existsByWorker_IdAndDivision_IdAndRecordStatus(Integer workerId, Integer divisionId, String recordStatus);

  /**
   * Busca todas las asignaciones de administrador de división para un trabajador específico.
   *
   * @param workerId El ID del trabajador.
   * @return Una lista de asignaciones de DivisionAdmin.
   */
  List<DivisionAdmin> findByWorker_Id(Integer workerId);
}
