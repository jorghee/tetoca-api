package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.Agency;
import com.tetoca.tetoca_api.tenant.models.Division;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgencyRepository extends JpaRepository<Agency, Integer> {
    
    Optional<Agency> findByName(String name);
    
    List<Agency> findByDivision(Division division);
    
    List<Agency> findByRecordStatus(String recordStatus);
    
    // Buscar agencias activas con paginación
    Page<Agency> findByRecordStatusOrderByName(String recordStatus, Pageable pageable);
    
    List<Agency> findByDivisionAndRecordStatus(Division division, String recordStatus);
    
    List<Agency> findByNameContainingIgnoreCase(String name);
    
    // Buscar por texto con paginación
    @Query("SELECT a FROM Agency a WHERE a.recordStatus = 'A' AND " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    Page<Agency> findBySearchTextAndActive(@Param("searchText") String searchText, Pageable pageable);
    
    // Contar colas activas por agencia
    @Query("SELECT COUNT(q) FROM Queue q WHERE q.agency.id = :agencyId AND q.recordStatus = 'A'")
    Integer countActiveQueuesByAgencyId(@Param("agencyId") Integer agencyId);
    
    boolean existsByNameAndDivision(String name, Division division);
}