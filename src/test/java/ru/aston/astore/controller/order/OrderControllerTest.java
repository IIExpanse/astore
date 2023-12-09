package ru.aston.astore.controller.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.order.NewOrderDto;
import ru.aston.astore.dto.order.OrderDto;
import ru.aston.astore.entity.order.OrderStatus;
import ru.aston.astore.service.order.OrderService;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderControllerTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private OrderController controller;
    private OrderService service;
    private ObjectMapper mapper;

    @BeforeEach
    void setMocks() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        service = Mockito.mock(OrderService.class);
        controller = new OrderController(service);
        mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Test
    void addNewOrder() throws IOException {
        String json = getNewOrderJson();
        OrderDto dto = getOrderDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getReader()).thenReturn(getReader(json));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.addOrder(ArgumentMatchers.any(NewOrderDto.class))).thenReturn(dto);
        controller.doPost(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(service).addOrder(mapper.readValue(json, NewOrderDto.class));
        assertEquals(mapper.writeValueAsString(dto), writer.toString());
    }

    @Test
    void getOrderById() throws IOException {
        OrderDto dto = getOrderDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getParameter("id")).thenReturn(dto.getId().toString());
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(dto));

        controller.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(mapper.writeValueAsString(dto), writer.toString());
    }

    @Test
    void getOrderByStatus() throws IOException {
        OrderDto dto = getOrderDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getParameter("status")).thenReturn(dto.getStatus().toString());
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.findByStatus(ArgumentMatchers.any(OrderStatus.class))).thenReturn(List.of(dto));

        controller.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(mapper.writeValueAsString(List.of(dto)), writer.toString());
    }

    @Test
    void getProductIdsByOrder() throws IOException {
        OrderDto dto = getOrderDto();
        StringWriter writer = new StringWriter();
        List<UUID> list = List.of(UUID.randomUUID());

        Mockito.when(request.getParameter("orderId")).thenReturn(dto.getId().toString());
        Mockito.when(request.getPathInfo()).thenReturn("/products");
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.getProductIdsByOrder(ArgumentMatchers.any(UUID.class))).thenReturn(list);

        controller.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(mapper.writeValueAsString(list), writer.toString());
    }

    @Test
    void updateOrder() throws IOException {
        Mockito.when(request.getPathInfo()).thenReturn(null);
        Mockito.when(request.getReader()).thenReturn(getReader(mapper.writeValueAsString(getOrderDto())));
        Mockito.when(service.updateOrder(ArgumentMatchers.any(OrderDto.class))).thenReturn(true);
        controller.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void addProductsIntoOrder() throws IOException {
        OrderDto dto = getOrderDto();
        String[] productIds = {UUID.randomUUID().toString()};

        Mockito.when(request.getPathInfo()).thenReturn("/products");
        Mockito.when(request.getParameter("orderId")).thenReturn(dto.getId().toString());
        Mockito.when(request.getParameterValues("productsIds")).thenReturn(productIds);
        Mockito.when(service.addProductsIntoOrder(ArgumentMatchers.anyList(), ArgumentMatchers.any(UUID.class)))
                .thenReturn(true);
        controller.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void removeOrder() throws IOException {
        OrderDto dto = getOrderDto();
        Mockito.when(request.getParameter("id")).thenReturn(dto.getId().toString());
        Mockito.when(service.removeOrder(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        controller.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    private BufferedReader getReader(String s) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
    }

    private OrderDto getOrderDto() {
        return new OrderDto(
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                List.of(UUID.randomUUID())
        );
    }

    private String getNewOrderJson() throws JsonProcessingException {
        NewOrderDto dto = new NewOrderDto();
        dto.setClient_id(UUID.randomUUID());
        dto.setStatus(OrderStatus.PENDING);
        dto.setProducts(List.of(UUID.randomUUID()));

        return mapper.writeValueAsString(dto);
    }
}