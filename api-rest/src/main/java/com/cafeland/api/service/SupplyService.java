package com.cafeland.api.service;

import com.cafeland.api.model.Supply;
import com.cafeland.api.model.SupplyInput;
import com.cafeland.api.model.Supplier;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class SupplyService {

    private List<Supply> bodega = new ArrayList<>();
    private List<Supplier> proveedores = new ArrayList<>();

    public List<Supply> getAllSupplies() {
        return bodega;
    }

    public Supply createSupply(SupplyInput input) {
        if (input == null) return null;

        Supply nuevo = new Supply();
        nuevo.setSupplyId("ins-" + (bodega.size() + 101));
        nuevo.setSupplyName(input.getSupplyName());
        nuevo.setStock(input.getCurrentStock()); 
        
        if (input.getUnit() != null) {
            nuevo.setUnit(Supply.UnitEnum.fromValue(input.getUnit().toString()));
        }
        
        bodega.add(nuevo);
        return nuevo;
    }

    public Supply updateStock(String supplyId, Integer variacion) {
        for (Supply s : bodega) {
            if (s.getSupplyId().equals(supplyId)) {
                s.setStock(s.getStock() + variacion);
                return s;
            }
        }
        return null;
    }

    public List<Supplier> getAllSuppliers() {
        return proveedores;
    }

    public Supplier createSupplier(Supplier input) {
        if (input == null) return null;

        input.setSupplierId("prov-" + (proveedores.size() + 101));
        
        proveedores.add(input);
        return input; 
    }
}