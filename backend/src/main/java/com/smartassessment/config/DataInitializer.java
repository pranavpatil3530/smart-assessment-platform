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
    private final CategoryRepository categoryRepository;
    private final ExamRepository examRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CategoryRepository categoryRepository,
                           ExamRepository examRepository,
                           ExamAttemptRepository examAttemptRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
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

        // 1. Seed Users
        User admin = User.builder()
                .username("admin")
                .email("admin@smartassessment.com")
                .password(passwordEncoder.encode("Admin@123"))
                .fullName("System Administrator")
                .role(Role.ROLE_ADMIN)
                .enabled(true)
                .build();
        userRepository.save(admin);

        User student1 = User.builder()
                .username("student1")
                .email("john.doe@example.com")
                .password(passwordEncoder.encode("Student@123"))
                .fullName("John Doe")
                .role(Role.ROLE_STUDENT)
                .enabled(true)
                .build();
        userRepository.save(student1);

        User student2 = User.builder()
                .username("student2")
                .email("priya.sharma@example.com")
                .password(passwordEncoder.encode("Student@123"))
                .fullName("Priya Sharma")
                .role(Role.ROLE_STUDENT)
                .enabled(true)
                .build();
        userRepository.save(student2);

        // 2. Seed Categories
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

        // 3. Seed Exam 1: Java Technical Screening
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
        Question q4 = Question.builder()
                .exam(javaExam)
                .questionType(QuestionType.CODING)
                .content("Implement a method 'findFirstNonRepeatingChar(String s)' that returns the first non-repeating character in a given string. If none exists, return '_'.")
                .marks(8.0)
                .negativeMarks(0.0)
                .explanation("Optimal solution uses a character frequency array or LinkedHashMap in O(N) time and O(1) space.")
                .build();

        CodingProblem codingProblem = CodingProblem.builder()
                .language("Java")
                .starterCode(
                        "public class Solution {\n" +
                        "    public static char findFirstNonRepeatingChar(String s) {\n" +
                        "        // Your code here\n" +
                        "        return '_';\n" +
                        "    }\n" +
                        "}"
                )
                .constraints("1 <= s.length <= 10^5; s contains only lowercase English letters.")
                .sampleInput("\"swiss\"")
                .sampleOutput("'w'")
                .testCasesJson("[{\"input\":\"swiss\",\"output\":\"w\"},{\"input\":\"aabbcc\",\"output\":\"_\"}]")
                .build();
        q4.setCodingProblem(codingProblem);
        javaExam.addQuestion(q4);

        examRepository.save(javaExam);

        // 4. Seed Exam 2: Spring Boot Enterprise
        Exam springExam = Exam.builder()
                .title("Spring Boot & JPA Enterprise Architecture Test")
                .description("Advanced test evaluating Spring Bean lifecycle, @Transactional propagation, Spring Security filter chains, and N+1 query problem solutions.")
                .category(springCategory)
                .durationMinutes(25)
                .totalMarks(15)
                .passingMarks(10)
                .negativeMarkingRate(0.5)
                .maxAttempts(2)
                .published(true)
                .randomizeQuestions(true)
                .randomizeOptions(true)
                .build();

        Question sq1 = Question.builder()
                .exam(springExam)
                .questionType(QuestionType.MCQ)
                .content("How can developers avoid the JPA 'N+1 Query Problem' when fetching an entity with lazy-loaded collections?")
                .marks(5.0)
                .negativeMarks(1.0)
                .explanation("JOIN FETCH in JPQL or using @EntityGraph forces Hibernate to retrieve the parent and associated collection in a single SQL query.")
                .build();

        sq1.addOption(QuestionOption.builder().optionText("Switching FetchType from LAZY to EAGER").optionOrder(1).correct(false).build());
        sq1.addOption(QuestionOption.builder().optionText("Using JPQL 'JOIN FETCH' or Spring Data JPA '@EntityGraph'").optionOrder(2).correct(true).build());
        sq1.addOption(QuestionOption.builder().optionText("Wrapping the query inside @Transactional(readOnly = true)").optionOrder(3).correct(false).build());
        sq1.addOption(QuestionOption.builder().optionText("Enabling second-level cache in application.properties").optionOrder(4).correct(false).build());
        springExam.addQuestion(sq1);

        Question sq2 = Question.builder()
                .exam(springExam)
                .questionType(QuestionType.MCQ)
                .content("In Spring Security 6, how does OncePerRequestFilter ensure each HTTP request is filtered only once?")
                .marks(5.0)
                .negativeMarks(1.0)
                .explanation("OncePerRequestFilter checks and marks a custom request attribute on the HttpServletRequest so internal forwards or dispatches don't re-trigger filtering.")
                .build();

        sq2.addOption(QuestionOption.builder().optionText("By checking a unique request attribute in the HttpServletRequest").optionOrder(1).correct(true).build());
        sq2.addOption(QuestionOption.builder().optionText("By keeping thread-local state in a ConcurrentHashMap").optionOrder(2).correct(false).build());
        sq2.addOption(QuestionOption.builder().optionText("By caching the response in the HTTP session").optionOrder(3).correct(false).build());
        sq2.addOption(QuestionOption.builder().optionText("By synchronizing on the servlet context").optionOrder(4).correct(false).build());
        springExam.addQuestion(sq2);

        Question sq3 = Question.builder()
                .exam(springExam)
                .questionType(QuestionType.MCQ)
                .content("What is the difference between @Controller and @RestController in Spring MVC?")
                .marks(5.0)
                .negativeMarks(1.0)
                .explanation("@RestController is a composed annotation that combines @Controller and @ResponseBody, automatically writing return values directly to the HTTP response body.")
                .build();

        sq3.addOption(QuestionOption.builder().optionText("@RestController automatically includes @ResponseBody on all handler methods").optionOrder(1).correct(true).build());
        sq3.addOption(QuestionOption.builder().optionText("@Controller is asynchronous while @RestController is synchronous").optionOrder(2).correct(false).build());
        sq3.addOption(QuestionOption.builder().optionText("@RestController bypasses Spring Security filters").optionOrder(3).correct(false).build());
        sq3.addOption(QuestionOption.builder().optionText("@Controller cannot return JSON data").optionOrder(4).correct(false).build());
        springExam.addQuestion(sq3);

        examRepository.save(springExam);

        // 5. Seed historical attempt
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

        log.info("Initial seed data successfully loaded! Admin: admin/Admin@123 | Student: student1/Student@123");
    }
}
