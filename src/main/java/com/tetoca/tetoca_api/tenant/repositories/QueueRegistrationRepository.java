package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.Client;
import com.tetoca.tetoca_api.tenant.models.Queue;
import com.tetoca.tetoca_api.tenant.models.QueueRegistration;

import java.util.List;

@Repository
public interface QueueRegistrationRepository extends JpaRepository<QueueRegistration, Long> {
    
    List<QueueRegistration> findByQueue(Queue queue);
    
    List<QueueRegistration> findByClient(Client client);
    
    List<QueueRegistration> findByQueueAndClient(Queue queue, Client client);
    
    List<QueueRegistration> findByRegistrationDateTimeBetween(Long startDateTime, Long endDateTime);
    
    List<QueueRegistration> findByRegistrationMethod(String registrationMethod);
    
    List<QueueRegistration> findByRecordStatus(String recordStatus);
    
    List<QueueRegistration> findByQueueIdOrderByRegistrationDateTime(Integer queueId);
}
