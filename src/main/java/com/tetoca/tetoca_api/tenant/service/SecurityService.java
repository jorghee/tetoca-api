package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.model.Queue; 
import com.tetoca.tetoca_api.tenant.repository.AgencyAdminRepository;
import com.tetoca.tetoca_api.tenant.repository.DivisionAdminRepository;
import com.tetoca.tetoca_api.tenant.repository.OperatorAssignmentRepository;
import com.tetoca.tetoca_api.tenant.repository.QueueRepository; 
import com.tetoca.tetoca_api.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("securityService")
@RequiredArgsConstructor
public class SecurityService {

  private final AgencyAdminRepository agencyAdminRepository;
  private final DivisionAdminRepository divisionAdminRepository;
  private final OperatorAssignmentRepository operatorAssignmentRepository;
  private final QueueRepository queueRepository;
  
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

  /**
   * Verifica si el usuario autenticado (un AGENCY_ADMIN) tiene permiso para gestionar
   * una cola específica. Lo hace comprobando si el usuario es admin de la agencia
   * a la que pertenece la cola.
   *
   * @param authentication El objeto de autenticación de Spring.
   * @param queueId El ID de la cola a verificar.
   * @return true si el usuario es admin de la agencia de la cola, false en caso contrario.
   */
  public boolean canAdminManageQueue(Authentication authentication, Integer queueId) {
    UserDetailsImpl user = getUserDetails(authentication);
    if (user == null || queueId == null) return false;

    Optional<Queue> queueOpt = queueRepository.findById(queueId);
    if (queueOpt.isEmpty()) {
      return false; // La cola no existe, la autorización falla por defecto.
    }

    Integer agencyId = queueOpt.get().getAgency().getId();
    return isWorkerAdminOfAgency(authentication, agencyId);
  }
}
