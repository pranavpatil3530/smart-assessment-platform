package com.smartassessment.repository;

import com.smartassessment.entity.AttemptResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptResponseRepository extends JpaRepository<AttemptResponse, Long> {

    List<AttemptResponse> findByExamAttemptId(Long attemptId);

    Optional<AttemptResponse> findByExamAttemptIdAndQuestionId(Long attemptId, Long questionId);
}
