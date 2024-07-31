package com.account_transactions_service.service;

import com.account_transactions_service.entities.Cuenta;
import com.account_transactions_service.repository.ICuentaRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
public class CuentaServiceImplTest {
    @Autowired
    private CuentaServiceImpl cuentaService;

    @Autowired
    private RestTemplate restTemplate;

    @MockBean
    private ICuentaRepository cuentaRepository;


    @Test
    public void whenCreateCuentaWithInvalidClient_ThenThrowException() {
        MockRestServiceServer thatMockServer = MockRestServiceServer.createServer(restTemplate);
        thatMockServer.expect(requestTo("http://localhost:8080/api/clientes/cuenta/12345"))
                .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

        assertThrows(RuntimeException.class, ()-> cuentaService.createCuenta("12345", new Cuenta()));
    }
}
