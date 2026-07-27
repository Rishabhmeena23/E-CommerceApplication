package com.ecomm.productservice.controller;

import com.ecomm.productservice.dto.request.SubCategoryRequest;
import com.ecomm.productservice.dto.response.SubCategoryResponse;
import com.ecomm.productservice.service.SubCategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/subcategories")
@RequiredArgsConstructor
public class SubCategoryController {

    private final SubCategoryService subCategoryService;

    @PostMapping
    public ResponseEntity<SubCategoryResponse> create(@Valid @RequestBody SubCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subCategoryService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SubCategoryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SubCategoryRequest request
    ) {
        return ResponseEntity.ok(subCategoryService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        subCategoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubCategoryResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(subCategoryService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<SubCategoryResponse>> getAll(
            @RequestParam(required = false) Long categoryId
    ) {
        return ResponseEntity.ok(subCategoryService.getAll(categoryId));
    }
}
