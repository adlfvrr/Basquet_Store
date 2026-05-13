package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.response.ShoeResponse;
import com.basquetstore.basquet_store_api.dto.response.SizeVariantResponse;
import com.basquetstore.basquet_store_api.entity.Shoe;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.repository.ShoeRepository;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ShoeServiceImpl implements ShoeService {

    //Servicio de zapatillas

    private final ShoeRepository shoeRepository;

    //Metodo auxiliar
    private ShoeResponse mapToResponse(Shoe shoe) {
        return new ShoeResponse(
                shoe.getId(),
                shoe.getBrand(),
                shoe.getModel(),
                shoe.getDescription(),
                shoe.getPrice(),
                shoe.getImageUrl(),
                shoe.getVariants().stream()
                        .map(v -> new SizeVariantResponse(v.getSize(), v.getStock()))
                        .collect(Collectors.toList())
        );
    }

    public Page<ShoeResponse> findAll(String brand, Integer size, Pageable pageable) {
        Page<Shoe> shoePage;

        //Mediante estos condicionales, según los datos que se reciban de la ruta, se mostrarán de diferente manera las zapatillas

        if (brand != null && size != null) {
            shoePage = shoeRepository.findByBrandAndSize(brand, size, pageable);
        } else if (brand != null) {
            shoePage = shoeRepository.findByBrand(brand, pageable);
        } else if (size != null) {
            shoePage = shoeRepository.findBySize(size, pageable);
        } else {
            shoePage = shoeRepository.findAll(pageable);
        }

        return shoePage.map(this::mapToResponse);
    }

    @Override
    public ShoeResponse findById(String id) {
        Shoe shoe = shoeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Zapatilla no encontrada con id: " + id));
        return mapToResponse(shoe);
    }

}
