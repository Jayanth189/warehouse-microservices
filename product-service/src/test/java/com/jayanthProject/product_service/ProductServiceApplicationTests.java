package com.jayanthProject.product_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayanthProject.product_service.dto.ProductRequest;
import com.jayanthProject.product_service.repository.ProductRespository;
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
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ProductRespository productRespository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void testGetAllProducts() throws Exception {
		int productSize = productRespository.findAll().size();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.length()").value(productSize))
				.andExpect(status().isOk());
	}

	@Test
	void testCreateProduct() throws Exception {

		int productSize = productRespository.findAll().size();
		ProductRequest productRequest = getProductRequest();
		String productRequestStr = objectMapper.writeValueAsString(productRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content(productRequestStr))
				.andExpect(status().isCreated());

		Assertions.assertEquals(productSize+1, productRespository.findAll().size());
	}


	private ProductRequest getProductRequest() {
		return ProductRequest.builder()
				.name("Test product")
				.description("Test product")
				.price(BigDecimal.valueOf(1200))
				.build();
	}

}
