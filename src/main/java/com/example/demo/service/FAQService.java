package com.example.demo.service;

import com.example.demo.model.dto.FAQDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

public interface FAQService {
    FAQDTO create(FAQDTO faqDTO);
    FAQDTO update(FAQDTO faqDTO);
    void deleteFAQ(Integer id);
    FAQDTO findById(Integer id);
    List<FAQDTO> findAll();
}
