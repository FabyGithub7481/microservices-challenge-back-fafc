package com.client_person_service.controllers;

import com.client_person_service.dtos.ClienteDTO;
import com.client_person_service.entities.Cliente;
import com.client_person_service.services.ClienteServiceImpl;
import com.client_person_service.util.IdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
public class ClienteControllerTest {



    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteServiceImpl clienteService;

    @Test
    public void createClienteTest() throws Exception {

        ClienteDTO testClienteDTO = new ClienteDTO();
        testClienteDTO.setClienteId(1L);
        testClienteDTO.setContrasena("testPassword");
        testClienteDTO.setEstado(true);

        when(clienteService.createCliente(any())).thenReturn(testClienteDTO);


        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"clienteId\": 1, \"contrasena\": \"testPassword\", \"estado\": true}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(1L))
                .andExpect(jsonPath("$.contrasena").value("testPassword"))
                .andExpect(jsonPath("$.estado").value(true));
    }
}