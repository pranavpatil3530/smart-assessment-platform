package com.smartassessment.service.impl;

import com.smartassessment.dto.question.CodingProblemDto;
import com.smartassessment.dto.question.OptionRequestDto;
import com.smartassessment.dto.question.OptionResponseDto;
import com.smartassessment.dto.question.QuestionRequestDto;
import com.smartassessment.dto.question.QuestionResponseDto;
import com.smartassessment.entity.*;
import com.smartassessment.exception.BadRequestException;
import com.smartassessment.exception.ResourceNotFoundException;
import com.smartassessment.repository.ExamRepository;
import com.smartassessment.repository.QuestionRepository;
import com.smartassessment.service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionServiceImpl.class);
    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository, ExamRepository examRepository) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
    }

    @Override
    @Transactional
    public QuestionResponseDto addQuestionToExam(Long examId, QuestionRequestDto requestDto) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));

        validateQuestionRequest(requestDto);

        Question question = Question.builder()
                .exam(exam)
                .questionType(requestDto.getQuestionType())
                .content(requestDto.getContent().trim())
                .marks(requestDto.getMarks())
                .negativeMarks(requestDto.getNegativeMarks() != null ? requestDto.getNegativeMarks() : (exam.getNegativeMarkingRate() * requestDto.getMarks()))
                .explanation(requestDto.getExplanation())
                .options(new ArrayList<>())
                .build();

        if (requestDto.getQuestionType() == QuestionType.MCQ) {
            int order = 1;
            for (OptionRequestDto optDto : requestDto.getOptions()) {
                QuestionOption option = QuestionOption.builder()
                        .optionText(optDto.getOptionText().trim())
                        .optionOrder(optDto.getOptionOrder() != null ? optDto.getOptionOrder() : order++)
                        .correct(Boolean.TRUE.equals(optDto.getCorrect()))
                        .build();
                question.addOption(option);
            }
        } else if (requestDto.getQuestionType() == QuestionType.CODING) {
            CodingProblemDto cpDto = requestDto.getCodingProblem();
            CodingProblem problem = CodingProblem.builder()
                    .starterCode(cpDto != null ? cpDto.getStarterCode() : "")
                    .language(cpDto != null && cpDto.getLanguage() != null ? cpDto.getLanguage() : "Java")
                    .constraints(cpDto != null ? cpDto.getConstraints() : "")
                    .sampleInput(cpDto != null ? cpDto.getSampleInput() : "")
                    .sampleOutput(cpDto != null ? cpDto.getSampleOutput() : "")
                    .testCasesJson(cpDto != null ? cpDto.getTestCasesJson() : "[]")
                    .build();
            question.setCodingProblem(problem);
        }

        Question saved = questionRepository.save(question);
        log.info("Added {} question ID: {} to exam ID: {}", saved.getQuestionType(), saved.getId(), examId);

        return mapToResponseDto(saved);
    }

    @Override
    @Transactional
    public QuestionResponseDto updateQuestion(Long questionId, QuestionRequestDto requestDto) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        validateQuestionRequest(requestDto);

        question.setQuestionType(requestDto.getQuestionType());
        question.setContent(requestDto.getContent().trim());
        question.setMarks(requestDto.getMarks());
        if (requestDto.getNegativeMarks() != null) {
            question.setNegativeMarks(requestDto.getNegativeMarks());
        }
        question.setExplanation(requestDto.getExplanation());

        if (requestDto.getQuestionType() == QuestionType.MCQ) {
            question.getOptions().clear();
            int order = 1;
            for (OptionRequestDto optDto : requestDto.getOptions()) {
                QuestionOption option = QuestionOption.builder()
                        .optionText(optDto.getOptionText().trim())
                        .optionOrder(optDto.getOptionOrder() != null ? optDto.getOptionOrder() : order++)
                        .correct(Boolean.TRUE.equals(optDto.getCorrect()))
                        .build();
                question.addOption(option);
            }
            question.setCodingProblem(null);
        } else if (requestDto.getQuestionType() == QuestionType.CODING) {
            question.getOptions().clear();
            CodingProblemDto cpDto = requestDto.getCodingProblem();
            CodingProblem problem = question.getCodingProblem();
            if (problem == null) {
                problem = new CodingProblem();
            }
            problem.setStarterCode(cpDto != null ? cpDto.getStarterCode() : "");
            problem.setLanguage(cpDto != null && cpDto.getLanguage() != null ? cpDto.getLanguage() : "Java");
            problem.setConstraints(cpDto != null ? cpDto.getConstraints() : "");
            problem.setSampleInput(cpDto != null ? cpDto.getSampleInput() : "");
            problem.setSampleOutput(cpDto != null ? cpDto.getSampleOutput() : "");
            problem.setTestCasesJson(cpDto != null ? cpDto.getTestCasesJson() : "[]");
            question.setCodingProblem(problem);
        }

        Question updated = questionRepository.save(question);
        log.info("Updated question ID: {}", updated.getId());

        return mapToResponseDto(updated);
    }

    @Override
    @Transactional
    public void deleteQuestion(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question", "id", questionId));

        questionRepository.delete(question);
        log.info("Deleted question ID: {}", questionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDto> getQuestionsByExamId(Long examId) {
        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("Exam", "id", examId);
        }
        return questionRepository.findByExamId(examId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private void validateQuestionRequest(QuestionRequestDto requestDto) {
        if (requestDto.getQuestionType() == QuestionType.MCQ) {
            if (requestDto.getOptions() == null || requestDto.getOptions().size() < 2) {
                throw new BadRequestException("MCQ Question must have at least 2 options.");
            }
            long correctCount = requestDto.getOptions().stream()
                    .filter(opt -> Boolean.TRUE.equals(opt.getCorrect()))
                    .count();
            if (correctCount == 0) {
                throw new BadRequestException("MCQ Question must have at least one marked correct option.");
            }
        }
    }

    private QuestionResponseDto mapToResponseDto(Question q) {
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
