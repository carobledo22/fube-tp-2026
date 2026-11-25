package com.tiendaonline.pedidos.integration;

import com.tiendaonline.pedidos.dto.PedidoDTOs.CrearPedidoRequest;
import com.tiendaonline.pedidos.dto.PedidoDTOs.ItemPedidoRequest;
import com.tiendaonline.pedidos.dto.PedidoDTOs.PedidoResponse;
import com.tiendaonline.pedidos.entity.EstadoPedido;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CrearPedidoIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private ClientAndServer mockServerClientes;
    private ClientAndServer mockServerInventario;

    @BeforeEach
    void setUp() {
        mockServerClientes = ClientAndServer.startClientAndServer(8081);
        mockServerInventario = ClientAndServer.startClientAndServer(8082);
    }

    @AfterEach
    void tearDown() {
        mockServerClientes.stop();
        mockServerInventario.stop();
    }

    //Prueba de servicio de Integracion - Happy Path
    @Test
    void crearPedido_cuandoClienteYProductoExistenConStock_devuelvePedidoCreado() {

        //Arrange
        UUID clienteId = UUID.randomUUID();

        UUID productoId = UUID.randomUUID();

        mockServerClientes.when(
                request().withMethod("GET").withPath("/api/v1/clientes/" + clienteId))
            .respond(
                response().withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"" + clienteId + "\",\"nombre\":\"Juan\",\"email\":\"juan@gmail.com\"}"));

        mockServerInventario.when(
                request().withMethod("GET").withPath("/api/v1/productos/" + productoId))
            .respond(
                response().withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"" + productoId + "\",\"nombre\":\"Notebook\",\"cantidadDisponible\":10,\"precioUnitario\":1500000}"));

        mockServerInventario.when(
                request().withMethod("POST").withPath("/api/v1/reservas/batch"))
            .respond(
                response().withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("[]"));

        CrearPedidoRequest request = new CrearPedidoRequest(
                clienteId,
                List.of(new ItemPedidoRequest(productoId, 2)));

        //Act
        ResponseEntity<PedidoResponse> response = restTemplate.postForEntity(
                "/api/v1/pedidos", request, PedidoResponse.class);

        //Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getPedidoId());
        assertEquals(clienteId, response.getBody().getClienteId());
        assertEquals(EstadoPedido.CREADO, response.getBody().getEstadoPedido());
        assertEquals(0, new BigDecimal("3000000.00").compareTo(response.getBody().getMontoTotal()));

        mockServerClientes.verify(
                request().withMethod("GET").withPath("/api/v1/clientes/" + clienteId),
                VerificationTimes.exactly(1));

        mockServerInventario.verify(
                request().withMethod("POST").withPath("/api/v1/reservas/batch"),
                VerificationTimes.exactly(1));
    }

    //Prueba de servicio de Integracion - Cliente no existe
    @Test
    void crearPedido_cuandoClienteNoExiste_devuelve404() {

        //Arrange
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        mockServerClientes.when(
                request().withMethod("GET").withPath("/api/v1/clientes/" + clienteId))
            .respond(
                response().withStatusCode(404)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"Cliente con id " + clienteId + " no encontrado\"}"));

        CrearPedidoRequest request = new CrearPedidoRequest(
                clienteId,
                List.of(new ItemPedidoRequest(productoId, 2)));

        //Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/pedidos", request, String.class);

        //Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        mockServerClientes.verify(
                request().withMethod("GET").withPath("/api/v1/clientes/" + clienteId),
                VerificationTimes.exactly(1));

        mockServerInventario.verify(
                request().withMethod("POST").withPath("/api/v1/reservas/batch"),
                VerificationTimes.exactly(0));
    }

    //Prueba de servicio de Integracion - Stock insuficiente
    @Test
    void crearPedido_cuandoStockInsuficiente_devuelve400() {

        //Arrange
        UUID clienteId = UUID.randomUUID();
        UUID productoId = UUID.randomUUID();

        mockServerClientes.when(
                request().withMethod("GET").withPath("/api/v1/clientes/" + clienteId))
            .respond(
                response().withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"" + clienteId + "\",\"nombre\":\"Juan\",\"email\":\"juan@gmail.com\"}"));

        mockServerInventario.when(
                request().withMethod("GET").withPath("/api/v1/productos/" + productoId))
            .respond(
                response().withStatusCode(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"id\":\"" + productoId + "\",\"nombre\":\"Notebook\",\"cantidadDisponible\":10,\"precioUnitario\":1500000}"));

        mockServerInventario.when(
                request().withMethod("POST").withPath("/api/v1/reservas/batch"))
            .respond(
                response().withStatusCode(400)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"message\":\"Stock insuficiente para el producto: Notebook\"}"));

        CrearPedidoRequest request = new CrearPedidoRequest(
                clienteId,
                List.of(new ItemPedidoRequest(productoId, 2)));

        //Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/pedidos", request, String.class);

        //Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        mockServerInventario.verify(
                request().withMethod("POST").withPath("/api/v1/reservas/batch"),
                VerificationTimes.exactly(1));
    }
}
