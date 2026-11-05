package com.smartassessment.repository;

import com.smartassessment.entity.ApprovalStatus;
import com.smartassessment.entity.Institute;
import com.smartassessment.entity.Role;
import com.smartassessment.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    long countByRole(Role role);

    Page<User> findByRole(Role role, Pageable pageable);

    List<User> findByInstituteAndRole(Institute institute, Role role);

    Page<User> findByInstituteAndRole(Institute institute, Role role, Pageable pageable);

    long countByInstituteAndRole(Institute institute, Role role);

    Page<User> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);

    long countByApprovalStatus(ApprovalStatus approvalStatus);
}
