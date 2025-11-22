package me.relrypesan.projecttestworkflow.core.flow;

import lombok.RequiredArgsConstructor;
import me.relrypesan.projecttestworkflow.core.step.*;
import me.relrypesan.springworkflow.flow.Flow;
import me.relrypesan.springworkflow.flow.FlowDefinition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreateProductFlow implements Flow {

    private final ValidateProductInput validateProductInput;
    private final CheckSkuUniqueness checkSkuUniqueness;
    private final CalculateShippingPrice calculateShippingPrice;
    private final PersistProduct persistProduct;
    private final PublishProductEvent publishProductEvent;

    @Override
    public FlowDefinition definition() {
        return FlowDefinition.builder()
                .addSteps(validateProductInput)
                .addSteps(checkSkuUniqueness, calculateShippingPrice)
                .addSteps(persistProduct)
                .addSteps(publishProductEvent)
                .build();
    }
}
