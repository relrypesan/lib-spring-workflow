package me.relrypesan.projecttestworkflow.core.step;

import lombok.extern.slf4j.Slf4j;
import me.relrypesan.springworkflow.WorkflowContext;
import me.relrypesan.springworkflow.exceptions.WorkflowException;
import me.relrypesan.springworkflow.step.Step;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PersistProduct implements Step {

    @Override
    public void execute(WorkflowContext workflowContext) throws WorkflowException {
        log.info("Persisting product to the database (simulated)...");
    }

}
