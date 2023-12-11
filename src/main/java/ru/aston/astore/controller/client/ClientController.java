package ru.aston.astore.controller.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.aston.astore.controller.ControllerUtils;
import ru.aston.astore.dto.client.ClientDto;
import ru.aston.astore.dto.client.NewClientDto;
import ru.aston.astore.mapper.client.ClientMapper;
import ru.aston.astore.repository.client.impl.JDBCClientRepository;
import ru.aston.astore.service.client.ClientService;
import ru.aston.astore.service.client.impl.ClientServiceImpl;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/client")
public class ClientController extends HttpServlet {
    private final ClientService service;
    private final ObjectMapper mapper;

    public ClientController() {
        this.service = new ClientServiceImpl(new JDBCClientRepository(), Mappers.getMapper(ClientMapper.class));
        this.mapper = new ObjectMapper();
    }

    public ClientController(ClientService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        NewClientDto dto;
        try {
            dto = mapper.readValue(
                    req.getReader().lines().collect(Collectors.joining()), NewClientDto.class);

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body.");
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(service.addClient(dto)));
        resp.getWriter().flush();
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String id = req.getParameter("id");
        Object ans;

        if (id != null) {
            Optional<UUID> uuid = tryParseId(id);
            if (uuid.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID.");
                return;
            }

            Optional<ClientDto> dto = service.findById(uuid.get());
            if (dto.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested client not found.");
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
        ClientDto dto;
        try {
            dto = mapper.readValue(
                    req.getReader().lines().collect(Collectors.joining()), ClientDto.class);

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body.");
            return;
        }
        if (dto.getId() == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Client ID must not be null.");
            return;
        }
        if (!service.updateClient(dto)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested client not found.");
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

        Optional<UUID> uuid = tryParseId(id);
        if (uuid.isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid client ID.");
            return;
        }

        if (!service.removeClient(uuid.get())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested client not found.");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private Optional<UUID> tryParseId(String id) {
        UUID uuid;
        try {
            uuid = UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        return Optional.of(uuid);
    }
}
