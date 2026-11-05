package com.smartassessment.service.impl;

import com.smartassessment.dto.common.PagedResponse;
import com.smartassessment.dto.exam.ExamDetailResponseDto;
import com.smartassessment.dto.exam.ExamRequestDto;
import com.smartassessment.dto.exam.ExamResponseDto;
import com.smartassessment.dto.question.CodingProblemDto;
import com.smartassessment.dto.question.OptionResponseDto;
import com.smartassessment.dto.question.QuestionResponseDto;
import com.smartassessment.entity.Category;
import com.smartassessment.entity.Exam;
import com.smartassessment.entity.Question;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.exception.ResourceNotFoundException;
import com.smartassessment.repository.CategoryRepository;
import com.smartassessment.repository.ExamRepository;
import com.smartassessment.service.ExamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamServiceImpl implements ExamService {

    private static final Logger log = LoggerFactory.getLogger(ExamServiceImpl.class);
    private final ExamRepository examRepository;
    private final CategoryRepository categoryRepository;

    public ExamServiceImpl(ExamRepository examRepository, CategoryRepository categoryRepository) {
        this.examRepository = examRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public ExamResponseDto createExam(ExamRequestDto requestDto) {
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", requestDto.getCategoryId()));

        if (requestDto.getPassingMarks() > requestDto.getTotalMarks()) {
            throw new BadRequestException("Passing marks cannot exceed total marks");
        }

        Exam exam = Exam.builder()
                .title(requestDto.getTitle().trim())
                .description(requestDto.getDescription())
                .category(category)
                .durationMinutes(requestDto.getDurationMinutes())
                .totalMarks(requestDto.getTotalMarks())
                .passingMarks(requestDto.getPassingMarks())
                .negativeMarkingRate(requestDto.getNegativeMarkingRate() != null ? requestDto.getNegativeMarkingRate() : 0.25)
                .maxAttempts(requestDto.getMaxAttempts() != null ? requestDto.getMaxAttempts() : 3)
                .published(requestDto.getPublished() != null ? requestDto.getPublished() : false)
                .randomizeQuestions(requestDto.getRandomizeQuestions() != null ? requestDto.getRandomizeQuestions() : true)
                .randomizeOptions(requestDto.getRandomizeOptions() != null ? requestDto.getRandomizeOptions() : true)
                .build();

        Exam savedExam = examRepository.save(exam);
        log.info("Created new exam: '{}' (ID: {})", savedExam.getTitle(), savedExam.getId());

        return mapToResponseDto(savedExam);
    }

    @Override
    @Transactional
    public ExamResponseDto updateExam(Long id, ExamRequestDto requestDto) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", requestDto.getCategoryId()));

        if (requestDto.getPassingMarks() > requestDto.getTotalMarks()) {
            throw new BadRequestException("Passing marks cannot exceed total marks");
        }

        exam.setTitle(requestDto.getTitle().trim());
        exam.setDescription(requestDto.getDescription());
        exam.setCategory(category);
        exam.setDurationMinutes(requestDto.getDurationMinutes());
        exam.setTotalMarks(requestDto.getTotalMarks());
        exam.setPassingMarks(requestDto.getPassingMarks());

        if (requestDto.getNegativeMarkingRate() != null) {
            exam.setNegativeMarkingRate(requestDto.getNegativeMarkingRate());
        }
        if (requestDto.getMaxAttempts() != null) {
            exam.setMaxAttempts(requestDto.getMaxAttempts());
        }
        if (requestDto.getPublished() != null) {
            exam.setPublished(requestDto.getPublished());
        }
        if (requestDto.getRandomizeQuestions() != null) {
            exam.setRandomizeQuestions(requestDto.getRandomizeQuestions());
        }
        if (requestDto.getRandomizeOptions() != null) {
            exam.setRandomizeOptions(requestDto.getRandomizeOptions());
        }

        Exam updated = examRepository.save(exam);
        log.info("Updated exam: '{}' (ID: {})", updated.getTitle(), updated.getId());

        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        examRepository.delete(exam);
        log.info("Deleted exam ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamDetailResponseDto getExamById(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        List<QuestionResponseDto> questionDtos = exam.getQuestions().stream()
                .map(this::mapQuestionToResponseDto)
                .collect(Collectors.toList());

        return ExamDetailResponseDto.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .categoryId(exam.getCategory().getId())
                .categoryName(exam.getCategory().getName())
                .durationMinutes(exam.getDurationMinutes())
                .totalMarks(exam.getTotalMarks())
                .passingMarks(exam.getPassingMarks())
                .negativeMarkingRate(exam.getNegativeMarkingRate())
                .maxAttempts(exam.getMaxAttempts())
                .published(exam.isPublished())
                .randomizeQuestions(exam.isRandomizeQuestions())
                .randomizeOptions(exam.isRandomizeOptions())
                .totalQuestions(exam.getQuestions().size())
                .createdAt(exam.getCreatedAt())
                .questions(questionDtos)
                .build();
    }

    @Override
    @Transactional
    public ExamResponseDto togglePublish(Long id, boolean published) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));

        if (published && exam.getQuestions().isEmpty()) {
            throw new BadRequestException("Cannot publish an examination without any questions added.");
        }

        exam.setPublished(published);
        Exam updated = examRepository.save(exam);
        log.info("Toggled publish status for exam ID {}: {}", id, published);

        return mapToResponseDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExamResponseDto> getAllExams(Long categoryId, String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Exam> examPage = examRepository.searchAllExams(categoryId, (keyword != null && !keyword.isBlank()) ? keyword.trim() : null, pageable);

        List<ExamResponseDto> content = examPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return PagedResponse.<ExamResponseDto>builder()
                .content(content)
                .pageNumber(examPage.getNumber())
                .pageSize(examPage.getSize())
                .totalElements(examPage.getTotalElements())
                .totalPages(examPage.getTotalPages())
                .last(examPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ExamResponseDto> getPublishedExams(Long categoryId, String keyword, int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Exam> examPage = examRepository.searchPublishedExams(categoryId, (keyword != null && !keyword.isBlank()) ? keyword.trim() : null, pageable);

        List<ExamResponseDto> content = examPage.getContent().stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());

        return PagedResponse.<ExamResponseDto>builder()
                .content(content)
                .pageNumber(examPage.getNumber())
                .pageSize(examPage.getSize())
                .totalElements(examPage.getTotalElements())
                .totalPages(examPage.getTotalPages())
                .last(examPage.isLast())
                .build();
    }

    private ExamResponseDto mapToResponseDto(Exam exam) {
        return ExamResponseDto.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .categoryId(exam.getCategory().getId())
                .categoryName(exam.getCategory().getName())
                .durationMinutes(exam.getDurationMinutes())
                .totalMarks(exam.getTotalMarks())
                .passingMarks(exam.getPassingMarks())
                .negativeMarkingRate(exam.getNegativeMarkingRate())
                .maxAttempts(exam.getMaxAttempts())
                .published(exam.isPublished())
                .randomizeQuestions(exam.isRandomizeQuestions())
                .randomizeOptions(exam.isRandomizeOptions())
                .totalQuestions(exam.getQuestions() != null ? exam.getQuestions().size() : 0)
                .createdAt(exam.getCreatedAt())
                .build();
    }

    private QuestionResponseDto mapQuestionToResponseDto(Question q) {
        List<OptionResponseDto> options = q.getOptions().stream()
                .map(opt -> OptionResponseDto.builder()
                        .id(opt.getId())
                        .optionText(opt.getOptionText())
                        .optionOrder(opt.getOptionOrder())
                        .correct(opt.isCorrect())
                        .build())
                .collect(Collectors.toList());

        CodingProblemDto codingDto = null;
        if (q.getCodingProblem() != null) {
            codingDto = CodingProblemDto.builder()
                    .id(q.getCodingProblem().getId())
                    .starterCode(q.getCodingProblem().getStarterCode())
                    .language(q.getCodingProblem().getLanguage())
                    .constraints(q.getCodingProblem().getConstraints())
                    .sampleInput(q.getCodingProblem().getSampleInput())
                    .sampleOutput(q.getCodingProblem().getSampleOutput())
                    .testCasesJson(q.getCodingProblem().getTestCasesJson())
                    .build();
        }

        return QuestionResponseDto.builder()
                .id(q.getId())
                .examId(q.getExam().getId())
                .questionType(q.getQuestionType())
                .content(q.getContent())
                .marks(q.getMarks())
                .negativeMarks(q.getNegativeMarks())
                .explanation(q.getExplanation())
                .options(options)
                .codingProblem(codingDto)
                .build();
    }
}
