package com.deva.paymentservice.repository;

import com.deva.paymentservice.entity.Payment;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class PaymentRepository {

    private final DynamoDbTable<Payment> paymentTable;

    public PaymentRepository(DynamoDbEnhancedClient enhancedClient,
                             @Value("${aws.dynamodb.tableName}") String tableName) {
        this.paymentTable = enhancedClient.table(tableName, TableSchema.fromBean(Payment.class));
    }

    public Payment save(Payment payment) {
        paymentTable.putItem(payment);
        return payment;
    }

    public Optional<Payment> findById(String paymentId) {
        Key key = Key.builder().partitionValue(paymentId).build();
        return Optional.ofNullable(paymentTable.getItem(key));
    }

    public List<Payment> findAll() {
        return paymentTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public List<Payment> findByOrderId(String orderId) {
        return findAll().stream()
                .filter(p -> orderId.equals(p.getOrderId()))
                .collect(Collectors.toList());
    }

    public List<Payment> findByUserId(String userId) {
        return findAll().stream()
                .filter(p -> userId.equals(p.getUserId()))
                .collect(Collectors.toList());
    }

    public void deleteById(String paymentId) {
        Key key = Key.builder().partitionValue(paymentId).build();
        paymentTable.deleteItem(key);
    }
}