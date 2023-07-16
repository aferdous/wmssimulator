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



    private void sendEvent(InventoryMovedEvent event){
        //We have configured 16 shards. In order to guarantee order for movements on WH+Product combination
        // We will ensure that same WH+Product combination already gets pushed to the same partition.
        String key = event.getData().getWarehouseId() + event.getData().getSku();
        // send the event through Kafka.
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
    @Scheduled (fixedRate = 5000)
    public void produceInventoryMoveEvents(){

        int movementTypeCoin = movementTypeRandom.nextInt(2);

        InventoryMovementRecord record = InventoryMovementRecord.builder()
                .sku("PRODUCT" + (1000 + productIDRandom.nextInt(200)))
                .quantityMoved(quantityRandom.nextInt(100))
                .warehouseId("WH" + (1000 + wareHouseIdRandom.nextInt(20)))
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
}
