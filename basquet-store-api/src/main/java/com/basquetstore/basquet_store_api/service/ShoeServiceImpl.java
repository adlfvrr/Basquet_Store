package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.AddShoeRequest;
import com.basquetstore.basquet_store_api.dto.response.ShoeResponse;
import com.basquetstore.basquet_store_api.dto.response.ShoeVariantResponse;
import com.basquetstore.basquet_store_api.entity.Shoe;
import com.basquetstore.basquet_store_api.entity.ShoeVariant;
import com.basquetstore.basquet_store_api.exception.BadRequestException;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.repository.ShoeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
                shoe.getShoeType(),
                shoe.getPrice(),
                shoe.getImageUrl(),
                shoe.getVariants().stream()
                        .map(v -> new ShoeVariantResponse(v.getSize(), v.getStock()))
                        .collect(Collectors.toList())
        );
    }

    public Page<ShoeResponse> findAll(String brand, Integer size, String shoeType, Pageable pageable) {
        Page<Shoe> shoePage;
        //Mediante estos condicionales, según los datos que se reciban de la ruta, se mostrarán de diferente manera las zapatillas

        //Agregamos el tipo de calzado para filtrar
        if (brand != null && size != null && shoeType != null) {
            shoePage = shoeRepository.findByBrandAndSizeAndShoeType(brand, size, shoeType, pageable);
        } else if (brand != null && shoeType != null) {
            shoePage = shoeRepository.findByBrandAndShoeType(brand, shoeType, pageable);
        } else if (size != null && shoeType != null) {
            shoePage = shoeRepository.findBySizeAndShoeType(size, shoeType, pageable);
        } else if (size != null && brand != null) {
            shoePage = shoeRepository.findByBrandAndSize(brand, size, pageable);
        } else if (brand != null) {
            shoePage = shoeRepository.findByBrand(brand, pageable);
        } else if (size != null) {
            shoePage = shoeRepository.findBySize(size, pageable);
        } else if (shoeType != null) {
            shoePage = shoeRepository.findByShoeType(shoeType, pageable);
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

    /*
    Notas para este nuevo método:
    Verificar que el modelo no exista actualmente
    Verificar que los SizeVariant tengan al menos UN TALLE (39 - 42) con stock (0 o más)
    Crear la zapatilla y guardarla
     */

    @Override
    @Transactional
    public ShoeResponse addShoe(AddShoeRequest shoeRequest) {

        //Primero pasamos la marca y modelo con inicial Mayúscula
        String capitalizedBrand = shoeRequest.getBrand().substring(0, 1).toUpperCase() + shoeRequest.getBrand().substring(1);
        String capitalizedModel = shoeRequest.getModel().substring(0, 1).toUpperCase() + shoeRequest.getModel().substring(1);

        //Verificamos que no exista
        if (shoeRepository.existsByBrandAndModel(capitalizedBrand, capitalizedModel)) {
            throw new BadRequestException("La zapatilla ya existe");
        }

        //Creamos la entity
        Shoe shoeFromRequest = new Shoe(shoeRequest.getBrand(),
                shoeRequest.getModel(),
                shoeRequest.getDescription(),
                shoeRequest.getShoeType().toUpperCase(),
                shoeRequest.getPrice(),
                shoeRequest.getImageUrl(),
                shoeRequest.getShoeVariants());

        //Verifico que los variants no tengan un talle menor a 39 o con stock negativo (stock 0 está permitido)
        //Para evitar la aparición de errores, primero verificamos que tipo de calzado es, luego verificamos sus variants
        List<ShoeVariant> shoeVariants = shoeFromRequest.getVariants();
        if (shoeFromRequest.getShoeType().equalsIgnoreCase("GENERAL")) {
            for (ShoeVariant variant : shoeVariants) {
                if (variant.getSize() < 39 || variant.getSize() > 42) {
                    throw new BadRequestException("No se puede ingresar talles menores a 39/mayores a 42 de calzado de tipo 'General'");
                }
                if(variant.getStock() < 0){
                    throw new BadRequestException("El stock no puede ser negativo (minimo 0)");
                }
            }
        }
        //Agregamos una verificación nueva con nuevas condiciones: Si es de tipo general o para niños
        if (shoeFromRequest.getShoeType().equalsIgnoreCase("KIDS")) {
            for (ShoeVariant variant : shoeVariants) {
                if (variant.getSize() < 36 || variant.getSize() > 39) {
                    throw new BadRequestException("No se puede ingresar talles menores a 36/mayores a 39 de calzado de tipo 'Kids'");
                }
                if(variant.getStock() < 0){
                    throw new BadRequestException("El stock no puede ser negativo (minimo 0)");
                }
            }
        }

        //Guardamos y posteriormente retornamos
        shoeRepository.save(shoeFromRequest);

        return mapToResponse(shoeFromRequest);

    }

}
