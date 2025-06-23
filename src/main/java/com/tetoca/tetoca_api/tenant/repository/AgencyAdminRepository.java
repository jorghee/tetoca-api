package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.tenant.model.AgencyAdmin;

import java.util.List;

@Repository
public interface AgencyAdminRepository extends JpaRepository<AgencyAdmin, Integer> {

  /**
   * Verifica si un trabajador es administrador de una agencia específica.
   * Fundamental para las comprobaciones de @PreAuthorize.
   */
  boolean existsByWorker_IdAndAgency_IdAndRecordStatus(Integer workerId, Integer agencyId, String recordStatus);

  /**
   * Busca todas las asignaciones de administrador de agencia para un trabajador específico.
   *
   * @param workerId El ID del trabajador.
   * @return Una lista de asignaciones de AgencyAdmin.
   */
  List<AgencyAdmin> findByWorker_Id(Integer workerId);
}
