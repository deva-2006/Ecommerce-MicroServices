package com.deva.paymentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    private String paymentId;
    private String orderId;
    private String userId;
    private Double amount;
    private String paymentMethod;
    private String status;
    private String createdAt;

    @DynamoDbPartitionKey
    public String getPaymentId() { return paymentId; }

    public String getOrderId() { return orderId; }
    public String getUserId() { return userId; }
    public Double getAmount() { return amount; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}