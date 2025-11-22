package me.relrypesan.springworkflow.config;

import me.relrypesan.springworkflow.WorkflowChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkflowAutoConfiguration {

    @Bean
    public WorkflowChain workflowChain() {
        return new WorkflowChain();
    }

}
