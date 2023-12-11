package ru.aston.astore.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import ru.aston.astore.dto.EmployeeDto;
import ru.aston.astore.service.EmployeeService;
import ru.aston.astore.util.ObjectsFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EmployeeControllerTest {
    private HttpServletRequest request;
    private HttpServletResponse response;
    private EmployeeController controller;
    private EmployeeService service;
    private ObjectMapper mapper;

    @BeforeEach
    void setMocks() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        service = Mockito.mock(EmployeeService.class);
        controller = new EmployeeController(service);
        mapper = new ObjectMapper();
    }

    @Test
    void addNewEmployee() throws IOException {
        String json = ObjectsFactory.getEmployeeJson(mapper);
        EmployeeDto dto = ObjectsFactory.getEmployeeDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getReader()).thenReturn(ObjectsFactory.getReader(json));
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.add(ArgumentMatchers.any(EmployeeDto.class))).thenReturn(dto);
        controller.doPost(request, response);

        Mockito.verify(response).setStatus(HttpServletResponse.SC_CREATED);
        Mockito.verify(service).add(mapper.readValue(json, EmployeeDto.class));
        assertEquals(mapper.writeValueAsString(dto), writer.toString());
    }

    @Test
    void getEmployeeById() throws IOException {
        EmployeeDto dto = ObjectsFactory.getEmployeeDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getParameter("id")).thenReturn(dto.getId().toString());
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.findById(ArgumentMatchers.any(UUID.class))).thenReturn(Optional.of(dto));

        controller.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(mapper.writeValueAsString(dto), writer.toString());
    }

    @Test
    void getEmployeeByName() throws IOException {
        EmployeeDto dto = ObjectsFactory.getEmployeeDto();
        StringWriter writer = new StringWriter();

        Mockito.when(request.getParameter("id")).thenReturn(null);
        Mockito.when(request.getParameter("firstName")).thenReturn(dto.getFirstName());
        Mockito.when(request.getParameter("lastName")).thenReturn(dto.getLastName());
        Mockito.when(response.getWriter()).thenReturn(new PrintWriter(writer));
        Mockito.when(service.findByName(dto.getFirstName(), dto.getLastName())).thenReturn(List.of(dto));

        controller.doGet(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(mapper.writeValueAsString(List.of(dto)), writer.toString());
    }

    @Test
    void updateEmployee() throws IOException {
        Mockito.when(request.getReader()).thenReturn(ObjectsFactory.getReader(ObjectsFactory.getEmployeeJson(mapper)));
        Mockito.when(service.update(ArgumentMatchers.any(EmployeeDto.class))).thenReturn(true);
        controller.doPut(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    void removeEmployee() throws IOException {
        EmployeeDto dto = ObjectsFactory.getEmployeeDto();
        Mockito.when(request.getParameter("id")).thenReturn(dto.getId().toString());
        Mockito.when(service.remove(ArgumentMatchers.any(UUID.class))).thenReturn(true);
        controller.doDelete(request, response);
        Mockito.verify(response).setStatus(HttpServletResponse.SC_OK);
    }
}