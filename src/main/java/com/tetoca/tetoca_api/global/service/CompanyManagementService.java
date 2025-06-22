package com.tetoca.tetoca_api.global.service;

import com.tetoca.tetoca_api.common.exception.ResourceAlreadyExistsException;
import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.global.dto.company.CompanyRegistrationRequest;
import com.tetoca.tetoca_api.global.dto.company.CompanyRequest;
import com.tetoca.tetoca_api.global.dto.company.CompanyResponse;
import com.tetoca.tetoca_api.global.dto.company.InstanceRequest;
import com.tetoca.tetoca_api.global.event.CompanyRegistrationEvent;
import com.tetoca.tetoca_api.global.model.*;
import com.tetoca.tetoca_api.global.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyManagementService {

  private final CompanyRepository companyRepository;
  private final InstanceRepository instanceRepository;
  private final CompanyStateRepository companyStateRepository;
  private final CompanyCategoryRepository companyCategoryRepository;
  private final DatabaseTypeRepository databaseTypeRepository;
  private final ConnectionStateRepository connectionStateRepository;

  private final ApplicationEventPublisher eventPublisher;

  private static final Integer ACTIVE_COMPANY_STATE = 1;
  private static final Integer PENDING_CONNECTION_STATE = 1;
  private static final String STATUS_ACTIVE = "A";
  private static final String STATUS_DELETED = "*";

  @Transactional
  public CompanyResponse registerCompany(CompanyRegistrationRequest request) {
    CompanyRequest companyRequest = request.getCompany();
    InstanceRequest instanceRequest = request.getInstance();

    validateCompanyUniqueness(companyRequest.getName(), companyRequest.getRuc());
    validateInstanceUniqueness(instanceRequest.getTenantId());

    CompanyState activeState = findCompanyStateById(ACTIVE_COMPANY_STATE);
    CompanyCategory category = findCompanyCategoryById(companyRequest.getCategoryCode());
    DatabaseType dbType = findDatabaseTypeById(instanceRequest.getDbTypeCode());
    ConnectionState pendingConnection = findConnectionStateById(PENDING_CONNECTION_STATE);

    Company company = Company.builder()
      .companyState(activeState)
      .companyCategory(category)
      .name(companyRequest.getName())
      .ruc(companyRequest.getRuc())
      .email(companyRequest.getEmail())
      .registerDate(getTodayDateInt())
      .recordStatus(STATUS_ACTIVE)
      .build();
    Company savedCompany = companyRepository.save(company);

    Instance instance = Instance.builder()
      .company(savedCompany)
      .connectionState(pendingConnection)
      .dbType(dbType)
      .dbName(instanceRequest.getDbName())
      .dbUri(instanceRequest.getDbUri())
      .dbUser(instanceRequest.getDbUser())
      .dbPassword(instanceRequest.getDbPassword())
      .tenantId(instanceRequest.getTenantId())
      .lastActivationDate(getTodayDateInt())
      .recordStatus(STATUS_ACTIVE)
      .build();
    instanceRepository.save(instance);

    eventPublisher.publishEvent(new CompanyRegistrationEvent(
      this,
      instance.getTenantId(),
      instance.getDbName(),
      instance.getDbUri(),
      instance.getDbUser(),
      instance.getDbPassword()
    ));

    return mapToCompanyResponse(savedCompany, instance.getTenantId());
  }

  public CompanyResponse getCompanyById(Integer id) {
    Company company = findActiveCompanyById(id);
    Instance instance = findInstanceByCompanyId(id);
    return mapToCompanyResponse(company, instance.getTenantId());
  }

  public List<CompanyResponse> getAllCompanies() {
    return companyRepository.findAllCompaniesAsResponse(STATUS_DELETED);
  }

  @Transactional
  public CompanyResponse updateCompany(Integer id, CompanyRequest request) {
    Company company = findActiveCompanyById(id);
    validateCompanyUniquenessOnUpdate(request.getName(), request.getRuc(), id);

    CompanyCategory category = findCompanyCategoryById(request.getCategoryCode());
    
    company.setName(request.getName());
    company.setRuc(request.getRuc());
    company.setEmail(request.getEmail());
    company.setCompanyCategory(category);
    
    Company updatedCompany = companyRepository.save(company);
    Instance instance = findInstanceByCompanyId(id);

    return mapToCompanyResponse(updatedCompany, instance.getTenantId());
  }

  @Transactional
  public void deleteCompany(Integer id) {
    Company company = findActiveCompanyById(id);
    company.setRecordStatus(STATUS_DELETED);
    
    // También marcamos la instancia como eliminada lógicamente
    Instance instance = findInstanceByCompanyId(id);
    instance.setRecordStatus(STATUS_DELETED);
    
    companyRepository.save(company);
    instanceRepository.save(instance);
  }


  private void validateCompanyUniqueness(String name, String ruc) {
    if (companyRepository.existsByNameIgnoreCase(name)) {
      throw new ResourceAlreadyExistsException("Ya existe una empresa con el nombre: " + name);
    }
    if (companyRepository.existsByRuc(ruc)) {
      throw new ResourceAlreadyExistsException("Ya existe una empresa con el RUC: " + ruc);
    }
  }

  private void validateCompanyUniquenessOnUpdate(String name, String ruc, Integer companyId) {
    companyRepository.findByNameIgnoreCase(name).ifPresent(c -> {
      if (!c.getId().equals(companyId)) {
        throw new ResourceAlreadyExistsException("El nombre '" + name + "' ya está en uso por otra empresa.");
      }
    });
    companyRepository.findByRuc(ruc).ifPresent(c -> {
      if (!c.getId().equals(companyId)) {
        throw new ResourceAlreadyExistsException("El RUC '" + ruc + "' ya está en uso por otra empresa.");
      }
    });
  }

  private void validateInstanceUniqueness(String tenantId) {
    if (instanceRepository.findByTenantIdIgnoreCase(tenantId).isPresent()) {
      throw new ResourceAlreadyExistsException("El Tenant ID '" + tenantId + "' ya está en uso.");
    }
  }
  
  private Company findActiveCompanyById(Integer id) {
    return companyRepository.findByIdAndRecordStatusNot(id, STATUS_DELETED)
      .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + id));
  }
  
  private Instance findInstanceByCompanyId(Integer companyId) {
    return instanceRepository.findByCompany_Id(companyId)
      .orElseThrow(() -> new ResourceNotFoundException("Instancia no encontrada para la empresa con ID: " + companyId));
  }

  private CompanyState findCompanyStateById(Integer id) {
    return companyStateRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Estado de empresa no encontrado con ID: " + id));
  }

  private CompanyCategory findCompanyCategoryById(Integer id) {
    return companyCategoryRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Categoría de empresa no encontrada con ID: " + id));
  }

  private DatabaseType findDatabaseTypeById(Integer id) {
    return databaseTypeRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Tipo de base de datos no encontrado con ID: " + id));
  }

  private ConnectionState findConnectionStateById(Integer id) {
    return connectionStateRepository.findById(id)
      .orElseThrow(() -> new ResourceNotFoundException("Estado de conexión no encontrado con ID: " + id));
  }

  private Integer getTodayDateInt() {
    return Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
  }
  
  private CompanyResponse mapToCompanyResponse(Company company, String tenantId) {
    return CompanyResponse.builder()
      .id(company.getId())
      .name(company.getName())
      .ruc(company.getRuc())
      .email(company.getEmail())
      .category(company.getCompanyCategory().getName())
      .state(company.getCompanyState().getName())
      .tenantId(tenantId)
      .recordStatus(company.getRecordStatus())
      .registerDate(company.getRegisterDate())
      .build();
  }
}
