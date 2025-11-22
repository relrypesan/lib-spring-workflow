package me.relrypesan.springworkflow.registries;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record WorkflowRegistry<T>(String id, Class<T> type) {
    private static final Map<String, WorkflowRegistry<?>> REGISTRIES = new ConcurrentHashMap<>();

    public static synchronized <T> WorkflowRegistry<T> register(String id, Class<T> type) {
        if (REGISTRIES.containsKey(id)) {
            throw new IllegalStateException("Duplicate registry id: " + id);
        }
        WorkflowRegistry<T> reg = new WorkflowRegistry<>(id, type);
        REGISTRIES.put(id, reg);
        return reg;
    }

    @SuppressWarnings("unchecked")
    public static <T> WorkflowRegistry<T> get(String id) {
        return (WorkflowRegistry<T>) REGISTRIES.get(id);
    }
}
