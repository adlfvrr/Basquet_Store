package com.basquetstore.basquet_store_api.controller;

import com.basquetstore.basquet_store_api.dto.request.AddClothingRequest;
import com.basquetstore.basquet_store_api.dto.request.AddShoeRequest;
import com.basquetstore.basquet_store_api.dto.response.ClothingResponse;
import com.basquetstore.basquet_store_api.dto.response.ShoeResponse;
import com.basquetstore.basquet_store_api.service.ClothingService;
import com.basquetstore.basquet_store_api.service.ShoeService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/clothing")
@AllArgsConstructor
public class ClothingController {

    //Controlador de indumentaria

    private final ClothingService clothingService;

    @GetMapping
    public ResponseEntity<Page<ClothingResponse>> listClothing(@RequestParam(required = false) String brand,
                                                               @RequestParam(required = false) String size,
                                                               @RequestParam(required = false) String section,
                                                               @PageableDefault(size = 6) Pageable pageable) {

        String brandCapitalize = null;
        String sectionCapitalize = null;
        String sizeCapitalize = null;
        if (brand != null) {
            brandCapitalize = brand.substring(0, 1).toUpperCase() + brand.substring(1);
        }
        if (section != null) {
            sectionCapitalize = section.substring(0, 1).toUpperCase() + section.substring(1);
        }
        if(size != null){
            sizeCapitalize = size.toUpperCase();
        }

        return ResponseEntity.ok(clothingService.findAll(brandCapitalize, sizeCapitalize, sectionCapitalize, pageable));

    }

    @GetMapping("/id")
    public ResponseEntity<ClothingResponse> getClothing(@PathVariable String id) {
        return ResponseEntity.ok(clothingService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClothingResponse> addClothing(@Valid @RequestBody AddClothingRequest request) {
        return ResponseEntity.ok(clothingService.addClothing(request));
    }

}
