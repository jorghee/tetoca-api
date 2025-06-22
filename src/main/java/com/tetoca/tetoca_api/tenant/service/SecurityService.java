package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.repository.AgencyAdminRepository;
import com.tetoca.tetoca_api.tenant.repository.DivisionAdminRepository;
import com.tetoca.tetoca_api.tenant.repository.OperatorAssignmentRepository;
import com.tetoca.tetoca_api.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

  private final AgencyAdminRepository agencyAdminRepository;
  private final DivisionAdminRepository divisionAdminRepository;
  private final OperatorAssignmentRepository operatorAssignmentRepository;
  
  private UserDetailsImpl getUserDetails(Authentication authentication) {
    return (UserDetailsImpl) authentication.getPrincipal();
  }

  public boolean isWorkerAdminOfAgency(Authentication authentication, Integer agencyId) {
    Integer workerId = getUserDetails(authentication).getId();
    return agencyAdminRepository.existsByWorker_IdAndAgency_IdAndRecordStatus(workerId, agencyId, "A");
  }

  public boolean isWorkerAdminOfDivision(Authentication authentication, Integer divisionId) {
    Integer workerId = getUserDetails(authentication).getId();
    return divisionAdminRepository.existsByWorker_IdAndDivision_IdAndRecordStatus(workerId, divisionId, "A");
  }

  public boolean isOperatorAssignedToQueue(Authentication authentication, Integer queueId) {
    Integer workerId = getUserDetails(authentication).getId();
    return operatorAssignmentRepository.existsByOperator_WorkerIdAndQueue_IdAndRecordStatus(workerId, queueId, "A");
  }
}
