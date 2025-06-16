package com.tetoca.tetoca_api.tenant.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tetoca.tetoca_api.tenant.models.Operator;
import com.tetoca.tetoca_api.tenant.models.OperatorAssignment;
import com.tetoca.tetoca_api.tenant.models.Queue;

import java.util.List;

@Repository
public interface OperatorAssignmentRepository extends JpaRepository<OperatorAssignment, Long> {
    
    List<OperatorAssignment> findByQueue(Queue queue);
    
    List<OperatorAssignment> findByOperator(Operator operator);
    
    List<OperatorAssignment> findByRecordStatus(String recordStatus);
    
    @Query("SELECT oa FROM OperatorAssignment oa WHERE oa.queue = :queue AND (oa.endDate IS NULL OR oa.endDate >= :currentDate)")
    List<OperatorAssignment> findActiveByQueue(@Param("queue") Queue queue, @Param("currentDate") Integer currentDate);
    
    @Query("SELECT oa FROM OperatorAssignment oa WHERE oa.operator = :operator AND (oa.endDate IS NULL OR oa.endDate >= :currentDate)")
    List<OperatorAssignment> findActiveByOperator(@Param("operator") Operator operator, @Param("currentDate") Integer currentDate);
}
