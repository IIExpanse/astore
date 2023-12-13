package ru.aston.astore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.ProductDto;
import ru.aston.astore.service.ProductService;
import ru.aston.astore.util.ObjectsFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductControllerTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private ProductController controller;
    private ProductService service;
    private ObjectMapper mapper;

    @BeforeEach
    void setMocks() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        service = Mockito.mock(ProductService.class);
        controller = new ProductController(service);
        mapper = new ObjectMapper();
    }

    @Test
    void addNewProduct() throws IOException {
        String json = ObjectsFactory.getProductJson(mapper);
        ProductDto dto = ObjectsFactory.getProductDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getReader()).thenReturn(ObjectsFactory.getReader(json));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.add(ArgumentMatchers.any(ProductDto.class))).thenReturn(dto);
        controller.doPost(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(service).add(mapper.readValue(json, ProductDto.class));
        assertEquals(mapper.writeValueAsString(dto), writer.toString());
    }

    @Test
    void getProductById() throws IOException {
        ProductDto dto = ObjectsFactory.getProductDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getParameter("id")).thenReturn(dto.getId().toString());
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(dto));

        controller.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(mapper.writeValueAsString(dto), writer.toString());
    }

    @Test
    void getProductByTitle() throws IOException {
        ProductDto dto = ObjectsFactory.getProductDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getParameter("id")).thenReturn(null);
        Mockito.when(request.getParameter("title")).thenReturn(dto.getTitle());
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.findByTitle(dto.getTitle())).thenReturn(List.of(dto));

        controller.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(mapper.writeValueAsString(List.of(dto)), writer.toString());
    }

    @Test
    void updateProduct() throws IOException {
        Mockito.when(request.getReader()).thenReturn(ObjectsFactory.getReader(ObjectsFactory.getProductJson(mapper)));
        Mockito.when(service.update(ArgumentMatchers.any(ProductDto.class))).thenReturn(true);
        controller.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void removeProduct() throws IOException {
        ProductDto dto = ObjectsFactory.getProductDto();
        Mockito.when(request.getParameter("id")).thenReturn(dto.getId().toString());
        Mockito.when(service.remove(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        controller.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}