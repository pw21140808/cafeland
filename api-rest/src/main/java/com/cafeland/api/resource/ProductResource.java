package com.cafeland.api.resource;

import com.cafeland.api.model.*;
import com.cafeland.api.service.ProductService;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.OffsetDateTime;
import java.util.List;

@Path("/api/v1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProductResource {

    @Inject
    ProductService productService;

    @GET
    @Path("/products")
    public Response listProducts(@QueryParam("search") String search) {
        List<ProductResponse> products = productService.listProducts(search);
        
        if (products == null || products.isEmpty()) {
            String msg = (search != null) ? "No se encontraron productos para: " + search : "No hay productos registrados en el sistema.";
            return buildInfoResponse(Response.Status.OK, "EMPTY_LIST", msg);
        }
        
        return Response.ok(products).build();
    }

    @POST
    @Path("/products") 
    public Response createProduct(@Valid Product productRequest) {
        
        if (productRequest == null || productRequest.getName() == null) {
            return buildError(Response.Status.BAD_REQUEST, "NULL_FIELD", 
                              "Error: El nombre del producto no puede ser nulo.", "/products");
        }
        
        if (productRequest.getName().length() > 50) {
            return buildError(Response.Status.BAD_REQUEST, "LENGTH_EXCEEDED", 
                              "El nombre es demasiado largo (máximo 50 caracteres).", "/products");
        }

        ProductResponse response = productService.createProduct(productRequest);
        
        if (response == null) {
            return buildError(Response.Status.INTERNAL_SERVER_ERROR, "CREATE_FAILED", 
                              "No se pudo procesar la creación del producto.", "/products");
        }

        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    @GET
    @Path("/products/{productId}")
    public Response getProductById(@PathParam("productId") String productId) {
        ProductResponse response = productService.getProductById(productId);
        
        if (response == null) {
            return buildError(Response.Status.NOT_FOUND, "NOT_FOUND", 
                              "El producto con ID " + productId + " no existe en la base de datos.", "/products/" + productId);
        }
        return Response.ok(response).build();
    }

    @PUT
    @Path("/products/{productId}")
    public Response updateProduct(@PathParam("productId") String productId, @Valid Product productRequest) {
        
        // Eliminamos la validación del ID interno porque la clase Product no tiene ID
        ProductResponse updated = productService.updateProduct(productId, productRequest);
        
        if (updated == null) {
            return buildError(Response.Status.NOT_FOUND, "UPDATE_ERROR", 
                              "No se encontró el producto " + productId + " para actualizar.", "/products/" + productId);
        }
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/products/{productId}")
    public Response deleteProduct(@PathParam("productId") String productId) {
        boolean deleted = productService.deleteProduct(productId);
        
        if (!deleted) {
            return buildError(Response.Status.NOT_FOUND, "DELETE_ERROR", 
                              "Error: No se encontró el producto " + productId + " o ya fue eliminado.", "/products/" + productId);
        }

        DeleteConfirmation confirm = new DeleteConfirmation();
        confirm.setId(productId);
        confirm.setMessage("Operación exitosa: El producto ha sido borrado de Cafeland.");
        confirm.setDeletedAt(OffsetDateTime.now());
        
        return Response.ok(confirm).build();
    }


    private Response buildInfoResponse(Response.Status status, String code, String message) {
        ApiError info = new ApiError();
        info.setCode(code);
        info.setMessage(message);
        info.setTimestamp(OffsetDateTime.now());
        info.setPath("/api/v1/products");
        return Response.status(status).entity(info).build();
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