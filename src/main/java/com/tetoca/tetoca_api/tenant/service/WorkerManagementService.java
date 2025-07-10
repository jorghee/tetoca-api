package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceAlreadyExistsException;
import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.dto.request.WorkerCreateRequest;
import com.tetoca.tetoca_api.tenant.dto.WorkerResponse;
import com.tetoca.tetoca_api.tenant.model.CompanyAdmin;
import com.tetoca.tetoca_api.tenant.model.Division;
import com.tetoca.tetoca_api.tenant.model.DivisionAdmin;
import com.tetoca.tetoca_api.tenant.model.Worker;
import com.tetoca.tetoca_api.tenant.model.WorkerType;
import com.tetoca.tetoca_api.tenant.repository.CompanyAdminRepository;
import com.tetoca.tetoca_api.tenant.repository.DivisionAdminRepository;
import com.tetoca.tetoca_api.tenant.repository.DivisionRepository;
import com.tetoca.tetoca_api.tenant.repository.WorkerRepository;
import com.tetoca.tetoca_api.tenant.repository.WorkerTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class WorkerManagementService {

  private final WorkerRepository workerRepository;
  private final WorkerTypeRepository workerTypeRepository;
  private final CompanyAdminRepository companyAdminRepository;
  private final DivisionAdminRepository divisionAdminRepository;
  private final DivisionRepository divisionRepository;   
  private final PasswordEncoder passwordEncoder;

  /**
   * Crea el primer (o subsiguientes) Administrador de Empresa.
   * Esta operación es transaccional.
   * @param request Datos para la creación del trabajador.
   * @return Un DTO con la información del trabajador y su nuevo rol.
   */
  @Transactional
  public WorkerResponse createCompanyAdmin(WorkerCreateRequest request) {
    if (workerRepository.existsByEmail(request.getEmail())) {
      throw new ResourceAlreadyExistsException("Ya existe un trabajador con el email: " + request.getEmail());
    }

    WorkerType workerType = workerTypeRepository.findById(request.getWorkerTypeId())
      .orElseThrow(() -> new ResourceNotFoundException("WorkerType", "id", request.getWorkerTypeId()));

    Worker worker = createNewWorker(request, workerType);
    Worker savedWorker = workerRepository.save(worker);

    // Asignar el rol de CompanyAdmin
    CompanyAdmin companyAdmin = new CompanyAdmin();
    companyAdmin.setWorker(savedWorker);
    companyAdmin.setAssignmentDate(getTodayDateInt());
    companyAdmin.setRecordStatus("A");
    
    companyAdminRepository.save(companyAdmin);

    return mapToWorkerResponse(savedWorker, Set.of("COMPANY_ADMIN"));
  }

  /**
   * Crea un nuevo trabajador y le asigna el rol de Administrador de División.
   *
   * @param request    Los datos del nuevo trabajador a crear.
   * @param divisionId El ID de la división a la que se le asignará.
   * @return Un DTO con la información del trabajador y su nuevo rol.
   */
  @Transactional
  public WorkerResponse createDivisionAdmin(WorkerCreateRequest request, Integer divisionId) {
    if (workerRepository.existsByEmail(request.getEmail())) {
      throw new ResourceAlreadyExistsException("Ya existe un trabajador con el email: " + request.getEmail());
    }

    Division division = divisionRepository.findById(divisionId)
      .orElseThrow(() -> new ResourceNotFoundException("Division", "id", divisionId));

    WorkerType workerType = workerTypeRepository.findById(request.getWorkerTypeId())
      .orElseThrow(() -> new ResourceNotFoundException("WorkerType", "id", request.getWorkerTypeId()));

    Worker worker = createNewWorker(request, workerType);
    Worker savedWorker = workerRepository.save(worker);

    // Asignar el rol de DivisionAdmin
    DivisionAdmin divisionAdmin = new DivisionAdmin();
    divisionAdmin.setWorker(savedWorker);
    divisionAdmin.setDivision(division); // Enlazar a la división correcta
    divisionAdmin.setAssignmentDate(getTodayDateInt());
    divisionAdmin.setRecordStatus("A");
    
    divisionAdminRepository.save(divisionAdmin);

    return mapToWorkerResponse(savedWorker, Set.of("DIVISION_ADMIN"));
  }
  
  private Worker createNewWorker(WorkerCreateRequest request, WorkerType workerType) {
    Worker worker = new Worker();
    worker.setFullName(request.getFullName());
    worker.setEmail(request.getEmail());
    worker.setPassword(passwordEncoder.encode(request.getPassword()));
    worker.setPhone(request.getPhone());
    worker.setWorkerType(workerType);
    worker.setRecordStatus("A");
    return worker;
  }

  private WorkerResponse mapToWorkerResponse(Worker worker, Set<String> roles) {
    return WorkerResponse.builder()
      .id(worker.getId())
      .fullName(worker.getFullName())
      .email(worker.getEmail())
      .phone(worker.getPhone())
      .workerType(worker.getWorkerType().getName())
      .recordStatus(worker.getRecordStatus())
      .roles(roles)
      .build();
  }

  private Integer getTodayDateInt() {
    return Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
  }
}
