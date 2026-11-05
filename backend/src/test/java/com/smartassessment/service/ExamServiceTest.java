package com.smartassessment.service;

import com.smartassessment.dto.exam.ExamRequestDto;
import com.smartassessment.dto.exam.ExamResponseDto;
import com.smartassessment.entity.Category;
import com.smartassessment.entity.Exam;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.exception.ResourceNotFoundException;
import com.smartassessment.repository.CategoryRepository;
import com.smartassessment.repository.ExamRepository;
import com.smartassessment.service.impl.ExamServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExamServiceTest {

    @Mock
    private ExamRepository examRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ExamServiceImpl examService;

    private Category testCategory;
    private Exam testExam;
    private ExamRequestDto testRequestDto;

    @BeforeEach
    void setUp() {
        testCategory = Category.builder()
                .id(1L)
                .name("Java Backend")
                .description("Java category")
                .build();

        testExam = Exam.builder()
                .id(100L)
                .title("Spring Boot Certification")
                .description("Test description")
                .category(testCategory)
                .durationMinutes(60)
                .totalMarks(100)
                .passingMarks(60)
                .negativeMarkingRate(0.25)
                .maxAttempts(3)
                .published(true)
                .build();

        testRequestDto = ExamRequestDto.builder()
                .title("Spring Boot Certification")
                .description("Test description")
                .categoryId(1L)
                .durationMinutes(60)
                .totalMarks(100)
                .passingMarks(60)
                .negativeMarkingRate(0.25)
                .maxAttempts(3)
                .published(true)
                .build();
    }

    @Test
    @DisplayName("Should successfully create an examination when request is valid")
    void createExam_Success() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(examRepository.save(any(Exam.class))).thenReturn(testExam);

        ExamResponseDto response = examService.createExam(testRequestDto);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getTitle()).isEqualTo("Spring Boot Certification");
        assertThat(response.getCategoryName()).isEqualTo("Java Backend");
        verify(examRepository, times(1)).save(any(Exam.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException when passing marks exceed total marks")
    void createExam_InvalidMarks_ThrowsBadRequest() {
        testRequestDto.setPassingMarks(120);
        testRequestDto.setTotalMarks(100);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        assertThatThrownBy(() -> examService.createExam(testRequestDto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Passing marks cannot exceed total marks");

        verify(examRepository, never()).save(any(Exam.class));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when category does not exist")
    void createExam_CategoryNotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> examService.createExam(testRequestDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Category not found");

        verify(examRepository, never()).save(any(Exam.class));
    }

    @Test
    @DisplayName("Should delete exam when it exists")
    void deleteExam_Success() {
        when(examRepository.findById(100L)).thenReturn(Optional.of(testExam));
        doNothing().when(examRepository).delete(testExam);

        examService.deleteExam(100L);

        verify(examRepository, times(1)).delete(testExam);
    }
}
