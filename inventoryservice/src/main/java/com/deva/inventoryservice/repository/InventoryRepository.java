package com.deva.inventoryservice.repository;

import com.deva.inventoryservice.entity.Inventory;
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
public class InventoryRepository {

    private final DynamoDbTable<Inventory> inventoryTable;

    public InventoryRepository(DynamoDbEnhancedClient enhancedClient,
                               @Value("${aws.dynamodb.tableName}") String tableName) {
        this.inventoryTable = enhancedClient.table(tableName, TableSchema.fromBean(Inventory.class));
    }

    public Inventory save(Inventory inventory) {
        inventoryTable.putItem(inventory);
        return inventory;
    }

    public Optional<Inventory> findByProductId(String productId) {
        Key key = Key.builder().partitionValue(productId).build();
        return Optional.ofNullable(inventoryTable.getItem(key));
    }

    public List<Inventory> findAll() {
        return inventoryTable.scan(ScanEnhancedRequest.builder().build())
                .items()
                .stream()
                .collect(Collectors.toList());
    }

    public void deleteByProductId(String productId) {
        Key key = Key.builder().partitionValue(productId).build();
        inventoryTable.deleteItem(key);
    }
}