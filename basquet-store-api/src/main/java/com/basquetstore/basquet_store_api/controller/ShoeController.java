package com.basquetstore.basquet_store_api.controller;

import com.basquetstore.basquet_store_api.dto.request.AddShoeRequest;
import com.basquetstore.basquet_store_api.dto.response.ShoeResponse;
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
@RequestMapping("api/shoes")
@AllArgsConstructor
public class ShoeController {

    //Controlador de zapatillas

    private final ShoeService shoeService;

    @GetMapping
    public ResponseEntity<Page<ShoeResponse>> listShoes(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String shoeType,
            @PageableDefault(size = 6)
            Pageable pageable) {

        /*
         Es necesario para el funcionamiento correcto del filtrado la correcta utilización de mayúsculas de la marca y el tipo de calzado
         Por lo tanto, si la marca y el tipo son nulos, no ocurrirán excepciones, pues eso está controlado en el servicio, no pasa nada si se inicializan asi.
         En cambio, si no son nulos, debemos filtrar según figuren las zapatillas en nuestra BDD (Marca comienza con Mayús y tipo es la palabra en Mayús).
         */

        String brandCapitalize = null;
        String shoeTypeCapitalized = null;
        if (brand != null) {
            brandCapitalize = brand.substring(0, 1).toUpperCase() + brand.substring(1).toLowerCase();
        }
        if (shoeType != null) {
            shoeTypeCapitalized = shoeType.toUpperCase();
        }

        //Capitalizamos la marca y el tipo de zapatillas para que los servicios trabajen de forma correcta

        return ResponseEntity.ok(shoeService.findAll(brandCapitalize, size, shoeTypeCapitalized, pageable));

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
    public ResponseEntity<ShoeResponse> getShoe(@PathVariable String id) {
        return ResponseEntity.ok(shoeService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ShoeResponse> addShoe(@Valid @RequestBody AddShoeRequest request) {
        return ResponseEntity.ok(shoeService.addShoe(request));
    }

}
