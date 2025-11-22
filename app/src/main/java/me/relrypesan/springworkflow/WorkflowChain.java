package me.relrypesan.springworkflow;

import lombok.extern.slf4j.Slf4j;
import me.relrypesan.springworkflow.exceptions.WorkflowException;
import me.relrypesan.springworkflow.flow.Flow;
import me.relrypesan.springworkflow.flow.FlowDefinition;
import me.relrypesan.springworkflow.flow.FlowElement;
import me.relrypesan.springworkflow.step.Step;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Core executor for flows.
 */
@Slf4j
public class WorkflowChain {
    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public WorkflowProcess execute(Flow flow, WorkflowContext ctx) throws WorkflowException {

        FlowDefinition definition = flow.definition();

        for (FlowElement fe : definition.elements()) {
            executeFlowLine(ctx, fe);
        }

        return new WorkflowProcess(flow, ctx);
    }

    private void executeFlowLine(WorkflowContext ctx, FlowElement fe) {
        try {
            if (fe.isParallel()) {
                executeParallel(ctx, fe);
            } else {
                executeSingle(ctx, fe);
            }
        } catch (RuntimeException ex) {
            throw new WorkflowException("Error executing flow element", ex);
        }
    }

    private void executeStep(WorkflowContext ctx, Step conditionalStep) {
        if (conditionalStep.shouldExecute(ctx)) {
            conditionalStep.execute(ctx);
        } else {
            // Step skipped due to condition
        }
    }

    private void executeSingle(WorkflowContext ctx, FlowElement el) {
        Step step = el.steps().getFirst();
        this.executeStep(ctx, step);
    }

    private void executeParallel(WorkflowContext ctx, FlowElement el) {
        List<Step> parallel = el.steps();
        try {
            List<Callable<Void>> tasks = parallel.stream()
                    .map(step -> (Callable<Void>) () -> {
                        this.executeStep(ctx, step);
                        return null;
                    }).collect(Collectors.toList());

            List<Future<Void>> futures = executor.invokeAll(tasks);
            for (Future<Void> f : futures) {
                try {
                    f.get();
                } catch (ExecutionException ee) {
                    throw new WorkflowException("Parallel step failed", ee.getCause());
                }
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            throw new WorkflowException("Parallel execution interrupted", ie);
        }
    }

}
