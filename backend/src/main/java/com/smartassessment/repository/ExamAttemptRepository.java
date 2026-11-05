package com.smartassessment.repository;

import com.smartassessment.entity.AttemptStatus;
import com.smartassessment.entity.ExamAttempt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamAttemptRepository extends JpaRepository<ExamAttempt, Long> {

    Page<ExamAttempt> findByUserIdOrderByStartTimeDesc(Long userId, Pageable pageable);

    List<ExamAttempt> findByUserIdAndExamId(Long userId, Long examId);

    long countByUserIdAndExamId(Long userId, Long examId);

    Optional<ExamAttempt> findFirstByUserIdAndExamIdAndStatus(Long userId, Long examId, AttemptStatus status);

    Page<ExamAttempt> findByExamIdOrderByStartTimeDesc(Long examId, Pageable pageable);

    @Query("SELECT COUNT(ea) FROM ExamAttempt ea WHERE ea.passed = true")
    long countPassedAttempts();

    @Query("SELECT AVG(ea.percentage) FROM ExamAttempt ea WHERE ea.status IN ('SUBMITTED', 'AUTO_SUBMITTED')")
    Double calculateOverallAveragePercentage();

    @Query("SELECT ea FROM ExamAttempt ea WHERE ea.exam.id = :examId AND ea.status IN ('SUBMITTED', 'AUTO_SUBMITTED') ORDER BY ea.score DESC")
    List<ExamAttempt> findTopPerformersByExamId(@Param("examId") Long examId, Pageable pageable);
}
