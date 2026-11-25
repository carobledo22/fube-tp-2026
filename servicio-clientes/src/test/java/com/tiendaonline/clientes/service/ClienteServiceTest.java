package com.tiendaonline.clientes.service;

import com.tiendaonline.clientes.dto.ClienteDTOs;
import com.tiendaonline.clientes.dto.ClienteDTOs.ClienteResponse;
import com.tiendaonline.clientes.dto.ClienteDTOs.ClienteRequest;
import com.tiendaonline.clientes.entity.Cliente;
import com.tiendaonline.clientes.exception.ClienteNotFoundException;
import com.tiendaonline.clientes.repository.ClienteRepository;
import com.tiendaonline.clientes.service.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClienteServiceTest {

    @Mock

    private ClienteRepository clienteRepository;

    @Mock
    private PiiMaskingService piiMaskingService;

    @InjectMocks

    private ClienteService clienteService;

    //Verificar como se crear un Cliente
    @Test
    void crearCliente_cuandoRequestValido_devuelveResponseConMismosDatos(){

    //Arrange
        ClienteRequest request = new ClienteRequest("Juan Perez", "jperez@gmail.com");

    //Act

        ClienteResponse response = clienteService.crearCliente(request);

    //Assert
        assertEquals(request.getNombre(), response.getNombre());
        assertEquals(request.getEmail(), response.getEmail());
        assertNotNull(response.getFechaAlta());

    }

    //El cliente se guarda correctamente
    @Test
    void crearCliente_InvocaSaveUnaVezConClienteCorrecto(){
        //Arrange
        ClienteRequest request = new ClienteRequest("Juan Perez", "jperez@gmail.com");

        //Act
        clienteService.crearCliente(request);

        //Assert
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(clienteRepository).save(captor.capture());
        Cliente clienteGuardado = captor.getValue();
        assertEquals("Juan Perez", clienteGuardado.getNombre());
        assertEquals("jperez@gmail.com", clienteGuardado.getEmail());
        assertNotNull(clienteGuardado.getFechaAlta());

    }
    //Test Cliente Existe
    @Test
    void obtenerClientePorId_cuandoExiste_DevuelveCliente(){
        //Arrange
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente (id ,"Juan Perez", "jperez@gmail.com", LocalDateTime.now());
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));

        //Act
        ClienteResponse response = clienteService.obtenerClientePorId(id);

        // Assert
        assertEquals(id, response.getId());
        assertEquals("Juan Perez", response.getNombre());
        assertEquals("jperez@gmail.com", response.getEmail());
        assertEquals(cliente.getFechaAlta(), response.getFechaAlta());
    }

    //Test Cliente no exite
    @Test
    void obtenerClientePorId_cuandoNoExiste_lanzaClienteNotFoundException(){
        //Arrange
        UUID id = UUID.randomUUID();
        when(clienteRepository.findById(id)).thenReturn(Optional.empty());

        //Act + Assert
        ClienteNotFoundException ex = assertThrows(ClienteNotFoundException.class,() -> clienteService.obtenerClientePorId(id));
        assertTrue(ex.getMessage().contains(id.toString()));
    }

    //Test flag mask-pii activo
    @Test
    void obtenerClientePorId_cuandoMaskPiiActivo_devuelveDatosEnmascarados(){
        //Arrange
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente(id, "Juan Perez", "jperez@gmail.com", LocalDateTime.now());
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(piiMaskingService.isMaskPiiEnabled(id)).thenReturn(true);
        when(piiMaskingService.maskName("Juan Perez")).thenReturn("J*** P***");
        when(piiMaskingService.maskEmail("jperez@gmail.com")).thenReturn("****@****.com");

        //Act
        ClienteResponse response = clienteService.obtenerClientePorId(id);

        //Assert
        assertEquals("J*** P***", response.getNombre());
        assertEquals("****@****.com", response.getEmail());
    }

    //Test flag mask-pii inactivo
    @Test
    void obtenerClientePorId_cuandoMaskPiiInactivo_devuelveDatosEnClaro(){
        //Arrange
        UUID id = UUID.randomUUID();
        Cliente cliente = new Cliente(id, "Juan Perez", "jperez@gmail.com", LocalDateTime.now());
        when(clienteRepository.findById(id)).thenReturn(Optional.of(cliente));
        when(piiMaskingService.isMaskPiiEnabled(id)).thenReturn(false);

        //Act
        ClienteResponse response = clienteService.obtenerClientePorId(id);

        //Assert
        assertEquals("Juan Perez", response.getNombre());
        assertEquals("jperez@gmail.com", response.getEmail());
    }
}
