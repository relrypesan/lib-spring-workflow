package me.relrypesan.springworkflow;

import me.relrypesan.springworkflow.registries.WorkflowRegistry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class WorkflowContext {
    private final ConcurrentHashMap<WorkflowRegistry<?>, Object> data = new ConcurrentHashMap<>();

    public <T> WorkflowContext put(WorkflowRegistry<T> key, T value) {
        data.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(WorkflowRegistry<T> key) {
        Object value = data.get(key);
        if (value == null) return null;
        if (!key.type().isInstance(value)) {
            throw new ClassCastException("Type mismatch for key " + key.id());
        }
        return (T) value;
    }

    public <T> boolean containsKey(WorkflowRegistry<T> key) {
        return data.containsKey(key);
    }

    public Map<String, Object> asMap() {
        return data.entrySet()
                .stream()
                .map(data -> Map.entry(data.getKey().id(), data.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
