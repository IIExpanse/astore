package ru.aston.astore.controller.order;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;
import ru.aston.astore.controller.ControllerUtils;
import ru.aston.astore.dto.order.NewOrderDto;
import ru.aston.astore.dto.order.OrderDto;
import ru.aston.astore.entity.order.OrderStatus;
import ru.aston.astore.mapper.order.OrderMapper;
import ru.aston.astore.repository.order.impl.JDBCOrderRepository;
import ru.aston.astore.service.order.OrderService;
import ru.aston.astore.service.order.impl.OrderServiceImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@WebServlet("/order/*")
public class OrderController extends HttpServlet {
    private final OrderService service;
    private final ObjectMapper mapper;

    public OrderController() {
        this.service = new OrderServiceImpl(new JDBCOrderRepository(), Mappers.getMapper(OrderMapper.class));
        this.mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    public OrderController(OrderService service) {
        this.service = service;
        this.mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        NewOrderDto dto;
        try {
            dto = mapper.readValue(
                    req.getReader().lines().collect(Collectors.joining()), NewOrderDto.class);

        } catch (JsonProcessingException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body. ");
            return;
        }
        if (dto.getClient_id() == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Client ID mut not be null.");
            return;
        }
        if (dto.getProducts() == null || dto.getProducts().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order must contain at least 1 product.");
            return;
        }
        resp.getWriter().write(mapper.writeValueAsString(service.addOrder(dto)));
        resp.getWriter().flush();
        resp.setStatus(HttpServletResponse.SC_CREATED);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            String id = req.getParameter("id");
            Object ans;

            if (id != null) {
                Optional<UUID> uuid = ControllerUtils.tryParseId(id);
                if (uuid.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID.");
                    return;
                }

                Optional<OrderDto> dto = service.findById(uuid.get());
                if (dto.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested order not found.");
                    return;
                }
                ans = dto.get();

            } else {
                String s = req.getParameter("status");
                Optional<OrderStatus> status = Optional.empty();

                if (!s.isBlank()) {
                    status = tryParseStatus(s);
                }
                if (status.isEmpty()) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query is missing required parameters.");
                    return;
                }
                ans = service.findByStatus(status.get());
            }
            resp.getWriter().write(mapper.writeValueAsString(ans));
            resp.getWriter().flush();
            resp.setStatus(HttpServletResponse.SC_OK);

        } else if (pathInfo.equals("/products")) {
            String orderId = req.getParameter("orderId");

            if (orderId.isBlank()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query is missing required parameters.");
                return;
            }
            UUID orderUuid;

            Optional<UUID> orderUuidOptional = ControllerUtils.tryParseId(orderId);
            if (orderUuidOptional.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query is missing required parameters.");
                return;
            }
            orderUuid = orderUuidOptional.get();

            resp.getWriter().write(mapper.writeValueAsString(service.getProductIdsByOrder(orderUuid)));
            resp.getWriter().flush();
            resp.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null) {
            OrderDto dto;
            try {
                dto = mapper.readValue(
                        req.getReader().lines().collect(Collectors.joining()), OrderDto.class);

            } catch (JsonProcessingException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body.");
                return;
            }
            if (dto.getId() == null) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Order ID must not be null.");
                return;
            }
            if (!service.updateOrder(dto)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested order not found.");
                return;
            }
            resp.setStatus(HttpServletResponse.SC_OK);

        } else if (pathInfo.equals("/products")) {
            String orderId = req.getParameter("orderId");
            String[] productsIds = req.getParameterValues("productsIds");

            if (orderId.isBlank() || productsIds.length == 0) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query is missing required parameters.");
                return;
            }
            UUID orderUuid;
            Collection<UUID> products;

            Optional<UUID> orderUuidOptional = ControllerUtils.tryParseId(orderId);
            if (orderUuidOptional.isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query is missing required parameters.");
                return;
            }
            orderUuid = orderUuidOptional.get();

            try {
                products = Arrays.stream(mapper.convertValue(productsIds, String[].class))
                        .map(UUID::fromString)
                        .collect(Collectors.toList());

            } catch (IllegalArgumentException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body.");
                return;
            }

            if (service.addProductsIntoOrder(products, orderUuid)) {
                resp.setStatus(HttpServletResponse.SC_OK);

            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Corresponding order or products not found.");
            }
        }
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid order ID.");
            return;
        }

        if (!service.removeOrder(uuid.get())) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Requested order not found.");
            return;
        }
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    private Optional<OrderStatus> tryParseStatus(String s) {
        OrderStatus status;
        try {
            status = OrderStatus.valueOf(s);

        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
        return Optional.of(status);
    }
}
