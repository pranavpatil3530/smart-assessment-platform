package com.smartassessment.repository;

import com.smartassessment.entity.ApprovalStatus;
import com.smartassessment.entity.Institute;
import com.smartassessment.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InstituteRepository extends JpaRepository<Institute, Long> {

    Optional<Institute> findByCode(String code);

    Optional<Institute> findByAdminUser(User adminUser);

    Page<Institute> findByStatus(ApprovalStatus status, Pageable pageable);

    long countByStatus(ApprovalStatus status);

    boolean existsByCode(String code);
}
