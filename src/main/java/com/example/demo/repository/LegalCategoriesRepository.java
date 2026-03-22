package com.example.demo.repository;

import com.example.demo.entity.LegalCategoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalCategoriesRepository extends JpaRepository<LegalCategoriesEntity, Integer> {
}
