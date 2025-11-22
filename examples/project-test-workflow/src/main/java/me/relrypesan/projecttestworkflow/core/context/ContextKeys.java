package me.relrypesan.projecttestworkflow.core.context;

import me.relrypesan.projecttestworkflow.core.domain.Product;
import me.relrypesan.springworkflow.registries.WorkflowRegistry;

import java.math.BigDecimal;

public class ContextKeys {
    public static final WorkflowRegistry<Product> PRODUCT = WorkflowRegistry.register("product", Product.class);
    public static final WorkflowRegistry<BigDecimal> BASE_DELIVERY_PRICE = WorkflowRegistry.register("baseDeliveryPrice", BigDecimal.class);
}
