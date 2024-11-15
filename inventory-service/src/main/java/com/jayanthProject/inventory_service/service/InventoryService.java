package com.jayanthProject.inventory_service.service;

import com.jayanthProject.inventory_service.dto.InventoryRequest;
import com.jayanthProject.inventory_service.dto.InventoryResponse;
import com.jayanthProject.inventory_service.model.Inventory;
import com.jayanthProject.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCodes) {
        log.info("wait started");
        log.info("wait end");
        return inventoryRepository.findBySkuCodeIn(skuCodes)
                .stream().map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity()>0)
                                .build()
                ).toList();
    }

    public String addInStock(InventoryRequest inventoryRequest) {
        Inventory inventory = new Inventory();
        inventory.setSkuCode(inventoryRequest.getSkuCode());
        inventory.setQuantity(inventoryRequest.getQuantity());
        inventoryRepository.save(inventory);
        return "successfully added in stock";
    }

    public List<InventoryResponse> getAllStocks() {
        List<Inventory> allInventory = inventoryRepository.findAll();
        return allInventory
                .stream().map(inventory ->
                        InventoryResponse.builder()
                                .skuCode(inventory.getSkuCode())
                                .isInStock(inventory.getQuantity()>0)
                                .build()
                ).toList();
    }
}
