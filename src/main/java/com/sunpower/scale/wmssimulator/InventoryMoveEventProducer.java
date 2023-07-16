package com.sunpower.scale.wmssimulator;

import com.sunpower.scale.wmssimulator.dto.InventoryMovementRecord;
import com.sunpower.scale.wmssimulator.dto.MovementType;
import com.sunpower.scale.wmssimulator.events.InventoryMovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryMoveEventProducer {

    private final KafkaTemplate<String, InventoryMovedEvent> kafkaTemplate ;

    Random productIDRandom = new Random();
    Random wareHouseIdRandom = new Random();
    Random quantityRandom = new Random();
    Random movementTypeRandom = new Random();



    /**
     * Simulates emitting of WMS domain event of Inventory movements. Configured to send one event every 1000 ms
     * SKU is randomly generated between PRODUCT1000 and PRODUCT1200.
     * Quantity is randomly generated between 0 and 99.
     * WarehouseId is randomly generated between WH1000 and WH1020
     * MovementType is randomly generated between one of MovementType.RECEIVED_AT_WAREHOUSE and MovementType.DELIVERED_FROM_WAREHOUSE
     * In a complete implementation, we should also produce domain events related to Warehouse, Product and Inventory,
     * for this implementation we have chosen to focus on the most important event for Inventory movements.
     */
    @Scheduled (fixedRate = 1000)
    public void produceInventoryMoveEvents(){

        int movementTypeCoin = movementTypeRandom.nextInt(2);

        InventoryMovementRecord record = InventoryMovementRecord.builder()
                .sku("PRODUCT" + (1000 + productIDRandom.nextInt(201)))
                .quantityMoved(quantityRandom.nextInt(100))
                .warehouseId("WH" + (1000 + wareHouseIdRandom.nextInt(21)))
                .movementType(movementTypeCoin == 0 ? MovementType.RECEIVED_AT_WAREHOUSE : MovementType.DELIVERED_FROM_WAREHOUSE)
                .movementTimeStamp(LocalDateTime.now())
                .build();
        InventoryMovedEvent event = InventoryMovedEvent.builder()
                .name("InventoryMovedEvent")
                .timeStamp(LocalDateTime.now())
                .data(record)
                .build();

        sendEvent(event);
    }

    /**
     * Sends an event to kafka topic to a specific partition. Ensures that combination of WH+Product is always
     * delivered to the same partition to ensure ordering of the events. WH+ProductCode Hashcode mod 16 is used
     * to calculate to which partition the message should be delivered to.
     * @param event
     */
    private void sendEvent(InventoryMovedEvent event){

        String key = event.getData().getWarehouseId() + event.getData().getSku();
        CompletableFuture<SendResult<String, InventoryMovedEvent>> future
                = kafkaTemplate.send("InventoryMovedEvent", Math.abs(key.hashCode())%16 , key , event);

        future.whenComplete((result, ex) -> {
                    log.info(result.toString());
                    if (null == ex){
                        log.info("InventoryMoveEventProducer:InventoryMovedEvent: InventoryMovedEvent sent {}",event);
                    } else {
                        log.error("InventoryMoveEventProducer:InventoryMovedEvent: InventoryMovedEvent unable to send due to {}",ex.getMessage());
                    }
                }
        );
    }
}
