package me.relrypesan.springworkflow.registries;

public class RegistryKeys {
    public static final WorkflowRegistry<Long> START_EXECUTION = WorkflowRegistry.register("start_execution", Long.class);
    public static final WorkflowRegistry<String> CORRELATION_ID = WorkflowRegistry.register("correlation_id", String.class);
    public static final WorkflowRegistry<String> EXECUTION_ID = WorkflowRegistry.register("execution_id", String.class);
    public static final WorkflowRegistry<String> SESSION_ID = WorkflowRegistry.register("session_id", String.class);
}
