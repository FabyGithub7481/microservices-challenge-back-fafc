package com.account_transactions_service.service;

import com.account_transactions_service.config.ApiConfig;
import com.account_transactions_service.dtos.CuentaDTO;
import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.exceptions.CuentaNoEncontradaException;
import com.account_transactions_service.repositories.ICuentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class CuentaServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ICuentaRepository cuentaRepository;

    @Mock
    private ApiConfig apiConfig;

    @InjectMocks
    private CuentaServiceImpl cuentaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


String UrlTestClienteCedula="http://api/clientes/cuenta/";

    @Test
    void createCuenta_ClienteFound_CuentaCreatedSuccessfully() {
        String identificacion = "123456789";
        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setNumeroCuenta("987654321");

        when(apiConfig.getApiClientCuentaUrl()).thenReturn(UrlTestClienteCedula);
        when(restTemplate.getForObject(anyString(), eq(Long.class))).thenReturn(1L);
        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.empty());
        when(cuentaRepository.save(any(Cuenta.class))).thenReturn(new Cuenta());

        CuentaDTO result = cuentaService.createCuenta(identificacion, cuentaDTO);

        assertNotNull(result);
        verify(cuentaRepository, times(1)).save(any(Cuenta.class));
    }

    @Test
    void createCuenta_CuentaAlreadyExists_ThrowsRuntimeException() {
        String identificacion = "123456789";
        CuentaDTO cuentaDTO = new CuentaDTO();
        cuentaDTO.setNumeroCuenta("7899654123");

        when(apiConfig.getApiClientCuentaUrl()).thenReturn(UrlTestClienteCedula);
        when(restTemplate.getForObject(anyString(), eq(Long.class))).thenReturn(1L);
        when(cuentaRepository.findByNumeroCuenta(anyString())).thenReturn(Optional.of(new Cuenta()));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cuentaService.createCuenta(identificacion, cuentaDTO);
        });

        assertEquals("La cuenta con el número 7899654123 ya existe.", thrown.getMessage());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void createCuenta_ClienteNotFound_ThrowsRuntimeException() {
        String identificacion = "147852369";
        CuentaDTO cuentaDTO = new CuentaDTO();

        when(apiConfig.getApiClientCuentaUrl()).thenReturn(UrlTestClienteCedula);
        when(restTemplate.getForObject(anyString(), eq(Long.class))).thenReturn(null);
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cuentaService.createCuenta(identificacion, cuentaDTO);
        });

        assertEquals("Cliente no encontrado con la identificación: 147852369", thrown.getMessage());
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void createCuenta_ExternalServiceNotFound_ThrowsRuntimeException() {
        String identificacion = "852369741";
        CuentaDTO cuentaDTO = new CuentaDTO();

        when(apiConfig.getApiClientCuentaUrl()).thenReturn(UrlTestClienteCedula);
        when(restTemplate.getForObject(anyString(), eq(Long.class)))
            .thenThrow(HttpClientErrorException.NotFound.class);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cuentaService.createCuenta(identificacion, cuentaDTO);
        });

        assertTrue(thrown.getMessage().contains("Cliente no encontrado en el servicio externo"));
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void createCuenta_ServerError_ThrowsRuntimeException() {

        String identificacion = "123456789";
        CuentaDTO cuentaDTO = new CuentaDTO();

        when(apiConfig.getApiClientCuentaUrl()).thenReturn(UrlTestClienteCedula);
        when(restTemplate.getForObject(anyString(), eq(Long.class)))
            .thenThrow(HttpServerErrorException.class);
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cuentaService.createCuenta(identificacion, cuentaDTO);
        });

        assertTrue(thrown.getMessage().contains("Error del servidor al obtener datos del cliente"));
        verify(cuentaRepository, never()).save(any(Cuenta.class));
    }

    @Test
    void testGetCuentaById_ExistentId() {
        String id = "0147852369";
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta(id);

        when(cuentaRepository.findByNumeroCuenta(id)).thenReturn(Optional.of(cuenta));
        Optional<CuentaDTO> result = cuentaService.getCuentaById(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().getNumeroCuenta());
        verify(cuentaRepository, times(2)).findByNumeroCuenta(id);
    }

    @Test
    void testGetCuentaById_NonExistentId() {
        String id = "12345";

        when(cuentaRepository.findByNumeroCuenta(id)).thenReturn(Optional.empty());
        CuentaNoEncontradaException exception = assertThrows(CuentaNoEncontradaException.class, () -> {
            cuentaService.getCuentaById(id);        });

        assertEquals("Cuenta no encontrada con ID: " + id, exception.getMessage());
        verify(cuentaRepository, times(1)).findByNumeroCuenta(id);
    }
}
