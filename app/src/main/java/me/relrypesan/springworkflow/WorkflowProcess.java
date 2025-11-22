package me.relrypesan.springworkflow;

import me.relrypesan.springworkflow.flow.Flow;

public record WorkflowProcess(Flow flow, WorkflowContext context) {
}
