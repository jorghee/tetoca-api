package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceAlreadyExistsException;
import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.dto.request.AgencyCreateRequest;
import com.tetoca.tetoca_api.tenant.dto.AgencyResponse;
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
public class AgencyManagementService {

  private final AgencyRepository agencyRepository;
  private final DivisionRepository divisionRepository;
  private final WorkerRepository workerRepository;
  private final WorkerTypeRepository workerTypeRepository;
  private final AgencyAdminRepository agencyAdminRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public AgencyResponse createAgencyWithAdmin(AgencyCreateRequest request) {
    Integer divisionId = request.getAgency().getDivisionId();

    if (agencyRepository.existsByNameIgnoreCaseAndDivision_Id(request.getAgency().getName(), divisionId)) {
      throw new ResourceAlreadyExistsException("Ya existe una agencia con el nombre '" + 
          request.getAgency().getName() + "' en esta división.");
    }
    if (workerRepository.existsByEmail(request.getAdmin().getEmail())) {
      throw new ResourceAlreadyExistsException("Ya existe un trabajador con el email: " + request.getAdmin().getEmail());
    }

    Division division = divisionRepository.findById(divisionId)
      .orElseThrow(() -> new ResourceNotFoundException("Division", "id", divisionId));

    Agency agency = new Agency();
    agency.setName(request.getAgency().getName());
    agency.setAddress(request.getAgency().getAddress());
    agency.setReference(request.getAgency().getReference());
    agency.setDivision(division);
    agency.setRecordStatus("A");
    Agency savedAgency = agencyRepository.save(agency);

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
    
    AgencyAdmin agencyAdmin = new AgencyAdmin();
    agencyAdmin.setAgency(savedAgency);
    agencyAdmin.setWorker(savedAdminWorker);
    agencyAdmin.setAssignmentDate(getTodayDateInt());
    agencyAdmin.setRecordStatus("A");
    agencyAdminRepository.save(agencyAdmin);

    return mapToAgencyResponse(savedAgency);
  }

  private AgencyResponse mapToAgencyResponse(Agency agency) {
    return AgencyResponse.builder()
      .id(agency.getId())
      .name(agency.getName())
      .address(agency.getAddress())
      .reference(agency.getReference())
      .recordStatus(agency.getRecordStatus())
      .divisionId(agency.getDivision().getId())
      .build();
  }

  private Integer getTodayDateInt() {
    return Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
  }
}
