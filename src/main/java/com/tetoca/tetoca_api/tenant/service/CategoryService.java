package com.tetoca.tetoca_api.tenant.service;

import com.tetoca.tetoca_api.tenant.dto.CategoryResponse;
import com.tetoca.tetoca_api.tenant.models.Division;
import com.tetoca.tetoca_api.tenant.repositories.DivisionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final DivisionRepository divisionRepository;
    
    public List<CategoryResponse> getAllCategories() {
        return divisionRepository.findByRecordStatus("A").stream()
                .map(this::mapToCategoryResponse)
                .collect(Collectors.toList());
    }
    
    private CategoryResponse mapToCategoryResponse(Division division) {
        return CategoryResponse.builder()
                .id(division.getId().toString())
                .name(division.getName())
                .iconName("business-outline") // Icono por defecto
                .color("#4b7bec") // Color por defecto
                .build();
    }
}