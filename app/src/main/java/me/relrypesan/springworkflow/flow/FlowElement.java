package me.relrypesan.springworkflow.flow;

import me.relrypesan.springworkflow.step.Step;

import java.util.List;

public record FlowElement(List<Step> steps) {

    public static FlowElement of(Step step) {
        return new FlowElement(List.of(step));
    }

    public static FlowElement of(List<Step> steps) {
        return new FlowElement(List.copyOf(steps));
    }

    public boolean isParallel() {
        return this.steps.size() > 1;
    }
}
