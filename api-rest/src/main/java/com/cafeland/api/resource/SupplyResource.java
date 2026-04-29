package com.cafeland.api.resource;

import java.time.OffsetDateTime;
import java.util.List;
import com.cafeland.api.model.*;
import com.cafeland.api.service.SupplyService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SupplyResource {

    @Inject
    SupplyService supplyService;

    @GET
    @Path("/supplies")
    public Response getSupplies() {
        List<Supply> supplies = supplyService.getAllSupplies();

        if (supplies == null || supplies.isEmpty()) {
            return buildError(Response.Status.OK, "EMPTY_INVENTORY", 
                              "La bodega está vacía. No hay insumos registrados.", "/supplies");
        }

        return Response.ok(supplies).build();
    }

    @POST
    @Path("/supplies")
    public Response createSupply(@Valid SupplyInput input) {
        if (input == null) {
            return buildError(Response.Status.BAD_REQUEST, "EMPTY_BODY", 
                              "El cuerpo de la petición no puede estar vacío.", "/supplies");
        }

        if (input.getCurrentStock() != null && input.getCurrentStock() < 0) {
            return buildError(Response.Status.BAD_REQUEST, "INVALID_STOCK", 
                              "Error: El stock inicial no puede ser un valor negativo.", "/supplies");
        }

        try {
            Supply created = supplyService.createSupply(input);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (Exception e) {
            return buildError(Response.Status.INTERNAL_SERVER_ERROR, "SERVER_ERROR", 
                              "Ocurrió un error inesperado al registrar el insumo.", "/supplies");
        }
    }

    @PATCH
    @Path("/supplies/{supplyId}")
    public Response updateStock(@PathParam("supplyId") String supplyId, @Valid SuppliesSupplyIdPatchRequest request) {
        
        if (request == null || request.getCantidadVariacion() == null) {
            return buildError(Response.Status.BAD_REQUEST, "INVALID_PATCH", 
                              "Debe proporcionar la cantidad de variación para actualizar.", "/supplies/" + supplyId);
        }

        Supply updated = supplyService.updateStock(supplyId, request.getCantidadVariacion().intValue());

        if (updated == null) {
            return buildError(Response.Status.NOT_FOUND, "SUPPLY_NOT_FOUND", 
                              "No se encontró el insumo con ID: " + supplyId, "/supplies/" + supplyId);
        }

        return Response.ok(updated).build();
    }

    @GET
    @Path("/suppliers")
    public Response getSuppliers() {
        List<Supplier> suppliers = supplyService.getAllSuppliers();

        if (suppliers == null || suppliers.isEmpty()) {
            return buildError(Response.Status.OK, "NO_SUPPLIERS", 
                              "No hay proveedores dados de alta en el sistema.", "/suppliers");
        }

        return Response.ok(suppliers).build();
    }

    @POST
    @Path("/suppliers")
    public Response createSupplier(@Valid Supplier supplier) {
        if (supplier == null || supplier.getCompanyName() == null) {
            return buildError(Response.Status.BAD_REQUEST, "INVALID_SUPPLIER", 
                              "Los datos del proveedor son insuficientes o nulos.", "/suppliers");
        }

        Supplier result = supplyService.createSupplier(supplier);
        return Response.status(Response.Status.CREATED).entity(result).build();
    }

    private Response buildError(Response.Status status, String code, String message, String path) {
        ApiError error = new ApiError();
        error.setCode(code);
        error.setMessage(message);
        error.setTimestamp(OffsetDateTime.now());
        error.setPath("/api/v1" + path);
        return Response.status(status).entity(error).build();
    }
}