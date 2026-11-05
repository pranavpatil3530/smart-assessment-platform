package com.smartassessment.repository;

import com.smartassessment.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    Page<Exam> findByPublishedTrue(Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE e.published = true AND " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Exam> searchPublishedExams(@Param("categoryId") Long categoryId,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Exam> searchAllExams(@Param("categoryId") Long categoryId,
                             @Param("keyword") String keyword,
                             Pageable pageable);

    List<Exam> findByCategoryId(Long categoryId);

    long countByPublishedTrue();
}
