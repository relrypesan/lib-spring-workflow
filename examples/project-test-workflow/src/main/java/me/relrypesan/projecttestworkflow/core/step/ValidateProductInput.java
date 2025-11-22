package me.relrypesan.projecttestworkflow.core.step;

import me.relrypesan.projecttestworkflow.core.context.ContextKeys;
import me.relrypesan.projecttestworkflow.core.domain.Product;
import me.relrypesan.springworkflow.WorkflowContext;
import me.relrypesan.springworkflow.exceptions.WorkflowException;
import me.relrypesan.springworkflow.step.Step;
import org.springframework.stereotype.Component;

@Component
public class ValidateProductInput implements Step {

    @Override
    public void execute(WorkflowContext workflowContext) throws WorkflowException {
        if (!workflowContext.containsKey(ContextKeys.PRODUCT)) {
            throw new WorkflowException("Missing required product information");
        }

        Product product = workflowContext.get(ContextKeys.PRODUCT);
        if (product.id() == null || product.id().isEmpty()) {
            throw new WorkflowException("Product ID is missing");
        }
        if (product.name() == null || product.name().isEmpty()) {
            throw new WorkflowException("Product name is missing");
        }
        if (product.weight() == null) {
            throw new WorkflowException("Product weight is missing");
        }
        if (product.price() == null) {
            throw new WorkflowException("Product price is missing");
        }
    }

}


