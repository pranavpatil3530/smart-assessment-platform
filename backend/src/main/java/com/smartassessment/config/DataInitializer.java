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
        if (userRepository.count() > 0) {
            log.info("Database already initialized. Skipping seeding.");
            return;
        }

        log.info("Starting initial seed data population for Smart Online Assessment Platform...");

        // 1. Seed Main Super Admin (Only ONE Main Admin)
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

        User legacyAdmin = User.builder()
                .username("admin")
                .email("admin@smartassessment.com")
                .password(passwordEncoder.encode("Admin@123"))
                .fullName("Platform Administrator")
                .role(Role.ROLE_SUPER_ADMIN)
                .enabled(true)
                .approvalStatus(ApprovalStatus.APPROVED)
                .build();
        userRepository.save(legacyAdmin);

        // 2. Seed Approved Institute (MIT)
        Institute mitInstitute = Institute.builder()
                .name("MIT Institute of Technology")
                .code("MIT-TECH")
                .description("Premier institution for engineering and technical education.")
                .contactEmail("mit.admin@example.com")
                .contactPhone("+1 617-253-1000")
                .status(ApprovalStatus.APPROVED)
                .build();

        User mitAdmin = User.builder()
                .username("mit_admin")
                .email("mit.admin@example.com")
                .password(passwordEncoder.encode("Admin@123"))
                .fullName("MIT Assessment Director")
                .role(Role.ROLE_INSTITUTE_ADMIN)
                .enabled(true)
                .approvalStatus(ApprovalStatus.APPROVED)
                .institute(mitInstitute)
                .build();

        mitInstitute.setAdminUser(mitAdmin);
        userRepository.save(mitAdmin);
        instituteRepository.save(mitInstitute);

        // 3. Seed Pending Institute (Stanford Academy) - Demonstrates Main Admin Approval workflow!
        Institute stanfordInstitute = Institute.builder()
                .name("Stanford Academy")
                .code("STAN-01")
                .description("Applied computing and data science academy awaiting accreditation.")
                .contactEmail("stanford.admin@example.com")
                .contactPhone("+1 650-723-2300")
                .status(ApprovalStatus.PENDING)
                .build();

        User stanfordAdmin = User.builder()
                .username("stanford_admin")
                .email("stanford.admin@example.com")
                .password(passwordEncoder.encode("Admin@123"))
                .fullName("Stanford Dean")
                .role(Role.ROLE_INSTITUTE_ADMIN)
                .enabled(false) // Cannot sign in until Main Admin approves
                .approvalStatus(ApprovalStatus.PENDING)
                .institute(stanfordInstitute)
                .build();

        stanfordInstitute.setAdminUser(stanfordAdmin);
        userRepository.save(stanfordAdmin);
        instituteRepository.save(stanfordInstitute);

        // 4. Seed Candidates (Students) enrolled in MIT Institute
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

        // 5. Seed Categories
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

        // 6. Seed Exam 1: Assigned to ALL students of MIT
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

        // Question 1 (MCQ)
        Question q1 = Question.builder()
                .exam(javaExam)
                .questionType(QuestionType.MCQ)
                .content("In Java, what happens when two different objects produce the same hashCode() in a HashMap?")
                .marks(4.0)
                .negativeMarks(1.0)
                .explanation("When two unequal objects have the same hash code, a hash collision occurs. The entries are stored in the same bucket linked list (or converted to a red-black tree when the bucket length exceeds TREEIFY_THRESHOLD = 8 in Java 8+).")
                .build();

        q1.addOption(QuestionOption.builder().optionText("The HashMap throws an IllegalStateException").optionOrder(1).correct(false).build());
        q1.addOption(QuestionOption.builder().optionText("A hash collision occurs; objects are stored in the same bucket via LinkedList or Red-Black Tree").optionOrder(2).correct(true).build());
        q1.addOption(QuestionOption.builder().optionText("The second object silently overwrites the first object").optionOrder(3).correct(false).build());
        q1.addOption(QuestionOption.builder().optionText("The JVM allocates a secondary bucket automatically").optionOrder(4).correct(false).build());
        javaExam.addQuestion(q1);

        // Question 2 (MCQ)
        Question q2 = Question.builder()
                .exam(javaExam)
                .questionType(QuestionType.MCQ)
                .content("Which garbage collector is the default in modern Java 17+ and Java 21 LTS releases?")
                .marks(4.0)
                .negativeMarks(1.0)
                .explanation("The Garbage-First (G1) Garbage Collector became the default GC starting from Java 9 and continues as default in modern LTS releases.")
                .build();

        q2.addOption(QuestionOption.builder().optionText("Serial GC").optionOrder(1).correct(false).build());
        q2.addOption(QuestionOption.builder().optionText("Parallel GC").optionOrder(2).correct(false).build());
        q2.addOption(QuestionOption.builder().optionText("G1 (Garbage-First) GC").optionOrder(3).correct(true).build());
        q2.addOption(QuestionOption.builder().optionText("ZGC (Z Garbage Collector)").optionOrder(4).correct(false).build());
        javaExam.addQuestion(q2);

        // Question 3 (MCQ)
        Question q3 = Question.builder()
                .exam(javaExam)
                .questionType(QuestionType.MCQ)
                .content("What is the primary architectural purpose of the 'volatile' keyword in Java?")
                .marks(4.0)
                .negativeMarks(1.0)
                .explanation("'volatile' guarantees visibility of changes to variables across threads and establishes a happens-before relationship, bypassing CPU caches.")
                .build();

        q3.addOption(QuestionOption.builder().optionText("It provides atomic operations for compound actions like incrementing (i++)").optionOrder(1).correct(false).build());
        q3.addOption(QuestionOption.builder().optionText("It guarantees visibility of variable updates across threads without mutual exclusion locking").optionOrder(2).correct(true).build());
        q3.addOption(QuestionOption.builder().optionText("It prevents the garbage collector from reclaiming the object").optionOrder(3).correct(false).build());
        q3.addOption(QuestionOption.builder().optionText("It serializes object state directly into thread-local storage").optionOrder(4).correct(false).build());
        javaExam.addQuestion(q3);

        // Question 4 (Coding Question)
        CodingProblem codingProblem = CodingProblem.builder()
                .starterCode("import java.util.*;\n\npublic class Solution {\n    public static int findDuplicate(int[] nums) {\n        // Write your solution here\n        return -1;\n    }\n}")
                .language("java")
                .constraints("2 <= nums.length <= 10^5\nAll integers appear once except one duplicate which appears twice.")
                .sampleInput("[1, 3, 4, 2, 2]")
                .sampleOutput("2")
                .testCasesJson("[{\"input\": \"[1, 3, 4, 2, 2]\", \"output\": \"2\"}, {\"input\": \"[3, 1, 3, 4, 2]\", \"output\": \"3\"}]")
                .build();

        Question q4 = Question.builder()
                .exam(javaExam)
                .questionType(QuestionType.CODING)
                .content("Find the Duplicate Number: Given an array of integers containing n + 1 integers where each integer is in the range [1, n] inclusive, find the duplicate number without modifying the array.")
                .marks(8.0)
                .negativeMarks(0.0)
                .explanation("Floyd's Cycle-Finding Algorithm (Tortoise and Hare) allows finding the duplicate in O(n) time and O(1) space.")
                .codingProblem(codingProblem)
                .build();
        codingProblem.setQuestion(q4);
        javaExam.addQuestion(q4);

        examRepository.save(javaExam);

        // 7. Seed Exam 2: Assigned specifically to student1 only (selective assignment by Institute Admin)
        Exam springExam = Exam.builder()
                .title("Spring Boot 3 & Cloud Native Architecture")
                .description("Advanced assessment on Spring Boot auto-configuration, Bean lifecycle, Spring Data JPA, and security filters.")
                .category(springCategory)
                .durationMinutes(20)
                .totalMarks(10)
                .passingMarks(6)
                .negativeMarkingRate(0.25)
                .maxAttempts(2)
                .published(true)
                .randomizeQuestions(false)
                .randomizeOptions(false)
                .institute(mitInstitute)
                .assignAllStudents(false)
                .build();

        springExam.getAssignedCandidates().add(student1);

        Question sq1 = Question.builder()
                .exam(springExam)
                .questionType(QuestionType.MCQ)
                .content("Which annotation triggers the Spring Boot auto-configuration mechanism?")
                .marks(3.0)
                .negativeMarks(0.75)
                .explanation("@EnableAutoConfiguration (or @SpringBootApplication which includes it) tells Spring Boot to automatically configure Beans based on classpath dependencies.")
                .build();

        sq1.addOption(QuestionOption.builder().optionText("@EnableAutoConfiguration").optionOrder(1).correct(true).build());
        sq1.addOption(QuestionOption.builder().optionText("@ComponentScan").optionOrder(2).correct(false).build());
        sq1.addOption(QuestionOption.builder().optionText("@Configuration").optionOrder(3).correct(false).build());
        sq1.addOption(QuestionOption.builder().optionText("@EnableWebMvc").optionOrder(4).correct(false).build());
        springExam.addQuestion(sq1);

        Question sq2 = Question.builder()
                .exam(springExam)
                .questionType(QuestionType.MCQ)
                .content("What is the default Bean scope in Spring Framework?")
                .marks(3.0)
                .negativeMarks(0.75)
                .explanation("The singleton scope is the default scope in Spring. Only one shared instance of a singleton bean is created per ApplicationContext.")
                .build();

        sq2.addOption(QuestionOption.builder().optionText("Singleton").optionOrder(1).correct(true).build());
        sq2.addOption(QuestionOption.builder().optionText("Prototype").optionOrder(2).correct(false).build());
        sq2.addOption(QuestionOption.builder().optionText("Request").optionOrder(3).correct(false).build());
        sq2.addOption(QuestionOption.builder().optionText("Session").optionOrder(4).correct(false).build());
        springExam.addQuestion(sq2);

        Question sq3 = Question.builder()
                .exam(springExam)
                .questionType(QuestionType.MCQ)
                .content("What is the difference between @Controller and @RestController in Spring MVC?")
                .marks(4.0)
                .negativeMarks(1.0)
                .explanation("@RestController is a composed annotation that combines @Controller and @ResponseBody, automatically writing return values directly to the HTTP response body.")
                .build();

        sq3.addOption(QuestionOption.builder().optionText("@RestController automatically includes @ResponseBody on all handler methods").optionOrder(1).correct(true).build());
        sq3.addOption(QuestionOption.builder().optionText("@Controller is asynchronous while @RestController is synchronous").optionOrder(2).correct(false).build());
        sq3.addOption(QuestionOption.builder().optionText("@RestController bypasses Spring Security filters").optionOrder(3).correct(false).build());
        sq3.addOption(QuestionOption.builder().optionText("@Controller cannot return JSON data").optionOrder(4).correct(false).build());
        springExam.addQuestion(sq3);

        examRepository.save(springExam);

        // 8. Seed historical attempt
        ExamAttempt attempt = ExamAttempt.builder()
                .user(student1)
                .exam(javaExam)
                .startTime(LocalDateTime.now().minusHours(2))
                .endTime(LocalDateTime.now().minusHours(1).minusMinutes(30))
                .submittedTime(LocalDateTime.now().minusHours(1).minusMinutes(35))
                .status(AttemptStatus.SUBMITTED)
                .score(16.0)
                .totalMarks(20.0)
                .percentage(80.0)
                .passed(true)
                .totalCorrect(3)
                .totalWrong(0)
                .totalUnattempted(1)
                .build();

        AttemptResponse r1 = AttemptResponse.builder()
                .examAttempt(attempt)
                .question(q1)
                .selectedOption(q1.getOptions().get(1))
                .isCorrect(true)
                .marksAwarded(4.0)
                .build();
        attempt.addResponse(r1);

        AttemptResponse r2 = AttemptResponse.builder()
                .examAttempt(attempt)
                .question(q2)
                .selectedOption(q2.getOptions().get(2))
                .isCorrect(true)
                .marksAwarded(4.0)
                .build();
        attempt.addResponse(r2);

        AttemptResponse r3 = AttemptResponse.builder()
                .examAttempt(attempt)
                .question(q3)
                .selectedOption(q3.getOptions().get(1))
                .isCorrect(true)
                .marksAwarded(4.0)
                .build();
        attempt.addResponse(r3);

        examAttemptRepository.save(attempt);

        log.info("Initial seed data successfully loaded!");
        log.info("Main Admin (Super): superadmin / Admin@123");
        log.info("Approved Institute Admin: mit_admin / Admin@123 (MIT-TECH)");
        log.info("Pending Institute Admin: stanford_admin / Admin@123 (STAN-01 - Pending Approval)");
        log.info("Candidate Students: student1 / Student@123, student2 / Student@123 (MIT)");
    }
}
