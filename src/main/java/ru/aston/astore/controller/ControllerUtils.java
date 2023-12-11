package ru.aston.astore.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class ControllerUtils {
    public static Optional<String[]> getNamesIfPresent(
            HttpServletRequest req,
            HttpServletResponse resp) throws IOException {
        String[] res = new String[2];
        res[0] = Objects.requireNonNullElse(req.getParameter("firstName"), "");
        res[1] = Objects.requireNonNullElse(req.getParameter("lastName"), "");

        if (res[0].isBlank() && res[1].isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query is missing required parameters.");
            return Optional.empty();
        }
        return Optional.of(res);
    }
}
