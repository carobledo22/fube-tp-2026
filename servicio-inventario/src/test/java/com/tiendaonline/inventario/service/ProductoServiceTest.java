package com.tiendaonline.inventario.service;

import com.tiendaonline.inventario.dto.ProductoDTOs;
import com.tiendaonline.inventario.dto.ProductoDTOs.CrearProductoRequest;
import com.tiendaonline.inventario.entity.Producto;
import com.tiendaonline.inventario.exception.ProductoNotFoundException;
import com.tiendaonline.inventario.repository.ProductoRepository;
import com.tiendaonline.inventario.repository.ReservaRepository;
import com.tiendaonline.inventario.service.ProductoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.configuration.IMockitoConfiguration;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductoServiceTest {
    @Mock

    private ProductoRepository productoRepository;

    @Mock

    private ReservaRepository reservaRepository;

    @InjectMocks

    private ProductoService productoService;

    //Verificar como se crear un Producto
    @Test
    void crearProducto_cuandoRequestValido_devuelveStockResponse(){

        //Arrange
       CrearProductoRequest request = new CrearProductoRequest("Notebook lenovo", 10, new BigDecimal("1500000"));

        //Act

       ProductoDTOs.StockResponse response = productoService.crearProducto(request);

        //Assert
        assertEquals(request.getNombre(), response.getNombre());
        assertEquals(request.getCantidadDisponible(), response.getCantidadDisponible());
        assertEquals(request.getPrecioUnitario(), response.getPrecioUnitario());

    }

    //El producto se guarda correctamente
    @Test
    void crearProducto_InvocaSaveUnaVezConProductoCorrecto(){
        //Arrange
        CrearProductoRequest request = new CrearProductoRequest("Notebook lenovo", 10, new BigDecimal("1500000"));

        //Act
        productoService.crearProducto(request);

        //Assert
        ArgumentCaptor<Producto> captor = ArgumentCaptor.forClass(Producto.class);
        verify(productoRepository).save(captor.capture());
        Producto productoGuardado = captor.getValue();
        assertEquals("Notebook lenovo", productoGuardado.getNombre());
        assertEquals(10, productoGuardado.getCantidadDisponible());
        assertEquals(new BigDecimal("1500000"), productoGuardado.getPrecioUnitario());

    }

    //Test Producto Existe
    @Test
    void obtenerProductoPorId_cuandoExiste_DevuelveProducto(){
        //Arrange
        UUID id = UUID.randomUUID();
        Producto producto = new Producto (id, "NL1000","Notebook lenovo", 10, new BigDecimal("1500000"));
        when(productoRepository.findById(id)).thenReturn( Optional.of(producto));

        //Act
        ProductoDTOs.StockResponse response = productoService.obtenerPorId(id);

        // Assert
        assertEquals(id, response.getId());
        assertEquals("Notebook lenovo", response.getNombre());
        assertEquals(10, response.getCantidadDisponible());
        assertEquals(new BigDecimal("1500000"), response.getPrecioUnitario());

    }

    //Test Producto no exite
    @Test
    void obtenerProductoPorId_cuandoNoExiste_lanzaProductoNotFoundException(){
        //Arrange
        UUID id = UUID.randomUUID();
        when(productoRepository.findById(id)).thenReturn(Optional.empty());

        //Act + Assert
        ProductoNotFoundException ex = assertThrows(ProductoNotFoundException.class,() -> productoService.obtenerPorId(id));
        assertTrue(ex.getMessage().contains(id.toString()));
    }
}
