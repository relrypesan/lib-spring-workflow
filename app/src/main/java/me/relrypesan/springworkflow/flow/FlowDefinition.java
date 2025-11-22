package me.relrypesan.springworkflow.flow;

import me.relrypesan.springworkflow.step.Step;

import java.util.ArrayList;
import java.util.List;

public record FlowDefinition(List<FlowElement> elements) {
    public FlowDefinition(List<FlowElement> elements) {
        this.elements = List.copyOf(elements);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<FlowElement> levels = new ArrayList<>();

        public Builder addSteps(Step... steps) {
            levels.add(FlowElement.of(List.of(steps)));
            return this;
        }

        public FlowDefinition build() {
            return new FlowDefinition(levels);
        }
    }
}
