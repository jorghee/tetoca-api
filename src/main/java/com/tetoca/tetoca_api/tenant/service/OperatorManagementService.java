package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceAlreadyExistsException;
import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.dto.request.OperatorAssignmentRequest;
import com.tetoca.tetoca_api.tenant.dto.request.WorkerCreateRequest;
import com.tetoca.tetoca_api.tenant.model.*;
import com.tetoca.tetoca_api.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OperatorManagementService {

  private final WorkerRepository workerRepository;
  private final OperatorRepository operatorRepository;
  private final AgencyRepository agencyRepository;
  private final QueueRepository queueRepository;
  private final WorkerTypeRepository workerTypeRepository;
  private final OperatorAssignmentRepository assignmentRepository;
  private final PasswordEncoder passwordEncoder;

  @Transactional
  public Worker createOperator(WorkerCreateRequest request, Integer agencyId) {
    if (workerRepository.existsByEmail(request.getEmail())) {
      throw new ResourceAlreadyExistsException("Ya existe un trabajador con el email: " + request.getEmail());
    }

    Agency agency = agencyRepository.findById(agencyId)
      .orElseThrow(() -> new ResourceNotFoundException("Agency", "id", agencyId));

    WorkerType workerType = workerTypeRepository.findById(request.getWorkerTypeId())
      .orElseThrow(() -> new ResourceNotFoundException("WorkerType", "id", request.getWorkerTypeId()));

    Worker worker = new Worker();
    worker.setFullName(request.getFullName());
    worker.setEmail(request.getEmail());
    worker.setPassword(passwordEncoder.encode(request.getPassword()));
    worker.setPhone(request.getPhone());
    worker.setWorkerType(workerType);
    worker.setRecordStatus("A");
    Worker savedWorker = workerRepository.save(worker);
    
    Operator operator = new Operator();
    operator.setWorker(savedWorker);
    operator.setAgency(agency);
    operator.setRecordStatus("A");
    operatorRepository.save(operator);

    return savedWorker;
  }

  @Transactional
  public OperatorAssignment assignOperatorToQueue(OperatorAssignmentRequest request) {
    Operator operator = operatorRepository.findById(request.getWorkerId())
      .orElseThrow(() -> new ResourceNotFoundException("Operator", "workerId", request.getWorkerId()));

    Queue queue = queueRepository.findById(request.getQueueId())
      .orElseThrow(() -> new ResourceNotFoundException("Queue", "id", request.getQueueId()));
      
    // Validar que el operador y la cola pertenezcan a la misma agencia
    if (!operator.getAgency().getId().equals(queue.getAgency().getId())) {
        throw new IllegalArgumentException("El operario y la cola no pertenecen a la misma agencia.");
    }
      
    // Validar que la asignación no exista aún
    if (assignmentRepository.existsByOperator_WorkerIdAndQueue_IdAndRecordStatus(operator.getWorkerId(), queue.getId(), "A")) {
        throw new ResourceAlreadyExistsException("Este operario ya está asignado a esta cola.");
    }

    OperatorAssignment assignment = new OperatorAssignment();
    assignment.setOperator(operator);
    assignment.setQueue(queue);
    assignment.setRecordStatus("A");
    
    return assignmentRepository.save(assignment);
  }
}
