package me.relrypesan.projecttestworkflow.adaper.web;

import lombok.RequiredArgsConstructor;
import me.relrypesan.projecttestworkflow.core.context.ContextKeys;
import me.relrypesan.projecttestworkflow.core.domain.Product;
import me.relrypesan.projecttestworkflow.core.flow.CreateProductFlow;
import me.relrypesan.springworkflow.WorkflowChain;
import me.relrypesan.springworkflow.WorkflowContext;
import me.relrypesan.springworkflow.WorkflowProcess;
import me.relrypesan.springworkflow.exceptions.WorkflowException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/products")
public class ProductController {

    private final WorkflowChain workflowChain;
    private final CreateProductFlow createProductFlow;

    @PostMapping
    public ResponseEntity<?> flow(@RequestBody Product product) {
        WorkflowContext ctx = new WorkflowContext();

        ctx.put(ContextKeys.PRODUCT, product);

        try {
            WorkflowProcess process = workflowChain.execute(createProductFlow, ctx);

            Map<String, Object> responseData = process.context().asMap();

            return ResponseEntity.ok(responseData);
        } catch (WorkflowException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
