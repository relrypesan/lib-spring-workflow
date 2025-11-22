package me.relrypesan.projecttestworkflow.core.step;

import me.relrypesan.projecttestworkflow.core.context.ContextKeys;
import me.relrypesan.projecttestworkflow.core.domain.Product;
import me.relrypesan.springworkflow.WorkflowContext;
import me.relrypesan.springworkflow.exceptions.WorkflowException;
import me.relrypesan.springworkflow.step.Step;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CalculateShippingPrice implements Step {

    @Override
    public void execute(WorkflowContext workflowContext) throws WorkflowException {
        Product product = workflowContext.get(ContextKeys.PRODUCT);
        if (product == null) {
            throw new WorkflowException("Product is missing");
        }

        if (product.weight() == null) {
            throw new WorkflowException("Product weight is missing");
        }

        if (product.price() == null) {
            throw new WorkflowException("Product price is missing");
        }

        BigDecimal shippingPrice = product.price().multiply(BigDecimal.valueOf(0.1)).multiply(product.weight());
        if (shippingPrice.compareTo(BigDecimal.valueOf(2.00)) < 0) {
            shippingPrice = BigDecimal.valueOf(2.00);
        }

        workflowContext.put(ContextKeys.BASE_DELIVERY_PRICE, shippingPrice);
    }

}
