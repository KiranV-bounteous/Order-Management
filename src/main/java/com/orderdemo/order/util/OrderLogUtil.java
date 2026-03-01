package com.orderdemo.order.util;

import com.orderdemo.order.entity.Order;
import com.orderdemo.order.exception.OrderLogException;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for logging orders to file using Java I/O
 */
@Component
public class OrderLogUtil {
    private static final String LOG_FILE_PATH = "logs/order_logs.txt";
    private static final String CSV_FILE_PATH = "logs/order_audit.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String CSV_HEADER = "OrderId,CustomerId,Symbol,OrderType,Quantity,Price,TotalAmount,Status,CreatedAt,UpdatedAt\n";

    /**
     * Initialize log files
     */
    public void initializeLogs() {
        try {
            Files.createDirectories(Paths.get("logs"));

            File logFile = new File(LOG_FILE_PATH);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }

            File csvFile = new File(CSV_FILE_PATH);
            if (!csvFile.exists()) {
                csvFile.createNewFile();
                Files.write(Paths.get(CSV_FILE_PATH), CSV_HEADER.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            throw new OrderLogException("Failed to initialize log files", e);
        }
    }

    /**
     * Log order to file
     */
    public void logOrder(Order order) {
        try {
            String logEntry = String.format(
                    "[%s] Order ID: %s, Customer: %s, Symbol: %s, Type: %s, Quantity: %s, Price: %s, Status: %s\n",
                    LocalDateTime.now().format(DATE_FORMATTER),
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getSymbol(),
                    order.getOrderType(),
                    order.getQuantity(),
                    order.getPrice(),
                    order.getStatus()
            );

            try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
                writer.write(logEntry);
                writer.flush();
            }
        } catch (IOException e) {
            throw new OrderLogException("Failed to log order to file", e);
        }
    }

    /**
     * Log order to CSV file for audit
     */
    public void logOrderToCSV(Order order) {
        try {
            String csvEntry = String.format(
                    "%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                    order.getOrderId(),
                    order.getCustomerId(),
                    order.getSymbol(),
                    order.getOrderType(),
                    order.getQuantity(),
                    order.getPrice(),
                    order.getTotalAmount(),
                    order.getStatus(),
                    order.getCreatedAt().format(DATE_FORMATTER),
                    order.getUpdatedAt().format(DATE_FORMATTER)
            );

            try (FileWriter writer = new FileWriter(CSV_FILE_PATH, true)) {
                writer.write(csvEntry);
                writer.flush();
            }
        } catch (IOException e) {
            throw new OrderLogException("Failed to log order to CSV file", e);
        }
    }

    /**
     * Log order update
     */
    public void logOrderUpdate(Order order, String action) {
        try {
            String logEntry = String.format(
                    "[%s] ACTION: %s | Order ID: %s, Status: %s, Notes: %s\n",
                    LocalDateTime.now().format(DATE_FORMATTER),
                    action,
                    order.getOrderId(),
                    order.getStatus(),
                    order.getNotes() != null ? order.getNotes() : "N/A"
            );

            try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
                writer.write(logEntry);
                writer.flush();
            }
        } catch (IOException e) {
            throw new OrderLogException("Failed to log order update", e);
        }
    }

    /**
     * Log error
     */
    public void logError(String orderId, String error) {
        try {
            String logEntry = String.format(
                    "[%s] ERROR - Order ID: %s, Message: %s\n",
                    LocalDateTime.now().format(DATE_FORMATTER),
                    orderId,
                    error
            );

            try (FileWriter writer = new FileWriter(LOG_FILE_PATH, true)) {
                writer.write(logEntry);
                writer.flush();
            }
        } catch (IOException e) {
            throw new OrderLogException("Failed to log error", e);
        }
    }

    /**
     * Get log file content
     */
    public String getLogFileContent() {
        try {
            return new String(Files.readAllBytes(Paths.get(LOG_FILE_PATH)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new OrderLogException("Failed to read log file", e);
        }
    }

    /**
     * Clear log files
     */
    public void clearLogs() {
        try {
            Files.write(Paths.get(LOG_FILE_PATH), new byte[0]);
            Files.write(Paths.get(CSV_FILE_PATH), CSV_HEADER.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new OrderLogException("Failed to clear logs", e);
        }
    }
}

