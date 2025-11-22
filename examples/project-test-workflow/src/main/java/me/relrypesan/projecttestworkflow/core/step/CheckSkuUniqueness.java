package me.relrypesan.projecttestworkflow.core.step;

import me.relrypesan.projecttestworkflow.core.context.ContextKeys;
import me.relrypesan.projecttestworkflow.core.domain.Product;
import me.relrypesan.springworkflow.WorkflowContext;
import me.relrypesan.springworkflow.exceptions.WorkflowException;
import me.relrypesan.springworkflow.step.Step;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CheckSkuUniqueness implements Step {

    private final Map<String, Product> existingSkus = new HashMap<>();

    @Override
    public void execute(WorkflowContext workflowContext) throws WorkflowException {
        Product product = workflowContext.get(ContextKeys.PRODUCT);

        if (product == null || product.id() == null || product.id().isEmpty()) {
            throw new WorkflowException("SKU is missing");
        }
        if (existingSkus.containsKey(product.id())) {
            throw new WorkflowException("SKU already exists: " + product.id());
        }

        existingSkus.put(product.id(), product);
    }

}
