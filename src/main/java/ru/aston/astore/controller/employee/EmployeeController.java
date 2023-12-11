package ru.aston.astore.controller.employee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.aston.astore.controller.ControllerUtils;
import ru.aston.astore.dto.employee.EmployeeDto;
import ru.aston.astore.mapper.employee.EmployeeMapper;
import ru.aston.astore.repository.employee.impl.JDBCEmployeeRepository;
import ru.aston.astore.service.employee.EmployeeService;
import ru.aston.astore.service.employee.impl.EmployeeServiceImpl;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/employee")
public class EmployeeController extends HttpServlet {
    private final EmployeeService service;
    private final ObjectMapper mapper;

    public EmployeeController() {
        this.service = new EmployeeServiceImpl(new JDBCEmployeeRepository(), Mappers.getMapper(EmployeeMapper.class));
        this.mapper = new ObjectMapper();
    }

    public EmployeeController(EmployeeService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EmployeeDto dto;
        try {
            dto = mapper.readValue(
                    req.getReader().lines().collect(Collectors.joining()), EmployeeDto.class);

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body.");
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(service.addEmployee(dto)));
        resp.getWriter().flush();
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        Object ans;

        if (id != null) {
            Optional<UUID> uuid = ControllerUtils.tryParseId(id);
            if (uuid.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID.");
                return;
            }

            Optional<EmployeeDto> dto = service.findById(uuid.get());
            if (dto.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested employee not found.");
                return;
            }
            ans = dto.get();

        } else {
            Optional<String[]> nameOptional = ControllerUtils.getNamesIfPresent(req, resp);
            if (nameOptional.isEmpty()) {
                return;
            }
            ans = service.findByName(nameOptional.get()[0], nameOptional.get()[1]);
        }
        resp.getWriter().write(mapper.writeValueAsString(ans));
        resp.getWriter().flush();
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EmployeeDto dto;
        try {
            dto = mapper.readValue(
                    req.getReader().lines().collect(Collectors.joining()), EmployeeDto.class);

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body.");
            return;
        }
        if (dto.getId() == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Employee ID must not be null.");
            return;
        }
        if (!service.updateEmployee(dto)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested employee not found.");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        if (id == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query is missing required parameters.");
            return;
        }

        Optional<UUID> uuid = ControllerUtils.tryParseId(id);
        if (uuid.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID.");
            return;
        }

        if (!service.removeEmployee(uuid.get())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested employee not found.");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
