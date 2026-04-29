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
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        OrderResponse result = orderService.crearPedido(request);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    @GET
    @Path("/historial") 
    public List<OrderResponse> getOrders() {
        return orderService.listarTodos();
    }

    @GET
    @Path("/{orderId}") 
    public Response getOrderById(@PathParam("orderId") Integer orderId) {
        OrderResponse found = orderService.listarTodos().stream()
            .filter(o -> o.getOrderId().equals(orderId))
            .findFirst()
            .orElse(null);

        if (found == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(found).build();
    }
}