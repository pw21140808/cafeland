package com.cafeland.api.resource;

import com.cafeland.api.model.OrderRequest;
import com.cafeland.api.model.OrderResponse;
import com.cafeland.api.service.OrderService;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;

    @POST
    public Response postOrder(@Valid OrderRequest request) {
        try {
            if (request == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"mensaje\":\"El cuerpo de la petición está vacío\"}")
                        .build();
            }

            OrderResponse result = orderService.crearPedido(request);

            return Response.status(Response.Status.CREATED)
                    .entity(result)
                    .build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\":\"Error de datos: " + e.getMessage() + "\"}")
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensaje\":\"Error interno: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/historial")
    public Response getOrders() {
        try {
            List<OrderResponse> orders = orderService.listarTodos();

            if (orders == null || orders.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"mensaje\":\"No hay pedidos registrados\"}")
                        .build();
            }

            return Response.ok(orders).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensaje\":\"Error al obtener pedidos: " + e.getMessage() + "\"}")
                    .build();
        }
    }

    @GET
    @Path("/{orderId}")
    public Response getOrderById(@PathParam("orderId") Integer orderId) {
        try {
            if (orderId == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"mensaje\":\"El ID no puede ser nulo\"}")
                        .build();
            }

            OrderResponse found = orderService.listarTodos().stream()
                    .filter(o -> o.getOrderId().equals(orderId))
                    .findFirst()
                    .orElse(null);

            if (found == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"mensaje\":\"No existe el pedido con ID: " + orderId + "\"}")
                        .build();
            }

            return Response.ok(found).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensaje\":\"Error al buscar pedido: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}