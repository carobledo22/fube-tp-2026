package com.tiendaonline.inventario.controller;


import com.tiendaonline.inventario.dto.ProductoDTOs.CrearProductoRequest;
import com.tiendaonline.inventario.dto.ProductoDTOs.StockResponse;
import com.tiendaonline.inventario.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/productos")
public class ProductoController {
    private final ProductoService productoService;

    @PostMapping
    public ResponseEntity<StockResponse> crearProducto(@Valid @RequestBody CrearProductoRequest request) {

        StockResponse creado = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> obtenerPorId(@PathVariable UUID id){
        StockResponse response = productoService.obtenerPorId(id);
        return ResponseEntity.ok(response);
    }
}
