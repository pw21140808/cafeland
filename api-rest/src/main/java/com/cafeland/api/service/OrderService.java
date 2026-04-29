package com.cafeland.api.service;

import com.cafeland.api.model.OrderRequest;
import com.cafeland.api.model.OrderResponse;
import com.cafeland.api.model.ItemPedido;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class OrderService {

    private List<OrderResponse> historialPedidos = new ArrayList<>();

    public OrderResponse crearPedido(OrderRequest request) {
        if (request == null || request.getProducts() == null) {
            return null;
        }

        double totalCalculado = calcularTotal(request.getProducts());

        OrderResponse response = new OrderResponse();
        
        response.setOrderId((int) (Math.random() * 10000));
        response.setTotal(BigDecimal.valueOf(totalCalculado));
        response.setMessage("Venta exitosa para " + request.getCustomer()); 
        response.setDate(OffsetDateTime.now()); 

        historialPedidos.add(response);
        return response;
    }

    private double calcularTotal(List<ItemPedido> items) {
        double total = 0.0;
        for (ItemPedido item : items) {
            double precio = obtenerPrecioSimulado(item.getProductId());
            total += precio * item.getCantidad();
        }
        return total;
    }

    private double obtenerPrecioSimulado(String productId) {
        return switch (productId) {
            case "1" -> 35.0; 
            case "2" -> 25.0;
            default -> 15.0;
        };
    }

    public List<OrderResponse> listarTodos() {
        return historialPedidos;
    }
}