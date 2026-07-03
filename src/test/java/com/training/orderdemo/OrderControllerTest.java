package com.training.orderdemo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateOrderWhenJsonIsValid() throws Exception {
        mockMvc.perform(post("/api/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":1003}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.orderId").value(1003))
                .andExpect(jsonPath("$.data.status").value("CREATED"));
    }

    @Test
    void shouldReturnBadRequestWhenJsonIsMalformed() throws Exception {
        mockMvc.perform(post("/api/order/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"orde"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/order/create"));
    }

    @Test
    void shouldReadCreatedOrder() throws Exception {
        mockMvc.perform(post("/api/order/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":2001}"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/order/2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").value(2001));
    }
}
