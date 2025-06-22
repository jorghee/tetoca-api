package com.tetoca.tetoca_api.tenant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.tetoca.tetoca_api.tenant.model.Operator;

import java.util.List;

@Repository
public interface OperatorRepository extends JpaRepository<Operator, Integer> {
    
  /**
   * Busca todos los operarios activos de una agencia.
   */
  List<Operator> findAllByAgency_IdAndRecordStatus(Integer agencyId, String recordStatus);
}
