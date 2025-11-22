package me.relrypesan.projecttestworkflow.core.step;

import lombok.extern.slf4j.Slf4j;
import me.relrypesan.projecttestworkflow.core.context.ContextKeys;
import me.relrypesan.projecttestworkflow.core.domain.Product;
import me.relrypesan.springworkflow.WorkflowContext;
import me.relrypesan.springworkflow.exceptions.WorkflowException;
import me.relrypesan.springworkflow.step.Step;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class PublishProductEvent implements Step {

    @Override
    public void execute(WorkflowContext workflowContext) throws WorkflowException {
        log.info("Publishing product event to message broker (simulated)...");
    }

    @Override
    public boolean shouldExecute(WorkflowContext context) {
        Product product = context.get(ContextKeys.PRODUCT);

        return product != null && product.weight() != null && product.weight().compareTo(BigDecimal.valueOf(10.0)) > 0;
    }
}
