package com.basquetstore.basquet_store_api.controller;

import com.basquetstore.basquet_store_api.dto.response.ShoeResponse;
import com.basquetstore.basquet_store_api.service.ShoeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/shoes")
@AllArgsConstructor
public class ShoeController {

    private final ShoeService shoeService;

    @GetMapping
    public ResponseEntity<Page<ShoeResponse>> listShoes(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer size,
            @PageableDefault(size = 4)
            Pageable pageable){

            if(brand != null) {
                String brandCapitalize = brand.substring(0, 1).toUpperCase() + brand.substring(1);
                return ResponseEntity.ok(shoeService.findAll(brandCapitalize, size, pageable));

            }
            return ResponseEntity.ok(shoeService.findAll(brand, size, pageable));

        /*
        Funcionamiento:
        findAll lista por Page 4 zapatillas (PageableDefault), funciona:
        El método primero pregunta si se recibe una marca y un talle (brand, size), si vienen incluidos en el endpoint,
        se listan aquellos modelos con stock.
        Si se recibe una marca O un talle (brand O size), listará aquellos que cumpla (si hay brand, se lista por brand,
        si hay size, se lista por size).
        Si no recibe ningún argumento (No brand y No size), listará todas por defecto.
        Por lo tanto, puede utilizar los argumentos recibidos como no.
         */
    }

    @GetMapping("/id")
    public ResponseEntity<ShoeResponse> getShoe(@PathVariable String id){
        return ResponseEntity.ok(shoeService.findById(id));
    }

}
