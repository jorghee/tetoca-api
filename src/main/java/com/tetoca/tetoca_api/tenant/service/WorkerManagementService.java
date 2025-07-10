package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceAlreadyExistsException;
import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.dto.request.WorkerCreateRequest;
import com.tetoca.tetoca_api.tenant.dto.WorkerResponse;
import com.tetoca.tetoca_api.tenant.model.CompanyAdmin;
import com.tetoca.tetoca_api.tenant.model.Worker;
import com.tetoca.tetoca_api.tenant.model.WorkerType;
import com.tetoca.tetoca_api.tenant.repository.CompanyAdminRepository;
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

    Worker worker = new Worker();
    worker.setFullName(request.getFullName());
    worker.setEmail(request.getEmail());
    worker.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar contraseña
    worker.setPhone(request.getPhone());
    worker.setWorkerType(workerType);
    worker.setRecordStatus("A");
    
    Worker savedWorker = workerRepository.save(worker);

    // Asignar el rol de CompanyAdmin
    CompanyAdmin companyAdmin = new CompanyAdmin();
    companyAdmin.setWorker(savedWorker);
    companyAdmin.setAssignmentDate(getTodayDateInt());
    companyAdmin.setRecordStatus("A");
    
    companyAdminRepository.save(companyAdmin);

    return mapToWorkerResponse(savedWorker, Set.of("COMPANY_ADMIN"));
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
