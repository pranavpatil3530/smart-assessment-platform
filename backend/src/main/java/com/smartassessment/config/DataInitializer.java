package com.smartassessment.config;

import com.smartassessment.entity.*;
import com.smartassessment.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public void run(String... args) {
        log.info("Ensuring platform initial seed data and 3-tier RBAC hierarchy...");

        // 1. Upgrade legacy admin to ROLE_SUPER_ADMIN if present
        userRepository.findByUsername("admin").ifPresent(admin -> {
            admin.setRole(Role.ROLE_SUPER_ADMIN);
            admin.setApprovalStatus(ApprovalStatus.APPROVED);
            userRepository.save(admin);
            log.info("Upgraded existing 'admin' account to ROLE_SUPER_ADMIN");
        });

        // 2. Ensure Main Super Admin 'superadmin' exists
        if (!userRepository.existsByUsername("superadmin")) {
            User superAdmin = User.builder()
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
        }

        // 3. Ensure Approved Institute (MIT-TECH) exists
        Institute mitInstitute = instituteRepository.findByCode("MIT-TECH").orElseGet(() -> {
            Institute mit = Institute.builder()
                    .name("MIT Institute of Technology")
                    .code("MIT-TECH")
                    .description("Premier institution for engineering and technical education.")
                    .contactEmail("mit.admin@example.com")
                    .contactPhone("+1 617-253-1000")
                    .status(ApprovalStatus.APPROVED)
                    .build();

            if (!userRepository.existsByUsername("mit_admin")) {
                User mitAdmin = User.builder()
                        .username("mit_admin")
                        .email("mit.admin@example.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .fullName("MIT Assessment Director")
                        .role(Role.ROLE_INSTITUTE_ADMIN)
                        .enabled(true)
                        .approvalStatus(ApprovalStatus.APPROVED)
                        .institute(mit)
                        .build();
                mit.setAdminUser(mitAdmin);
                userRepository.save(mitAdmin);
            }
            return instituteRepository.save(mit);
        });

        // Ensure mit_admin is linked to mitInstitute
        userRepository.findByUsername("mit_admin").ifPresent(mitAdmin -> {
            mitAdmin.setInstitute(mitInstitute);
            mitAdmin.setRole(Role.ROLE_INSTITUTE_ADMIN);
            mitAdmin.setEnabled(true);
            mitAdmin.setApprovalStatus(ApprovalStatus.APPROVED);
            userRepository.save(mitAdmin);
            if (mitInstitute.getAdminUser() == null) {
                mitInstitute.setAdminUser(mitAdmin);
                instituteRepository.save(mitInstitute);
            }
        });

        // 4. Ensure Pending Institute (STAN-01) exists (Demonstrates Super Admin Approval workflow!)
        Institute stanfordInstitute = instituteRepository.findByCode("STAN-01").orElseGet(() -> {
            Institute stanford = Institute.builder()
                    .name("Stanford Academy")
                    .code("STAN-01")
                    .description("Applied computing and data science academy awaiting accreditation.")
                    .contactEmail("stanford.admin@example.com")
                    .contactPhone("+1 650-723-2300")
                    .status(ApprovalStatus.PENDING)
                    .build();

            if (!userRepository.existsByUsername("stanford_admin")) {
                User stanfordAdmin = User.builder()
                        .username("stanford_admin")
                        .email("stanford.admin@example.com")
                        .password(passwordEncoder.encode("Admin@123"))
                        .fullName("Stanford Dean")
                        .role(Role.ROLE_INSTITUTE_ADMIN)
                        .enabled(false) // Disabled until Main Admin approves
                        .approvalStatus(ApprovalStatus.PENDING)
                        .institute(stanford)
                        .build();
                stanford.setAdminUser(stanfordAdmin);
                userRepository.save(stanfordAdmin);
            }
            return instituteRepository.save(stanford);
        });

        // Ensure stanford_admin is linked and kept pending if not already approved
        userRepository.findByUsername("stanford_admin").ifPresent(stAdmin -> {
            stAdmin.setInstitute(stanfordInstitute);
            stAdmin.setRole(Role.ROLE_INSTITUTE_ADMIN);
            if (stanfordInstitute.getStatus() == ApprovalStatus.PENDING) {
                stAdmin.setEnabled(false);
                stAdmin.setApprovalStatus(ApprovalStatus.PENDING);
            }
            userRepository.save(stAdmin);
        });

        // 5. Ensure Candidate Students are associated with MIT
        userRepository.findByUsername("student1").ifPresent(s1 -> {
            if (s1.getInstitute() == null) {
                s1.setInstitute(mitInstitute);
                s1.setApprovalStatus(ApprovalStatus.APPROVED);
                userRepository.save(s1);
            }
        });

        userRepository.findByUsername("student2").ifPresent(s2 -> {
            if (s2.getInstitute() == null) {
                s2.setInstitute(mitInstitute);
                s2.setApprovalStatus(ApprovalStatus.APPROVED);
                userRepository.save(s2);
            }
        });

        // If no students exist, create them
        if (!userRepository.existsByUsername("student1")) {
            User student1 = User.builder()
                    .username("student1")
                    .email("john.doe@example.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .fullName("John Doe")
                    .role(Role.ROLE_STUDENT)
                    .enabled(true)
                    .approvalStatus(ApprovalStatus.APPROVED)
                    .institute(mitInstitute)
                    .build();
            userRepository.save(student1);
        }

        if (!userRepository.existsByUsername("student2")) {
            User student2 = User.builder()
                    .username("student2")
                    .email("priya.sharma@example.com")
                    .password(passwordEncoder.encode("Student@123"))
                    .fullName("Priya Sharma")
                    .role(Role.ROLE_STUDENT)
                    .enabled(true)
                    .approvalStatus(ApprovalStatus.APPROVED)
                    .institute(mitInstitute)
                    .build();
            userRepository.save(student2);
        }

        // 6. Ensure Categories and Exams
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

            // Java Exam assigned to all students
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
                    .institute(mitInstitute)
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
            // Associate existing exams with MIT if not associated and ensure assignAllStudents = true
            examRepository.findAll().forEach(e -> {
                boolean changed = false;
                if (e.getInstitute() == null && mitInstitute != null) {
                    e.setInstitute(mitInstitute);
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

        log.info("3-Tier RBAC Data initialization successfully synchronized!");
        log.info("Main Admin (Super): superadmin / Admin@123");
        log.info("Approved Institute Admin: mit_admin / Admin@123 (MIT-TECH)");
        log.info("Pending Institute Admin: stanford_admin / Admin@123 (STAN-01 - Pending Approval)");
        log.info("Candidate Students: student1 / Student@123, student2 / Student@123 (MIT)");
    }
}
