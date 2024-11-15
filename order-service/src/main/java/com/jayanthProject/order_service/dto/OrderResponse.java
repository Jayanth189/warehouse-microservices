package com.jayanthProject.order_service.dto;

import com.jayanthProject.order_service.model.OrderLineItems;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {

    private Long id;
    private String orderNumber;

    private List<OrderLineItems> orderLineItemsList;

}
