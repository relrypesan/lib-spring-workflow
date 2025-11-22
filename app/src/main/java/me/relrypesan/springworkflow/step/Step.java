package me.relrypesan.springworkflow.step;

import me.relrypesan.springworkflow.WorkflowContext;
import me.relrypesan.springworkflow.exceptions.WorkflowException;

public interface Step {
    /**
     * Unique name of the step used in workflow definitions.
     */
    default String name() {
        return this.getClass().getSimpleName();
    }

    /**
     * Execute the step. The implementation may read/write to the context.
     * Throw WorkflowException to indicate a failure that should stop execution.
     */
    void execute(WorkflowContext context) throws WorkflowException;

    /**
     * Determine if this step should be executed given the current context.
     * Default implementation always returns true.
     */
    default boolean shouldExecute(WorkflowContext context) {
        return true;
    }
}
