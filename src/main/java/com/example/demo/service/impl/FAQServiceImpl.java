package com.example.demo.service.impl;

import com.example.demo.convert.FAQConvert;
import com.example.demo.model.dto.FAQDTO;
import com.example.demo.repository.FAQRepository;
import com.example.demo.service.FAQService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@lombok.extern.slf4j.Slf4j
public class FAQServiceImpl implements FAQService {
    private final FAQRepository faqRepository;
    private final FAQConvert faqConvert;
    @Override
    public FAQDTO create(FAQDTO faqDTO) {
        log.info("Creating new FAQ: {}", faqDTO.getQuestion());
        com.example.demo.entity.FAQEntity faqEntity = faqConvert.toFAQEntity(faqDTO);
        faqEntity.setCreateAt(new java.util.Date());
        faqEntity = faqRepository.save(faqEntity);
        log.info("Successfully created FAQ with id: {}", faqEntity.getId());
        return faqConvert.toFAQDTO(faqEntity);
    }

    @Override
    public FAQDTO update(FAQDTO faqDTO) {
        log.info("Updating FAQ with id: {}", faqDTO.getId());
        com.example.demo.entity.FAQEntity existingEntity = faqRepository.findById(faqDTO.getId())
                .orElseThrow(() -> {
                    log.error("FAQ update failed: not found with id: {}", faqDTO.getId());
                    return new RuntimeException("FAQ not found");
                });
        existingEntity.setQuestion(faqDTO.getQuestion());
        existingEntity.setAnswer(faqDTO.getAnswer());
        existingEntity = faqRepository.save(existingEntity);
        log.info("Successfully updated FAQ with id: {}", existingEntity.getId());
        return faqConvert.toFAQDTO(existingEntity);
    }

    @Override
    public void deleteFAQ(Integer id) {
        log.info("Deleting FAQ with id: {}", id);
        try {
            faqRepository.deleteById(id);
            log.info("Successfully deleted FAQ with id: {}", id);
        } catch (Exception e) {
            log.error("Error deleting FAQ with id: {}", id, e);
            throw new RuntimeException("Error deleting FAQ", e);
        }
    }

    @Override
    public FAQDTO findById(Integer id) {
        log.info("Fetching FAQ with id: {}", id);
        return faqRepository.findById(id)
                .map(faqConvert::toFAQDTO)
                .orElseThrow(() -> {
                    log.error("FAQ fetch failed: not found with id: {}", id);
                    return new RuntimeException("FAQ not found");
                });
    }

    @Override
    public List<FAQDTO> findAll() {
        log.info("Fetching all FAQs");
        List<FAQDTO> faqs = faqRepository.findAll().stream()
                .map(faqConvert::toFAQDTO)
                .collect(java.util.stream.Collectors.toList());
        log.info("Found {} FAQs", faqs.size());
        return faqs;
    }
}
