package com.example.demo.service;

import com.example.demo.model.dto.LegalCategoriesDTO;
import java.util.List;

public interface LegalCategoriesService {
    LegalCategoriesDTO create(LegalCategoriesDTO dto);
    LegalCategoriesDTO update(LegalCategoriesDTO dto);
    void delete(Integer id);
    LegalCategoriesDTO findById(Integer id);
    List<LegalCategoriesDTO> findAll();
}
