package com.jayanthProject.order_service.service;

import com.jayanthProject.order_service.dto.InventoryResponse;
import com.jayanthProject.order_service.dto.OrderLineItemsDto;
import com.jayanthProject.order_service.dto.OrderRequest;
import com.jayanthProject.order_service.dto.OrderResponse;
import com.jayanthProject.order_service.event.OrderPlacedEvent;
import com.jayanthProject.order_service.model.Order;
import com.jayanthProject.order_service.model.OrderLineItems;
import com.jayanthProject.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();
        order.setOrderLineItemsList(orderLineItems);

        //Have to call inventory service, and place order if product is in stock

        List<String> skuCodes = orderLineItems.stream().map(OrderLineItems::getSkuCode).toList();

        InventoryResponse[] inventoryResponseArray = webClientBuilder.build().get()
                .uri("http://inventory-service/api/inventory", uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();

        boolean allProductsAvailable = false;
        if(inventoryResponseArray!=null && inventoryResponseArray.length>0){
            allProductsAvailable = Arrays.stream(inventoryResponseArray).allMatch(InventoryResponse::isInStock);
        }

        if(!allProductsAvailable){
            throw new IllegalArgumentException("Product is not in stock, please try again");
        }
        orderRepository.save(order);
        kafkaTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
        return "successfully placed an order";

    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(orderLineItemsDto.getQuantity());
        orderLineItems.setPrice(orderLineItemsDto.getPrice());
        orderLineItems.setSkuCode(orderLineItemsDto.getSkuCode());
        return orderLineItems;
    }

    public List<OrderResponse> getAllOrders() {
        List<Order> allOrders = orderRepository.findAll();
        return allOrders.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    private OrderResponse mapToResponseDto(Order order) {
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderNumber(order.getOrderNumber());
        orderResponse.setId(order.getId());
        orderResponse.setOrderLineItemsList(order.getOrderLineItemsList());
        return orderResponse;
    }
}
