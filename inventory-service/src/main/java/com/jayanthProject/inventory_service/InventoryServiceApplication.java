package com.jayanthProject.inventory_service;

import com.jayanthProject.inventory_service.model.Inventory;
import com.jayanthProject.inventory_service.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

//	@Bean
//	public CommandLineRunner loadData(InventoryRepository inventoryRepository){
//		return args -> {
//			Inventory inventory = new Inventory();
//			inventory.setSkuCode("iphone_charger");
//			inventory.setQuantity(0);
//
//			Inventory inventory1 = new Inventory();
//			inventory1.setSkuCode("boat_earphones");
//			inventory1.setQuantity(0);
//
//			Inventory inventory2 = new Inventory();
//			inventory2.setSkuCode("boat_powerbank");
//			inventory2.setQuantity(0);
//
//			inventoryRepository.save(inventory);
//			inventoryRepository.save(inventory1);
//			inventoryRepository.save(inventory2);
//		};
//	}
}
