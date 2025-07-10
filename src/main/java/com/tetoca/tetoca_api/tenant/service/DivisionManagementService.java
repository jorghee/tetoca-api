package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceAlreadyExistsException;
import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.dto.request.DivisionCreateRequest;
import com.tetoca.tetoca_api.tenant.dto.DivisionResponse;
import com.tetoca.tetoca_api.tenant.model.*;
import com.tetoca.tetoca_api.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class DivisionManagementService {

  private final DivisionRepository divisionRepository;
  private final WorkerRepository workerRepository;
  private final WorkerTypeRepository workerTypeRepository;
  private final DivisionAdminRepository divisionAdminRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public DivisionResponse createDivisionWithAdmin(DivisionCreateRequest request) {
    if (divisionRepository.existsByNameIgnoreCase(request.getDivision().getName())) {
      throw new ResourceAlreadyExistsException("Ya existe una división con el nombre: " + request.getDivision().getName());
    }

    if (workerRepository.existsByEmail(request.getAdmin().getEmail())) {
      throw new ResourceAlreadyExistsException("Ya existe un trabajador con el email: " + request.getAdmin().getEmail());
    }

    Division division = new Division();
    division.setName(request.getDivision().getName());
    division.setDescription(request.getDivision().getDescription());
    division.setRecordStatus("A");
    Division savedDivision = divisionRepository.save(division);

    WorkerType workerType = workerTypeRepository.findById(request.getAdmin().getWorkerTypeId())
      .orElseThrow(() -> new ResourceNotFoundException("WorkerType", "id", request.getAdmin().getWorkerTypeId()));
    
    Worker adminWorker = new Worker();
    adminWorker.setFullName(request.getAdmin().getFullName());
    adminWorker.setEmail(request.getAdmin().getEmail());
    adminWorker.setPassword(passwordEncoder.encode(request.getAdmin().getPassword()));
    adminWorker.setPhone(request.getAdmin().getPhone());
    adminWorker.setWorkerType(workerType);
    adminWorker.setRecordStatus("A");
    Worker savedAdminWorker = workerRepository.save(adminWorker);

    DivisionAdmin divisionAdmin = new DivisionAdmin();
    divisionAdmin.setDivision(savedDivision);
    divisionAdmin.setWorker(savedAdminWorker);
    divisionAdmin.setAssignmentDate(getTodayDateInt());
    divisionAdmin.setRecordStatus("A");
    divisionAdminRepository.save(divisionAdmin);

    return mapToDivisionResponse(savedDivision);
  }

  private DivisionResponse mapToDivisionResponse(Division division) {
    return DivisionResponse.builder()
      .id(division.getId())
      .name(division.getName())
      .description(division.getDescription())
      .recordStatus(division.getRecordStatus())
      .build();
  }

  private Integer getTodayDateInt() {
    return Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
  }
}
