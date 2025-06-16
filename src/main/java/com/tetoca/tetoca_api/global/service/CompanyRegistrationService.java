package com.tetoca.tetoca_api.global.service;

import com.tetoca.tetoca_api.global.dto.company.CompanyRegisterRequest;
import com.tetoca.tetoca_api.global.model.*;
import com.tetoca.tetoca_api.global.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CompanyRegistrationService {

  private final CompanyRepository companyRepository;
  private final InstanceRepository instanceRepository;
  private final CompanyStateRepository companyStateRepository;
  private final CompanyCategoryRepository companyCategoryRepository;
  private final DatabaseTypeRepository databaseTypeRepository;
  private final ConnectionStateRepository connectionStateRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Transactional
  public void registerCompany(CompanyRegisterRequest request) {

    // Search references
    CompanyStateEntity companyState = companyStateRepository.findById(request.getStatusCode())
      .orElseThrow(() -> new IllegalArgumentException("Invalid company state code"));

    CompanyCategoryEntity category = companyCategoryRepository.findById(request.getCategoryCode())
      .orElseThrow(() -> new IllegalArgumentException("Invalid category code"));

    DatabaseTypeEntity dbType = databaseTypeRepository.findById(request.getDbTypeCode())
      .orElseThrow(() -> new IllegalArgumentException("Invalid DB type code"));

    ConnectionStateEntity connectionState = connectionStateRepository.findById(request.getConnectionStateCode())
      .orElseThrow(() -> new IllegalArgumentException("Invalid connection state code"));

    // Create company
    CompanyEntity company = CompanyEntity.builder()
      .companyState(companyState)
      .companyCategory(category)
      .name(request.getName())
      .ruc(request.getRuc())
      .email(request.getEmail())
      .registerDate(getTodayDateInt())
      .recordStatus("A")
      .build();

    companyRepository.save(company);

    // Create instance
    InstanceEntity instance = InstanceEntity.builder()
      .company(company)
      .connectionState(connectionState)
      .dbType(dbType)
      .dbName(request.getDbName())
      .dbUri(request.getDbUri())
      .dbUser(request.getDbUser())
      .dbPassword(request.getDbPassword())
      .tenantId(request.getTenantId())
      .lastActivationDate(getTodayDateInt())
      .recordStatus("A")
      .build();

    instanceRepository.save(instance);

    // Create phisical database if not exists
    createDatabaseIfNotExists(request.getDbName());
  }

  private Integer getTodayDateInt() {
    return Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
  }

  private void createDatabaseIfNotExists(String dbName) {
    String sql = "SELECT 1 FROM pg_database WHERE datname = :dbName";
    Object exists = entityManager
      .createNativeQuery(sql)
      .setParameter("dbName", dbName)
      .getResultList();

    if (exists == null) {
      entityManager.createNativeQuery("CREATE DATABASE " + dbName).executeUpdate();
    }
  }
}
