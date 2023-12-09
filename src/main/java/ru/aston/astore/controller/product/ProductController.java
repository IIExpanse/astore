package ru.aston.astore.controller.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.aston.astore.dto.product.NewProductDto;
import ru.aston.astore.dto.product.ProductDto;
import ru.aston.astore.mapper.product.ProductMapper;
import ru.aston.astore.repository.product.impl.JDBCProductRepository;
import ru.aston.astore.service.product.ProductService;
import ru.aston.astore.service.product.impl.ProductServiceImpl;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/product")
public class ProductController extends HttpServlet {
    private final ProductService service;
    private final ObjectMapper mapper;

    public ProductController() {
        this.service = new ProductServiceImpl(new JDBCProductRepository(), Mappers.getMapper(ProductMapper.class));
        this.mapper = new ObjectMapper();
    }

    public ProductController(ProductService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        NewProductDto dto;
        try {
            dto = mapper.readValue(
                    req.getReader().lines().collect(Collectors.joining()), NewProductDto.class);

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body.");
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(service.addProduct(dto)));
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
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID.");
                return;
            }

            Optional<ProductDto> dto = service.findById(uuid.get());
            if (dto.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested product not found.");
                return;
            }
            ans = dto.get();

        } else {
            String title = Objects.requireNonNullElse(req.getParameter("title"), "");
            if (title.isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query is missing required parameters.");
                return;
            }
            ans = service.findByTitle(title);
        }
        resp.getWriter().write(mapper.writeValueAsString(ans));
        resp.getWriter().flush();
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ProductDto dto;
        try {
            dto = mapper.readValue(
                    req.getReader().lines().collect(Collectors.joining()), ProductDto.class);

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body.");
            return;
        }
        if (dto.getId() == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Product ID must not be null.");
            return;
        }
        if (!service.updateProduct(dto)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested product not found.");
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID.");
            return;
        }

        if (!service.removeProduct(uuid.get())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested product not found.");
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
