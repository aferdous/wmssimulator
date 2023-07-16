package com.sunpower.scale.wmssimulator.events;


import com.sunpower.scale.wmssimulator.dto.InventoryMovementRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class InventoryMovedEvent {
        String name ;
        LocalDateTime timeStamp;
        InventoryMovementRecord data;
}
