package com.jayanthProject.inventory_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayanthProject.inventory_service.dto.InventoryRequest;
import com.jayanthProject.inventory_service.model.Inventory;
import com.jayanthProject.inventory_service.repository.InventoryRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class InventoryServiceApplicationTests {

	@Container
	static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13-alpine")
			.withDatabaseName("warehouseDB_inventory")
			.withUsername("postgres")
			.withPassword("root");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private InventoryRepository inventoryRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		dynamicPropertyRegistry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		dynamicPropertyRegistry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}

	@Test
	void testIsInStock() throws Exception{
		boolean isInStock;
		String skuCode = "random_sku";
		Inventory inventory = inventoryRepository.findBySkuCode(skuCode);
		if (inventory != null) {
			isInStock = (inventory.getQuantity() > 0);
		} else {
			isInStock = false;
		}

		String responseContent = mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory")
						.param("skuCodes", skuCode)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();

		boolean response = Boolean.parseBoolean(responseContent);
		Assertions.assertEquals(isInStock, response);

	}

	@Test
	void testAddInStock() throws Exception {
		int inventorySize = inventoryRepository.findAll().size();
		InventoryRequest inventoryRequest = getInventoryRequest();
		String inventoryRequestStr = objectMapper.writeValueAsString(inventoryRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/inventory")
						.contentType(MediaType.APPLICATION_JSON)
						.content(inventoryRequestStr))
				.andExpect(status().isCreated());

		Assertions.assertEquals(inventorySize+1, inventoryRepository.findAll().size());
	}

	@Test
	void testGetAllProducts() throws Exception {
		int stockSize = inventoryRepository.findAll().size();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/inventory/getAll")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(stockSize))
				.andExpect(status().isOk());
	}

	private InventoryRequest getInventoryRequest() {
		return new InventoryRequest("random_sku_code", 2);
	}

}
