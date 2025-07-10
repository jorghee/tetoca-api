package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.common.exception.ResourceNotFoundException;
import com.tetoca.tetoca_api.tenant.dto.request.QueueRequest;
import com.tetoca.tetoca_api.tenant.dto.request.ScheduleRequest;
import com.tetoca.tetoca_api.tenant.model.*;
import com.tetoca.tetoca_api.tenant.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QueueManagementService {

  private final QueueRepository queueRepository;
  private final AgencyRepository agencyRepository;
  private final QueueTypeRepository queueTypeRepository;
  private final QueueStatusRepository queueStatusRepository;
  private final ScheduleRepository scheduleRepository;

  @Transactional
  public Queue createQueue(QueueRequest request) {
    Agency agency = findAgencyById(request.getAgencyId());
    QueueType type = findQueueTypeById(request.getQueueTypeId());
    QueueStatus status = findQueueStatusById(request.getQueueStatusId());

    Queue queue = new Queue();
    mapRequestToQueue(request, queue, agency, type, status);
    
    Queue savedQueue = queueRepository.save(queue);
    
    if (request.getSchedules() != null && !request.getSchedules().isEmpty()) {
      createSchedulesForQueue(request.getSchedules(), savedQueue);
    }
    
    return savedQueue;
  }

  @Transactional
  public Queue updateQueue(Integer queueId, QueueRequest request) {
    Queue queue = queueRepository.findById(queueId)
      .orElseThrow(() -> new ResourceNotFoundException("Queue", "id", queueId));

    Agency agency = findAgencyById(request.getAgencyId());
    QueueType type = findQueueTypeById(request.getQueueTypeId());
    QueueStatus status = findQueueStatusById(request.getQueueStatusId());

    mapRequestToQueue(request, queue, agency, type, status);

    // Eliminar horarios antiguos y crear los nuevos
    scheduleRepository.deleteAll(scheduleRepository.findByQueue(queue));
    if (request.getSchedules() != null && !request.getSchedules().isEmpty()) {
      createSchedulesForQueue(request.getSchedules(), queue);
    }
    
    return queueRepository.save(queue);
  }

  @Transactional
  public void deleteQueue(Integer queueId) {
    Queue queue = queueRepository.findById(queueId)
      .orElseThrow(() -> new ResourceNotFoundException("Queue", "id", queueId));
    
    // Eliminación lógica
    queue.setRecordStatus("*");
    queueRepository.save(queue);
  }

  private void mapRequestToQueue(QueueRequest request, Queue queue, Agency agency, QueueType type, QueueStatus status) {
    queue.setName(request.getName());
    queue.setAgency(agency);
    queue.setQueueType(type);
    queue.setQueueStatus(status);
    queue.setMaxCapacity(request.getMaxCapacity());
    queue.setEstimatedTimePerTurn(request.getEstimatedTimePerTurn());
  }

  private void createSchedulesForQueue(List<ScheduleRequest> scheduleRequests, Queue queue) {
    List<Schedule> schedules = scheduleRequests.stream().map(req -> {
      Schedule schedule = new Schedule();
      schedule.setQueue(queue);
      schedule.setDayOfWeek(req.getDayOfWeek());
      schedule.setStartTime(req.getStartTime());
      schedule.setEndTime(req.getEndTime());
      schedule.setRecordStatus("A");
      return schedule;
    }).collect(Collectors.toList());
    scheduleRepository.saveAll(schedules);
  }
  
  private Agency findAgencyById(Integer id) {
    return agencyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Agency", "id", id));
  }

  private QueueType findQueueTypeById(Integer id) {
    return queueTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("QueueType", "id", id));
  }

  private QueueStatus findQueueStatusById(Integer id) {
    return queueStatusRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("QueueStatus", "id", id));
  }
}
