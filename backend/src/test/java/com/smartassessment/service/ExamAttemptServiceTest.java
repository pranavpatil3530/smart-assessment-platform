package com.smartassessment.service;

import com.smartassessment.dto.attempt.ExamResultResponseDto;
import com.smartassessment.dto.attempt.StudentAnswerSubmissionDto;
import com.smartassessment.dto.attempt.SubmitExamRequestDto;
import com.smartassessment.entity.*;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.repository.ExamAttemptRepository;
import com.smartassessment.repository.ExamRepository;
import com.smartassessment.repository.QuestionOptionRepository;
import com.smartassessment.repository.UserRepository;
import com.smartassessment.service.impl.ExamAttemptServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamAttemptServiceTest {

    @Mock
    private ExamAttemptRepository examAttemptRepository;

    @Mock
    private ExamRepository examRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private QuestionOptionRepository questionOptionRepository;

    @InjectMocks
    private ExamAttemptServiceImpl examAttemptService;

    private User testStudent;
    private Exam testExam;
    private Question question1;
    private QuestionOption correctOption;
    private QuestionOption wrongOption;
    private ExamAttempt testAttempt;

    @BeforeEach
    void setUp() {
        testStudent = User.builder()
                .id(1L)
                .username("student1")
                .email("student1@example.com")
                .role(Role.ROLE_STUDENT)
                .build();

        Category category = Category.builder().id(1L).name("Java").build();

        testExam = Exam.builder()
                .id(10L)
                .title("Java Core Test")
                .category(category)
                .durationMinutes(30)
                .totalMarks(10)
                .passingMarks(6)
                .negativeMarkingRate(0.5) // -0.5 marks deduction per wrong answer
                .maxAttempts(2)
                .published(true)
                .questions(new ArrayList<>())
                .build();

        question1 = Question.builder()
                .id(101L)
                .exam(testExam)
                .questionType(QuestionType.MCQ)
                .content("What is the parent class of all classes in Java?")
                .marks(4.0)
                .negativeMarks(1.0)
                .options(new ArrayList<>())
                .build();

        correctOption = QuestionOption.builder()
                .id(201L)
                .question(question1)
                .optionText("Object")
                .correct(true)
                .build();

        wrongOption = QuestionOption.builder()
                .id(202L)
                .question(question1)
                .optionText("Class")
                .correct(false)
                .build();

        question1.addOption(correctOption);
        question1.addOption(wrongOption);
        testExam.addQuestion(question1);

        testAttempt = ExamAttempt.builder()
                .id(500L)
                .user(testStudent)
                .exam(testExam)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(30))
                .status(AttemptStatus.IN_PROGRESS)
                .build();
    }

    @Test
    @DisplayName("Should evaluate correct MCQ answer and award positive marks")
    void submitExam_CorrectAnswer_AwardsFullMarks() {
        SubmitExamRequestDto request = SubmitExamRequestDto.builder()
                .answers(List.of(
                        StudentAnswerSubmissionDto.builder()
                                .questionId(101L)
                                .selectedOptionId(201L)
                                .build()
                ))
                .build();

        when(examAttemptRepository.findById(500L)).thenReturn(Optional.of(testAttempt));
        when(questionOptionRepository.findById(201L)).thenReturn(Optional.of(correctOption));
        when(examAttemptRepository.save(any(ExamAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExamResultResponseDto result = examAttemptService.submitExam(500L, 1L, request, false);

        assertThat(result).isNotNull();
        assertThat(result.getScore()).isEqualTo(4.0);
        assertThat(result.getTotalCorrect()).isEqualTo(1);
        assertThat(result.getTotalWrong()).isEqualTo(0);
        assertThat(result.getStatus()).isEqualTo(AttemptStatus.SUBMITTED);
        verify(examAttemptRepository, times(1)).save(any(ExamAttempt.class));
    }

    @Test
    @DisplayName("Should evaluate wrong MCQ answer and deduct negative marks")
    void submitExam_WrongAnswer_AppliesNegativeMarking() {
        SubmitExamRequestDto request = SubmitExamRequestDto.builder()
                .answers(List.of(
                        StudentAnswerSubmissionDto.builder()
                                .questionId(101L)
                                .selectedOptionId(202L)
                                .build()
                ))
                .build();

        when(examAttemptRepository.findById(500L)).thenReturn(Optional.of(testAttempt));
        when(questionOptionRepository.findById(202L)).thenReturn(Optional.of(wrongOption));
        when(examAttemptRepository.save(any(ExamAttempt.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ExamResultResponseDto result = examAttemptService.submitExam(500L, 1L, request, false);

        assertThat(result).isNotNull();
        // Since score is clamped at 0.0 (cannot be negative aggregate):
        assertThat(result.getScore()).isEqualTo(0.0);
        assertThat(result.getTotalCorrect()).isEqualTo(0);
        assertThat(result.getTotalWrong()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should throw BadRequestException when student exceeds max attempts limit")
    void startExam_MaxAttemptsExceeded_ThrowsBadRequest() {
        when(examRepository.findById(10L)).thenReturn(Optional.of(testExam));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(examAttemptRepository.findFirstByUserIdAndExamIdAndStatus(1L, 10L, AttemptStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());
        when(examAttemptRepository.countByUserIdAndExamId(1L, 10L)).thenReturn(2L); // Max is 2

        assertThatThrownBy(() -> examAttemptService.startOrResumeExam(10L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("maximum allowed attempts");
    }
}
