package ru.aston.astore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.OrderDto;
import ru.aston.astore.entity.OrderStatus;
import ru.aston.astore.service.OrderService;
import ru.aston.astore.util.ObjectsFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
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
        String json = ObjectsFactory.getOrderJson(mapper);
        OrderDto dto = ObjectsFactory.getOrderDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getReader()).thenReturn(ObjectsFactory.getReader(json));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.add(ArgumentMatchers.any(OrderDto.class))).thenReturn(dto);
        controller.doPost(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(service).add(mapper.readValue(json, OrderDto.class));
        assertEquals(mapper.writeValueAsString(dto), writer.toString());
    }

    @Test
    void getOrderById() throws IOException {
        OrderDto dto = ObjectsFactory.getOrderDto();
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
        OrderDto dto = ObjectsFactory.getOrderDto();
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
        OrderDto dto = ObjectsFactory.getOrderDto();
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
        Mockito.when(request.getReader()).thenReturn(ObjectsFactory.getReader(ObjectsFactory.getOrderJson(mapper)));
        Mockito.when(service.update(ArgumentMatchers.any(OrderDto.class))).thenReturn(true);
        controller.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void addProductsIntoOrder() throws IOException {
        OrderDto dto = ObjectsFactory.getOrderDto();
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
        OrderDto dto = ObjectsFactory.getOrderDto();
        Mockito.when(request.getParameter("id")).thenReturn(dto.getId().toString());
        Mockito.when(service.remove(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        controller.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}