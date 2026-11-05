package com.smartassessment.config;

import com.smartassessment.entity.*;
import com.smartassessment.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final InstituteRepository instituteRepository;
    private final CategoryRepository categoryRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           InstituteRepository instituteRepository,
                           CategoryRepository categoryRepository,
                           ExamRepository examRepository,
                           ExamAttemptRepository examAttemptRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.instituteRepository = instituteRepository;
        this.categoryRepository = categoryRepository;
        this.examRepository = examRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        log.info("Ensuring platform initial seed data and 3-tier RBAC hierarchy...");

        // 1. Upgrade legacy admin account to ROLE_SUPER_ADMIN and ensure password Admin@123
        try {
            userRepository.findByUsername("admin").ifPresent(admin -> {
                admin.setRole(Role.ROLE_SUPER_ADMIN);
                admin.setApprovalStatus(ApprovalStatus.APPROVED);
                admin.setEnabled(true);
                admin.setPassword(passwordEncoder.encode("Admin@123"));
                userRepository.save(admin);
                log.info("Synchronized 'admin' account as ROLE_SUPER_ADMIN (Admin@123)");
            });
        } catch (Exception e) {
            log.error("Failed to update 'admin' user: {}", e.getMessage());
        }

        // 2. Ensure Main Super Admin 'superadmin' exists
        try {
            User superAdmin = userRepository.findByUsername("superadmin").orElse(null);
            if (superAdmin == null) {
                superAdmin = User.builder()
                        .username("superadmin")
                        .email("superadmin@smartassessment.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .fullName("Main Platform Super Admin")
                        .role(Role.ROLE_SUPER_ADMIN)
                        .enabled(true)
                        .approvalStatus(ApprovalStatus.APPROVED)
                        .build();
                userRepository.save(superAdmin);
                log.info("Seeded Main Super Admin: superadmin / Admin@123");
            } else {
                superAdmin.setRole(Role.ROLE_SUPER_ADMIN);
                superAdmin.setEnabled(true);
                superAdmin.setPassword(passwordEncoder.encode("Admin@123"));
                userRepository.save(superAdmin);
            }
        } catch (Exception e) {
            log.error("Failed to seed superadmin: {}", e.getMessage());
        }

        // 3. Ensure Approved Institute (MIT-TECH) and its Admin exist
        Institute mitInstitute = null;
        try {
            mitInstitute = instituteRepository.findByCode("MIT-TECH").orElse(null);
            if (mitInstitute == null) {
                mitInstitute = Institute.builder()
                        .name("MIT Institute of Technology")
                        .code("MIT-TECH")
                        .description("Premier institution for engineering and technical education.")
                        .contactEmail("mit.admin@example.com")
                        .contactPhone("+1 617-253-1000")
                        .status(ApprovalStatus.APPROVED)
                        .build();
                mitInstitute = instituteRepository.save(mitInstitute);
                log.info("Seeded approved institute: MIT-TECH");
            }

            User mitAdmin = userRepository.findByUsername("mit_admin").orElse(null);
            if (mitAdmin == null) {
                mitAdmin = User.builder()
                        .username("mit_admin")
                        .email("mit.admin@example.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .fullName("MIT Assessment Director")
                        .role(Role.ROLE_INSTITUTE_ADMIN)
                        .enabled(true)
                        .approvalStatus(ApprovalStatus.APPROVED)
                        .institute(mitInstitute)
                        .build();
                mitAdmin = userRepository.save(mitAdmin);
                log.info("Seeded MIT admin: mit_admin / Admin@123");
            } else {
                mitAdmin.setInstitute(mitInstitute);
                mitAdmin.setRole(Role.ROLE_INSTITUTE_ADMIN);
                mitAdmin.setEnabled(true);
                mitAdmin.setPassword(passwordEncoder.encode("Admin@123"));
                mitAdmin.setApprovalStatus(ApprovalStatus.APPROVED);
                mitAdmin = userRepository.save(mitAdmin);
            }

            if (mitInstitute.getAdminUser() == null) {
                mitInstitute.setAdminUser(mitAdmin);
                mitInstitute = instituteRepository.save(mitInstitute);
            }
        } catch (Exception e) {
            log.error("Failed to seed MIT Institute: {}", e.getMessage());
        }

        // 4. Ensure Pending Institute (STAN-01) and its Admin exist (Demonstrates Pending Authorization)
        try {
            Institute stanfordInstitute = instituteRepository.findByCode("STAN-01").orElse(null);
            if (stanfordInstitute == null) {
                stanfordInstitute = Institute.builder()
                        .name("Stanford Academy")
                        .code("STAN-01")
                        .description("Applied computing and data science academy awaiting accreditation.")
                        .contactEmail("stanford.admin@example.com")
                        .contactPhone("+1 650-723-2300")
                        .status(ApprovalStatus.PENDING)
                        .build();
                stanfordInstitute = instituteRepository.save(stanfordInstitute);
                log.info("Seeded pending institute: STAN-01");
            }

            User stanfordAdmin = userRepository.findByUsername("stanford_admin").orElse(null);
            if (stanfordAdmin == null) {
                stanfordAdmin = User.builder()
                        .username("stanford_admin")
                        .email("stanford.admin@example.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .fullName("Stanford Dean")
                        .role(Role.ROLE_INSTITUTE_ADMIN)
                        .enabled(false) // Disabled until approved by Main Admin
                        .approvalStatus(ApprovalStatus.PENDING)
                        .institute(stanfordInstitute)
                        .build();
                stanfordAdmin = userRepository.save(stanfordAdmin);
                log.info("Seeded Stanford pending admin: stanford_admin / Admin@123");
            } else {
                stanfordAdmin.setInstitute(stanfordInstitute);
                stanfordAdmin.setRole(Role.ROLE_INSTITUTE_ADMIN);
                stanfordAdmin.setPassword(passwordEncoder.encode("Admin@123"));
                if (stanfordInstitute.getStatus() == ApprovalStatus.PENDING) {
                    stanfordAdmin.setEnabled(false);
                    stanfordAdmin.setApprovalStatus(ApprovalStatus.PENDING);
                }
                stanfordAdmin = userRepository.save(stanfordAdmin);
            }

            if (stanfordInstitute.getAdminUser() == null) {
                stanfordInstitute.setAdminUser(stanfordAdmin);
                instituteRepository.save(stanfordInstitute);
            }
        } catch (Exception e) {
            log.error("Failed to seed Stanford Institute: {}", e.getMessage());
        }

        // 5. Ensure Candidate Students (student1 & student2) exist and are linked to MIT
        try {
            final Institute finalMit = mitInstitute;
            User s1 = userRepository.findByUsername("student1").orElse(null);
            if (s1 == null) {
                s1 = User.builder()
                        .username("student1")
                        .email("john.doe@example.com")
                        .password(passwordEncoder.encode("Student@123"))
                        .fullName("John Doe")
                        .role(Role.ROLE_STUDENT)
                        .enabled(true)
                        .approvalStatus(ApprovalStatus.APPROVED)
                        .institute(finalMit)
                        .build();
                userRepository.save(s1);
            } else {
                s1.setInstitute(finalMit);
                s1.setEnabled(true);
                s1.setPassword(passwordEncoder.encode("Student@123"));
                userRepository.save(s1);
            }

            User s2 = userRepository.findByUsername("student2").orElse(null);
            if (s2 == null) {
                s2 = User.builder()
                        .username("student2")
                        .email("priya.sharma@example.com")
                        .password(passwordEncoder.encode("Student@123"))
                        .fullName("Priya Sharma")
                        .role(Role.ROLE_STUDENT)
                        .enabled(true)
                        .approvalStatus(ApprovalStatus.APPROVED)
                        .institute(finalMit)
                        .build();
                userRepository.save(s2);
            } else {
                s2.setInstitute(finalMit);
                s2.setEnabled(true);
                s2.setPassword(passwordEncoder.encode("Student@123"));
                userRepository.save(s2);
            }
        } catch (Exception e) {
            log.error("Failed to seed students: {}", e.getMessage());
        }

        // 6. Ensure Categories and Exams
        try {
            final Institute finalMit = mitInstitute;
            if (categoryRepository.count() == 0) {
                Category javaCategory = Category.builder()
                        .name("Core Java & Concurrency")
                        .description("Assessment of Java 17+ core concepts, OOP principles, JVM memory model, and concurrency.")
                        .build();
                categoryRepository.save(javaCategory);

                Category springCategory = Category.builder()
                        .name("Spring Boot & Microservices")
                        .description("Spring Boot 3, Spring Data JPA, Spring Security, RESTful APIs, and transaction management.")
                        .build();
                categoryRepository.save(springCategory);

                Category dsaCategory = Category.builder()
                        .name("Data Structures & Algorithms")
                        .description("Arrays, HashMaps, Two Pointers, Dynamic Programming, and algorithmic problem solving.")
                        .build();
                categoryRepository.save(dsaCategory);

                Exam javaExam = Exam.builder()
                        .title("Java Full Stack Core Technical Assessment")
                        .description("Comprehensive 30-minute evaluation covering Java fundamentals, memory management, multithreading, and basic coding problem.")
                        .category(javaCategory)
                        .durationMinutes(30)
                        .totalMarks(20)
                        .passingMarks(12)
                        .negativeMarkingRate(0.5)
                        .maxAttempts(3)
                        .published(true)
                        .randomizeQuestions(true)
                        .randomizeOptions(true)
                        .institute(finalMit)
                        .assignAllStudents(true)
                        .build();

                Question q1 = Question.builder()
                        .exam(javaExam)
                        .questionType(QuestionType.MCQ)
                        .content("In Java, what happens when two different objects produce the same hashCode() in a HashMap?")
                        .marks(4.0)
                        .negativeMarks(1.0)
                        .explanation("Hash collision: stored in the same bucket via LinkedList or Red-Black Tree.")
                        .build();
                q1.addOption(QuestionOption.builder().optionText("The HashMap throws an IllegalStateException").optionOrder(1).correct(false).build());
                q1.addOption(QuestionOption.builder().optionText("A hash collision occurs; objects are stored in the same bucket via LinkedList or Red-Black Tree").optionOrder(2).correct(true).build());
                q1.addOption(QuestionOption.builder().optionText("The second object silently overwrites the first object").optionOrder(3).correct(false).build());
                q1.addOption(QuestionOption.builder().optionText("The JVM allocates a secondary bucket automatically").optionOrder(4).correct(false).build());
                javaExam.addQuestion(q1);

                examRepository.save(javaExam);
            } else {
                examRepository.findAll().forEach(e -> {
                    boolean changed = false;
                    if (e.getInstitute() == null && finalMit != null) {
                        e.setInstitute(finalMit);
                        changed = true;
                    }
                    if (!e.isAssignAllStudents()) {
                        e.setAssignAllStudents(true);
                        changed = true;
                    }
                    if (!e.isPublished()) {
                        e.setPublished(true);
                        changed = true;
                    }
                    if (changed) {
                        examRepository.save(e);
                    }
                });
            }
        } catch (Exception e) {
            log.error("Failed to seed exams: {}", e.getMessage());
        }

        log.info("3-Tier RBAC Data initialization successfully synchronized!");
    }
}
