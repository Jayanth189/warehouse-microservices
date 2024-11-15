package com.jayanthProject.inventory_service.controller;

import com.jayanthProject.inventory_service.dto.InventoryRequest;
import com.jayanthProject.inventory_service.dto.InventoryResponse;
import com.jayanthProject.inventory_service.model.Inventory;
import com.jayanthProject.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> isInStock(@RequestParam List<String> skuCodes){
        return inventoryService.isInStock(skuCodes);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String addInStock(@RequestBody InventoryRequest inventoryRequest){
        return inventoryService.addInStock(inventoryRequest);
    }

    @GetMapping("/getAll")
    @ResponseStatus(HttpStatus.OK)
    public List<InventoryResponse> getAllStocks(){
        return inventoryService.getAllStocks();
    }
}
