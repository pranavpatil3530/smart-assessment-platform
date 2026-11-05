package com.smartassessment.service.impl;

import com.smartassessment.dto.attempt.*;
import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.question.*;
import com.smartassessment.entity.*;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.exception.ExamAlreadySubmittedException;
import com.smartassessment.exception.ResourceNotFoundException;
import com.smartassessment.exception.UnauthorizedException;
import com.smartassessment.repository.ExamAttemptRepository;
import com.smartassessment.repository.ExamRepository;
import com.smartassessment.repository.QuestionOptionRepository;
import com.smartassessment.repository.UserRepository;
import com.smartassessment.service.ExamAttemptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private static final Logger log = LoggerFactory.getLogger(ExamAttemptServiceImpl.class);
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamRepository examRepository;
    private final UserRepository userRepository;
    private final QuestionOptionRepository questionOptionRepository;

    public ExamAttemptServiceImpl(ExamAttemptRepository examAttemptRepository,
                                  ExamRepository examRepository,
                                  UserRepository userRepository,
                                  QuestionOptionRepository questionOptionRepository) {
        this.examAttemptRepository = examAttemptRepository;
        this.examRepository = examRepository;
        this.userRepository = userRepository;
        this.questionOptionRepository = questionOptionRepository;
    }

    @Override
    @Transactional
    public StartExamResponseDto startOrResumeExam(Long examId, Long userId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        if (!exam.isPublished()) {
            throw new BadRequestException("This examination is not currently published or available.");
        }

        if (exam.getQuestions().isEmpty()) {
            throw new BadRequestException("This examination does not contain any questions yet.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Optional<ExamAttempt> inProgressOpt = examAttemptRepository
                .findFirstByUserIdAndExamIdAndStatus(userId, examId, AttemptStatus.IN_PROGRESS);

        ExamAttempt attempt;
        LocalDateTime now = LocalDateTime.now();

        if (inProgressOpt.isPresent()) {
            attempt = inProgressOpt.get();
            if (now.isAfter(attempt.getEndTime())) {
                log.info("Previous attempt ID {} has expired. Auto-finalizing it.", attempt.getId());
                finalizeExpiredAttempt(attempt);
                verifyAttemptLimit(userId, exam);
                attempt = createNewAttempt(user, exam, now);
            } else {
                log.info("Resuming active attempt ID {} for user {}", attempt.getId(), user.getUsername());
            }
        } else {
            verifyAttemptLimit(userId, exam);
            attempt = createNewAttempt(user, exam, now);
        }

        long remainingSeconds = Math.max(0, Duration.between(LocalDateTime.now(), attempt.getEndTime()).getSeconds());
        List<StudentQuestionDto> studentQuestions = prepareStudentQuestions(exam);

        return StartExamResponseDto.builder()
                .attemptId(attempt.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .durationMinutes(exam.getDurationMinutes())
                .negativeMarkingRate(exam.getNegativeMarkingRate())
                .startTime(attempt.getStartTime())
                .endTime(attempt.getEndTime())
                .remainingSeconds(remainingSeconds)
                .questions(studentQuestions)
                .build();
    }

    private void verifyAttemptLimit(Long userId, Exam exam) {
        long pastAttemptsCount = examAttemptRepository.countByUserIdAndExamId(userId, exam.getId());
        if (pastAttemptsCount >= exam.getMaxAttempts()) {
            throw new BadRequestException(String.format(
                    "You have reached the maximum allowed attempts (%d) for this examination.",
                    exam.getMaxAttempts()
            ));
        }
    }

    private ExamAttempt createNewAttempt(User user, Exam exam, LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusMinutes(exam.getDurationMinutes());

        ExamAttempt newAttempt = ExamAttempt.builder()
                .user(user)
                .exam(exam)
                .startTime(startTime)
                .endTime(endTime)
                .status(AttemptStatus.IN_PROGRESS)
                .score(0.0)
                .totalMarks((double) exam.getTotalMarks())
                .percentage(0.0)
                .passed(false)
                .totalCorrect(0)
                .totalWrong(0)
                .totalUnattempted(exam.getQuestions().size())
                .build();

        return examAttemptRepository.save(newAttempt);
    }

    private void finalizeExpiredAttempt(ExamAttempt attempt) {
        attempt.setStatus(AttemptStatus.TIMED_OUT);
        attempt.setSubmittedTime(attempt.getEndTime());
        attempt.setScore(0.0);
        attempt.setPercentage(0.0);
        attempt.setPassed(false);
        examAttemptRepository.save(attempt);
    }

    private List<StudentQuestionDto> prepareStudentQuestions(Exam exam) {
        List<Question> questions = new ArrayList<>(exam.getQuestions());

        if (exam.isRandomizeQuestions()) {
            Collections.shuffle(questions, new Random());
        }

        return questions.stream().map(q -> {
            List<StudentOptionDto> optionDtos = new ArrayList<>();
            if (q.getQuestionType() == QuestionType.MCQ) {
                List<QuestionOption> options = new ArrayList<>(q.getOptions());
                if (exam.isRandomizeOptions()) {
                    Collections.shuffle(options, new Random());
                }
                optionDtos = options.stream().map(opt -> StudentOptionDto.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .optionOrder(opt.getOptionOrder())
                        .build()).collect(Collectors.toList());
            }

            CodingProblemDto codingDto = null;
            if (q.getCodingProblem() != null) {
                codingDto = CodingProblemDto.builder()
                        .id(q.getCodingProblem().getId())
                        .starterCode(q.getCodingProblem().getStarterCode())
                        .language(q.getCodingProblem().getLanguage())
                        .constraints(q.getCodingProblem().getConstraints())
                        .sampleInput(q.getCodingProblem().getSampleInput())
                        .sampleOutput(q.getCodingProblem().getSampleOutput())
                        .build();
            }

            return StudentQuestionDto.builder()
                    .id(q.getId())
                    .questionType(q.getQuestionType())
                    .content(q.getContent())
                    .marks(q.getMarks())
                    .negativeMarks(q.getNegativeMarks())
                    .options(optionDtos)
                    .codingProblem(codingDto)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ExamResultResponseDto submitExam(Long attemptId, Long userId, SubmitExamRequestDto submitDto, boolean isAutoSubmit) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        if (!attempt.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to submit this attempt.");
        }

        if (attempt.getStatus() != AttemptStatus.IN_PROGRESS) {
            throw new ExamAlreadySubmittedException("This examination has already been submitted or finalized.");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean timeExpired = now.isAfter(attempt.getEndTime().plusSeconds(60));
        AttemptStatus finalStatus = (isAutoSubmit || timeExpired) ? AttemptStatus.AUTO_SUBMITTED : AttemptStatus.SUBMITTED;

        Exam exam = attempt.getExam();
        Map<Long, StudentAnswerSubmissionDto> answerMap = new HashMap<>();
        if (submitDto != null && submitDto.getAnswers() != null) {
            for (StudentAnswerSubmissionDto ans : submitDto.getAnswers()) {
                if (ans.getQuestionId() != null) {
                    answerMap.put(ans.getQuestionId(), ans);
                }
            }
        }

        double totalScore = 0.0;
        int correctCount = 0;
        int wrongCount = 0;
        int unattemptedCount = 0;

        List<Question> questions = exam.getQuestions();
        List<AttemptResponse> responses = new ArrayList<>();

        for (Question question : questions) {
            StudentAnswerSubmissionDto submission = answerMap.get(question.getId());
            AttemptResponse response = AttemptResponse.builder()
                    .examAttempt(attempt)
                    .question(question)
                    .build();

            if (question.getQuestionType() == QuestionType.MCQ) {
                if (submission != null && submission.getSelectedOptionId() != null) {
                    Long selectedId = submission.getSelectedOptionId();
                    Optional<QuestionOption> selectedOpt = questionOptionRepository.findById(selectedId);

                    if (selectedOpt.isPresent()) {
                        QuestionOption opt = selectedOpt.get();
                        response.setSelectedOption(opt);

                        if (opt.isCorrect()) {
                            response.setIsCorrect(true);
                            response.setMarksAwarded(question.getMarks());
                            totalScore += question.getMarks();
                            correctCount++;
                        } else {
                            response.setIsCorrect(false);
                            double penalty = question.getNegativeMarks() != null ?
                                    question.getNegativeMarks() : (exam.getNegativeMarkingRate() * question.getMarks());
                            response.setMarksAwarded(-penalty);
                            totalScore -= penalty;
                            wrongCount++;
                        }
                    } else {
                        unattemptedCount++;
                        response.setIsCorrect(null);
                        response.setMarksAwarded(0.0);
                    }
                } else {
                    unattemptedCount++;
                    response.setIsCorrect(null);
                    response.setMarksAwarded(0.0);
                }
            } else if (question.getQuestionType() == QuestionType.CODING) {
                if (submission != null && submission.getSubmittedCode() != null && !submission.getSubmittedCode().isBlank()) {
                    response.setSubmittedCode(submission.getSubmittedCode());
                    response.setIsCorrect(true);
                    response.setMarksAwarded(question.getMarks());
                    totalScore += question.getMarks();
                    correctCount++;
                } else {
                    unattemptedCount++;
                    response.setIsCorrect(null);
                    response.setMarksAwarded(0.0);
                }
            }

            attempt.addResponse(response);
            responses.add(response);
        }

        double finalScore = Math.max(0.0, Math.round(totalScore * 100.0) / 100.0);
        double totalExamMarks = exam.getTotalMarks() != null && exam.getTotalMarks() > 0 ? exam.getTotalMarks() : 100.0;
        double percentage = Math.round((finalScore / totalExamMarks * 100.0) * 100.0) / 100.0;
        boolean passed = finalScore >= exam.getPassingMarks();

        attempt.setStatus(finalStatus);
        attempt.setSubmittedTime(now);
        attempt.setScore(finalScore);
        attempt.setTotalMarks(totalExamMarks);
        attempt.setPercentage(percentage);
        attempt.setPassed(passed);
        attempt.setTotalCorrect(correctCount);
        attempt.setTotalWrong(wrongCount);
        attempt.setTotalUnattempted(unattemptedCount);

        ExamAttempt savedAttempt = examAttemptRepository.save(attempt);
        log.info("Exam Attempt ID {} successfully evaluated. Score: {} / {}, Passed: {}",
                savedAttempt.getId(), finalScore, totalExamMarks, passed);

        return buildResultResponse(savedAttempt);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResultResponseDto getAttemptResult(Long attemptId, Long userId) {
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("ExamAttempt", "id", attemptId));

        if (!attempt.getUser().getId().equals(userId) &&
                !attempt.getUser().getRole().name().equals("ROLE_ADMIN")) {
            throw new UnauthorizedException("You do not have permission to view this result.");
        }

        return buildResultResponse(attempt);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AttemptHistoryDto> getStudentAttemptHistory(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExamAttempt> attemptsPage = examAttemptRepository.findByUserIdOrderByStartTimeDesc(userId, pageable);

        List<AttemptHistoryDto> content = attemptsPage.getContent().stream()
                .map(att -> {
                    long duration = 0L;
                    if (att.getSubmittedTime() != null && att.getStartTime() != null) {
                        duration = Duration.between(att.getStartTime(), att.getSubmittedTime()).getSeconds();
                    }
                    return AttemptHistoryDto.builder()
                            .attemptId(att.getId())
                            .examId(att.getExam().getId())
                            .examTitle(att.getExam().getTitle())
                            .categoryName(att.getExam().getCategory().getName())
                            .score(att.getScore())
                            .totalMarks(att.getTotalMarks())
                            .percentage(att.getPercentage())
                            .passed(Boolean.TRUE.equals(att.getPassed()))
                            .status(att.getStatus())
                            .startTime(att.getStartTime())
                            .submittedTime(att.getSubmittedTime())
                            .timeTakenSeconds(duration)
                            .build();
                })
                .collect(Collectors.toList());

        return PagedResponse.<AttemptHistoryDto>builder()
                .content(content)
                .pageNumber(attemptsPage.getNumber())
                .pageSize(attemptsPage.getSize())
                .totalElements(attemptsPage.getTotalElements())
                .totalPages(attemptsPage.getTotalPages())
                .last(attemptsPage.isLast())
                .build();
    }

    private ExamResultResponseDto buildResultResponse(ExamAttempt attempt) {
        long timeTakenSeconds = 0L;
        if (attempt.getStartTime() != null && attempt.getSubmittedTime() != null) {
            timeTakenSeconds = Duration.between(attempt.getStartTime(), attempt.getSubmittedTime()).getSeconds();
        }

        int totalAttempted = attempt.getTotalCorrect() + attempt.getTotalWrong();
        double accuracyRate = totalAttempted > 0 ?
                Math.round(((double) attempt.getTotalCorrect() / totalAttempted * 100.0) * 100.0) / 100.0 : 0.0;

        List<QuestionResultDto> breakdown = attempt.getResponses().stream().map(resp -> {
            Question q = resp.getQuestion();
            QuestionOption selected = resp.getSelectedOption();
            QuestionOption correct = q.getOptions().stream().filter(QuestionOption::isCorrect).findFirst().orElse(null);

            List<OptionResponseDto> optionDtos = q.getOptions().stream().map(opt -> OptionResponseDto.builder()
                    .id(opt.getId())
                    .optionText(opt.getOptionText())
                    .optionOrder(opt.getOptionOrder())
                    .correct(opt.isCorrect())
                    .build()).collect(Collectors.toList());

            return QuestionResultDto.builder()
                    .questionId(q.getId())
                    .questionType(q.getQuestionType())
                    .content(q.getContent())
                    .marks(q.getMarks())
                    .negativeMarks(q.getNegativeMarks())
                    .selectedOptionId(selected != null ? selected.getId() : null)
                    .selectedOptionText(selected != null ? selected.getOptionText() : null)
                    .correctOptionId(correct != null ? correct.getId() : null)
                    .correctOptionText(correct != null ? correct.getOptionText() : null)
                    .submittedCode(resp.getSubmittedCode())
                    .isCorrect(resp.getIsCorrect())
                    .marksAwarded(resp.getMarksAwarded())
                    .explanation(q.getExplanation())
                    .options(optionDtos)
                    .build();
        }).collect(Collectors.toList());

        return ExamResultResponseDto.builder()
                .attemptId(attempt.getId())
                .examId(attempt.getExam().getId())
                .examTitle(attempt.getExam().getTitle())
                .studentName(attempt.getUser().getFullName())
                .studentEmail(attempt.getUser().getEmail())
                .score(attempt.getScore())
                .totalMarks(attempt.getTotalMarks())
                .passingMarks(attempt.getExam().getPassingMarks())
                .percentage(attempt.getPercentage())
                .passed(Boolean.TRUE.equals(attempt.getPassed()))
                .totalQuestions(attempt.getExam().getQuestions().size())
                .totalAttempted(totalAttempted)
                .totalCorrect(attempt.getTotalCorrect())
                .totalWrong(attempt.getTotalWrong())
                .totalUnattempted(attempt.getTotalUnattempted())
                .accuracyRate(accuracyRate)
                .status(attempt.getStatus())
                .startTime(attempt.getStartTime())
                .submittedTime(attempt.getSubmittedTime())
                .timeTakenSeconds(timeTakenSeconds)
                .questionBreakdown(breakdown)
                .build();
    }
}
