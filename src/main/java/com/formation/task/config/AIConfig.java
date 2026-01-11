package com.formation.task.config;

import com.formation.task.services.AIService;
import com.formation.task.services.DummyAIService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AIConfig {

    @Bean
    @Primary
    public AIService aiService(DummyAIService dummyAIService) {
        // Cette configuration garantit qu'un bean AIService sera toujours disponible
        // Si HuggingFaceAIService est créé (quand AI_ENABLED=true), il prendra le dessus
        // grâce à @Primary sur DummyAIService et la priorité des beans
        return dummyAIService;
    }
}