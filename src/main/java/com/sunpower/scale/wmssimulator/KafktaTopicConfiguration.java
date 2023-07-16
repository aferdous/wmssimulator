package com.sunpower.scale.wmssimulator;

import com.sunpower.scale.wmssimulator.events.InventoryMovedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafktaTopicConfiguration {
    @Bean
    public NewTopic inventoryMoveTopicBuilder(){
        return TopicBuilder.name("InventoryMovedEvent")
                .partitions(16)
                .build();
    }
}
