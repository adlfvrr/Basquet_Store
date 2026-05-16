package com.basquetstore.basquet_store_api.dto.request;

import com.basquetstore.basquet_store_api.entity.SizeVariant;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class AddShoeRequest {

    @NotBlank(message = "Campo obligatorio: marca")
    private String brand;

    @NotBlank(message = "Campo obligatorio: descripción")
    private String description;

    private String imageUrl;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal price;

    @NotBlank(message = "Campo obligatorio: modelo")
    private String model;

    @NotEmpty(message = "Debe contener al menos un talle")
    private List<SizeVariant> sizeVariants = new ArrayList<>();

}
