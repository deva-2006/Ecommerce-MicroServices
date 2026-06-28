package com.deva.cartservice.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;


@DynamoDbBean
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

    private String userId;
    private String productId;
    private String productName;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
    private String addedAt;

    @DynamoDbPartitionKey
    public String getUserId() { return userId; }


    @DynamoDbSortKey
    public String getProductId() { return productId; }

    public String getProductName() { return productName; }
    public Double getPrice() { return price; }
    public Integer getQuantity() { return quantity; }
    public Double getTotalPrice() { return totalPrice; }
    public String getAddedAt() { return addedAt; }
}