package ru.aston.astore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.aston.astore.dto.ClientDto;
import ru.aston.astore.dto.EmployeeDto;
import ru.aston.astore.dto.OrderDto;
import ru.aston.astore.dto.ProductDto;
import ru.aston.astore.entity.Client;
import ru.aston.astore.entity.Employee;
import ru.aston.astore.entity.EmployeeRole;
import ru.aston.astore.entity.Order;
import ru.aston.astore.entity.OrderStatus;
import ru.aston.astore.entity.Product;
import ru.aston.astore.entity.ProductType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ObjectsFactory {
    private static String clientJson;
    private static String employeeJson;
    private static String productJson;
    private static String orderJson;

    private ObjectsFactory() {}

    public static Client getDefaultClient() {
        return Client.builder()
                .id(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .build();
    }

    public static Employee getDefaultEmployee() {
        return Employee.builder()
                .id(UUID.randomUUID())
                .firstName("Jack")
                .lastName("Bond")
                .role(EmployeeRole.MANAGER)
                .build();
    }

    public static Product getDefaultProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .title("Wooden chair")
                .price(9.5f)
                .discount(null)
                .type(ProductType.FURNITURE)
                .build();
    }

    public static Order getDefaultOrder(UUID clientId) {
        return Order.builder()
                .id(UUID.randomUUID())
                .client_id(clientId)
                .status(OrderStatus.PENDING)
                .created(LocalDateTime.now())
                .products(List.of())
                .build();
    }

    public static ClientDto getClientDto() {
        ClientDto dto = new ClientDto();
        dto.setId(UUID.randomUUID());
        dto.setFirstName("John");
        dto.setLastName("Doe");

        return dto;
    }

    public static EmployeeDto getEmployeeDto() {
        EmployeeDto dto = new EmployeeDto();
        dto.setId(UUID.randomUUID());
        dto.setFirstName("John");
        dto.setLastName("Doe");
        dto.setRole(EmployeeRole.MANAGER);
        dto.setAssignedOrders(List.of());

        return dto;
    }

    public static ProductDto getProductDto() {
        ProductDto dto = new ProductDto();
        dto.setId(UUID.randomUUID());
        dto.setTitle("Wooden chair");
        dto.setPrice(14.5f);
        dto.setType(ProductType.FURNITURE);

        return dto;
    }

    public static OrderDto getOrderDto() {
        OrderDto dto = new OrderDto();
        dto.setId(UUID.randomUUID());
        dto.setClient_id(UUID.randomUUID());
        dto.setStatus(OrderStatus.PENDING);
        dto.setProducts(List.of(UUID.randomUUID()));

        return dto;
    }

    public static BufferedReader getReader(String s) {
        return new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes())));
    }

    public static String getClientJson(ObjectMapper mapper) throws JsonProcessingException {
        if (clientJson == null) {
            clientJson = mapper.writeValueAsString(getClientDto());
        }
        return clientJson;
    }

    public static String getEmployeeJson(ObjectMapper mapper) throws JsonProcessingException {
        if (employeeJson == null) {
            employeeJson = mapper.writeValueAsString(getEmployeeDto());
        }
        return employeeJson;
    }

    public static String getProductJson(ObjectMapper mapper) throws JsonProcessingException {
        if (productJson == null) {
            productJson = mapper.writeValueAsString(getProductDto());
        }
        return productJson;
    }

    public static String getOrderJson(ObjectMapper mapper) throws JsonProcessingException {
        if (orderJson == null) {
            orderJson = mapper.writeValueAsString(getOrderDto());
        }
        return orderJson;
    }
}
