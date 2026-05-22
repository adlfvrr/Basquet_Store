package com.basquetstore.basquet_store_api.service;

import com.basquetstore.basquet_store_api.dto.request.AddClothingRequest;
import com.basquetstore.basquet_store_api.dto.response.ClothingResponse;
import com.basquetstore.basquet_store_api.dto.response.ClothingVariantResponse;
import com.basquetstore.basquet_store_api.entity.Clothing;
import com.basquetstore.basquet_store_api.entity.ClothingVariant;
import com.basquetstore.basquet_store_api.entity.ShoeVariant;
import com.basquetstore.basquet_store_api.exception.BadRequestException;
import com.basquetstore.basquet_store_api.exception.ResourceNotFoundException;
import com.basquetstore.basquet_store_api.repository.ClothingRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClothingServiceImpl implements ClothingService {
    //Servicio de indumentaria

    private final ClothingRepository clothingRepository;

    //Metodo auxiliar
    private ClothingResponse mapToResponse(Clothing clothing) {
        return new ClothingResponse(clothing.getId(),
                clothing.getBrand(),
                clothing.getModel(),
                clothing.getDescription(),
                clothing.getSection(),
                clothing.getPrice(),
                clothing.getImageUrl(),
                clothing.getVariants().stream()
                        .map(v -> new ClothingVariantResponse(v.getSize(), v.getStock()))
                        .collect(Collectors.toList()));
    }

    public Page<ClothingResponse> findAll(String brand, String size, String section, Pageable pageable) {
        Page<Clothing> clothingPage;
        //Mediante estos condicionales, según los datos que se reciban de la ruta, se mostrarán de diferente manera la indumentaria

        if (brand != null && size != null && section != null) {
            clothingPage = clothingRepository.findByBrandAndSizeAndSection(brand, size, section, pageable);
        } else if (brand != null && section != null) {
            clothingPage = clothingRepository.findByBrandAndSection(brand, section, pageable);
        } else if (size != null && section != null) {
            clothingPage = clothingRepository.findBySizeAndSection(size, section, pageable);
        } else if (size != null && brand != null) {
            clothingPage = clothingRepository.findByBrandAndSize(brand, size, pageable);
        } else if (brand != null) {
            clothingPage = clothingRepository.findByBrand(brand, pageable);
        } else if (size != null) {
            clothingPage = clothingRepository.findBySize(size, pageable);
        } else if (section != null) {
            clothingPage = clothingRepository.findBySection(section, pageable);
        } else {
            clothingPage = clothingRepository.findAll(pageable);
        }

        return clothingPage.map(this::mapToResponse);
    }

    @Override
    public ClothingResponse findById(String id) {
        Clothing clothing = clothingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Zapatilla no encontrada con id: " + id));
        return mapToResponse(clothing);
    }

    @Override
    @Transactional
    public ClothingResponse addClothing(AddClothingRequest clothingRequest) {

        String capitalizedBrand = clothingRequest.getBrand().substring(0, 1).toUpperCase() + clothingRequest.getBrand().substring(1);
        String capitalizedModel = clothingRequest.getModel().substring(0, 1).toUpperCase() + clothingRequest.getModel().substring(1);

        if (clothingRepository.existsByBrandAndModel(capitalizedBrand, capitalizedModel)) {
            throw new BadRequestException("La indumentaria ya existe");
        }

        Clothing clothingFromRequest = new Clothing(clothingRequest.getBrand(), clothingRequest.getModel(), clothingRequest.getDescription(), clothingRequest.getSection().toUpperCase(), clothingRequest.getImageUrl(), clothingRequest.getPrice(), clothingRequest.getClothingVariants());


        //Ahora, como estamos verificando indumentaria, solamente tendremos en cuenta talles desde XS a XL


        //Tenemos acá nuestro vector con los talles válidos
        String[] commonSizes = {"XS", "S", "M", "L", "XL"};
        List<ClothingVariant> clothingVariants = clothingFromRequest.getVariants();
        for (ClothingVariant variant : clothingVariants) {
            //Verificamos que el talle coincida con alguno de nuestros talles dentro del vector
            if (!Arrays.asList(commonSizes).contains(variant.getSize())) {
                throw new BadRequestException("Los talles válidos para la indumentaria deben ser desde XS a XL");
            }
            if (variant.getStock() < 0) {
                throw new BadRequestException("El stock no puede ser negativo (minimo 0)");
            }
        }

        clothingRepository.save(clothingFromRequest);

        return mapToResponse(clothingFromRequest);

    }
}
