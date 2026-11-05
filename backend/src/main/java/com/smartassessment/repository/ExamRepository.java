package com.smartassessment.repository;

import com.smartassessment.entity.Exam;
import com.smartassessment.entity.Institute;
import com.smartassessment.entity.User;
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

    Page<Exam> findByInstitute(Institute institute, Pageable pageable);

    long countByInstitute(Institute institute);

    @Query("SELECT e FROM Exam e WHERE e.published = true AND " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Exam> searchPublishedExams(@Param("categoryId") Long categoryId,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE e.published = true AND " +
           "(:institute IS NULL OR e.institute IS NULL OR e.institute = :institute) AND " +
           "(e.assignAllStudents = true OR :student MEMBER OF e.assignedCandidates) AND " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Exam> searchAvailableExamsForStudent(@Param("institute") Institute institute,
                                            @Param("student") User student,
                                            @Param("categoryId") Long categoryId,
                                            @Param("keyword") String keyword,
                                            Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Exam> searchAllExams(@Param("categoryId") Long categoryId,
                              @Param("keyword") String keyword,
                              Pageable pageable);

    @Query("SELECT e FROM Exam e WHERE " +
           "(:institute IS NULL OR e.institute = :institute) AND " +
           "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
           "(:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Exam> searchExamsForAdmin(@Param("institute") Institute institute,
                                  @Param("categoryId") Long categoryId,
                                  @Param("keyword") String keyword,
                                  Pageable pageable);

    List<Exam> findByCategoryId(Long categoryId);

    long countByPublishedTrue();
}
