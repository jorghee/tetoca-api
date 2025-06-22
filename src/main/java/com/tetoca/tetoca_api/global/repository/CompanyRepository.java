package com.tetoca.tetoca_api.global.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import com.tetoca.tetoca_api.global.model.Company;
import com.tetoca.tetoca_api.global.dto.company.CompanyResponse;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Integer> {

  Optional<Company> findByNameIgnoreCase(String name);

  Optional<Company> findByRuc(String ruc);

  boolean existsByNameIgnoreCase(String name);

  boolean existsByRuc(String ruc);

  Optional<Company> findByIdAndRecordStatusNot(Integer id, String recordStatus);

  @Query("SELECT new com.tetoca.tetoca_api.global.dto.company.CompanyResponse(" +
         "c.id, c.name, c.ruc, c.email, c.companyCategory.name, c.companyState.name, i.tenantId, c.recordStatus, c.registerDate) " +
         "FROM Company c JOIN Instance i ON c.id = i.company.id " +
         "WHERE c.recordStatus <> :excludedStatus")
  List<CompanyResponse> findAllCompaniesAsResponse(String excludedStatus);
}
