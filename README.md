# lib-spring-workflow

*A lightweight, strongly‑typed, IDE‑friendly workflow engine for Spring applications.*

This repository contains the current, stable implementation of a small workflow execution library built on top of Spring. It uses explicit Java `Flow` classes and Spring-managed `Step` components to define execution graphs composed of sequential and parallel stages. The implementation in this repo (the latest version you provided) should be considered the canonical source for any future modifications.

---

## 🚀 Overview

`lib-spring-workflow` provides a minimal but opinionated runtime to define and execute workflows inside a Spring application. Key goals:

* **IDE-friendly**: Flows and steps are explicit Java classes (easy navigation, refactoring).
* **Strong typing**: Uses Java types and a typed `WorkflowRegistry` for context keys.
* **Parallelism by-level**: Each element (level) of a `FlowDefinition` can contain multiple `Step` instances; when an element has >1 step they run in parallel and the engine waits for all to finish before continuing.
* **Centralized registration**: Small utility `WorkflowRegistry` is used to register typed keys used in `WorkflowContext`.
* **Small surface area**: the API keeps concepts minimal (Flow, FlowDefinition, FlowElement, Step, WorkflowContext, WorkflowChain).

---

## 🔍 Key classes (actual names from the repository)

All classes live in package `me.relrypesan.springworkflow` (subpackages `flow`, `step`, `registries`, `config`, `exceptions`). The most relevant classes are:

* `me.relrypesan.springworkflow.step.Step` – interface representing a single step of work.
* `me.relrypesan.springworkflow.flow.Flow` – interface representing a flow. Implementations return a `FlowDefinition`.
* `me.relrypesan.springworkflow.flow.FlowDefinition` – immutable container of `FlowElement`s; created with `FlowDefinition.builder()`.
* `me.relrypesan.springworkflow.flow.FlowElement` – a single level of execution; contains a list of `Step` instances and exposes `isParallel()`.
* `me.relrypesan.springworkflow.WorkflowContext` – typed context storage keyed by `WorkflowRegistry` entries.
* `me.relrypesan.springworkflow.registries.WorkflowRegistry` – a static, typed registry for keys used inside the `WorkflowContext`.
* `me.relrypesan.springworkflow.registries.RegistryKeys` – convenience keys already registered (examples: `START_EXECUTION`, `CORRELATION_ID`, `EXECUTION_ID`, `SESSION_ID`).
* `me.relrypesan.springworkflow.WorkflowChain` – the engine that executes a `Flow` given a `WorkflowContext`. It returns a `WorkflowProcess` record after execution.
* `me.relrypesan.springworkflow.WorkflowProcess` – a small record holding a `(Flow, WorkflowContext)` pair after execution.
* `me.relrypesan.springworkflow.exceptions.WorkflowException` – runtime exception for workflow errors.
* `me.relrypesan.springworkflow.config.WorkflowAutoConfiguration` – Spring `@Configuration` that exposes a `WorkflowChain` bean.

> These names and signatures match the current code in the repository you uploaded and are used below in examples.

---

## 📦 Quick installation

Clone the repo and add this module to your project as a local dependency (or publish it to your internal registry):

```bash
git clone https://github.com/relrypesan/lib-spring-workflow.git
# then add the module to your build (Maven/Gradle)
```

There is an `app` module inside the project containing the implementation under `app/src/main/java/me/relrypesan/springworkflow`.

---

## 🧰 API overview and examples (accurate to the repository)

### `Step` — define a step

```java
package me.relrypesan.springworkflow.step;

import me.relrypesan.springworkflow.WorkflowContext;
import me.relrypesan.springworkflow.exceptions.WorkflowException;

@Component
public class StepA implements Step {
    @Override
    public void execute(WorkflowContext context) throws WorkflowException {
        // step logic here
    }
}
```

Steps are plain components. They may read and write to the provided `WorkflowContext`.

### `Flow` and `FlowDefinition` — define a flow

A `Flow` implementation returns a `FlowDefinition` built through the provided builder. Each `FlowElement` corresponds to a level. When an element has more than one `Step`, those steps are executed in parallel.

```java
@Component
public class CreateProductFlow implements Flow {

    private final StepA stepA;
    private final StepB stepB;
    private final StepC stepC;
    private final StepD stepD;

    public CreateProductFlow(StepA stepA, StepB stepB, StepC stepC, StepD stepD) {
        this.stepA = stepA;
        this.stepB = stepB;
        this.stepC = stepC;
        this.stepD = stepD;
    }

    @Override
    public FlowDefinition definition() {
        return FlowDefinition.builder()
            .addSteps(stepA)        // level 1: sequential
            .addSteps(stepB, stepC) // level 2: parallel (B and C concurrently)
            .addSteps(stepD)        // level 3: sequential
            .build();
    }
}
```

**Notes:**

* `FlowDefinition.builder().addSteps(...)` accepts one or more `Step` instances.
* `FlowElement.isParallel()` returns `true` when the level has more than one `Step`.

### `WorkflowContext` and `WorkflowRegistry`

The context is typed and keyed by `WorkflowRegistry` entries. This design avoids raw string keys and enforces type safety at runtime.

```java
// read/write example
WorkflowContext ctx = new WorkflowContext();
ctx.put(RegistryKeys.CORRELATION_ID, "abc-123");
String correlation = ctx.get(RegistryKeys.CORRELATION_ID);
```

`WorkflowRegistry.register("id", Type.class)` is used internally to declare typed keys (see `RegistryKeys` for examples).

### `WorkflowChain` — executing a flow

A `WorkflowChain` bean is provided by `WorkflowAutoConfiguration`. It exposes an execution method matching the current repository:

```java
WorkflowProcess process = workflowChain.execute(createProductFlow, ctx);
```

`execute(Flow, WorkflowContext)` runs every `FlowElement` in order, executing elements with multiple steps in parallel (the engine waits for completion before continuing). It returns a `WorkflowProcess` containing the `Flow` and the context used.

---

## 🧩 Concurrency & error behavior (how the current engine works)

* **Parallel execution:** the engine uses a thread pool and `invokeAll` semantics to run steps inside the same element concurrently. It waits for all futures to complete.
* **Error handling:** if any parallel step throws, the engine wraps/throws a `WorkflowException` and the flow execution terminates. Interrupted threads reassert the interrupt flag and throw a `WorkflowException`.

This behavior is intentional and keeps the runtime simple. See `WorkflowChain` implementation for details.

---

## 🛠️ Debugging and developer workflow

* Because flows are defined as classes that return a `FlowDefinition`, you can set breakpoints inside the `definition()` method to inspect the constructed `FlowDefinition` and the exact list of `FlowElement`s and `Step`s.
* Steps are normal Spring beans — you can place breakpoints inside step implementation methods as usual.

---

## 🧪 Example: minimal Spring Boot usage
Below is a real-world example of how to execute a workflow inside a REST controller:
```java
@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final WorkflowChain workflowChain;
    private final CreateProductFlow createProductFlow;

    @PostMapping
    public ResponseEntity<?> flow(@RequestBody Product product) {
        WorkflowContext ctx = new WorkflowContext();

        ctx.put(ContextKeys.PRODUCT, product);

        try {
            WorkflowProcess process = workflowChain.execute(createProductFlow, ctx);

            Map<String, Object> responseData = process.context().asMap()
                    .entrySet()
                    .stream()
                    .map(data -> Map.entry(data.getKey().id(), data.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            return ResponseEntity.ok(responseData);

        } catch (WorkflowException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

```

---

## 📚 Contribution

Contributions are welcome. Basic workflow:

1. Fork the repository
2. Create a feature branch
3. Add tests and code
4. Send a PR

Please keep API changes backward-compatible when possible and update this README with any breaking changes.

---

## 📄 License

Add your preferred license file to the repository (MIT is a good default).

---

If you want, I can now:

* open the README in the repo and commit this updated file (I can prepare a patch you can apply), or
* further refine the README to include exact package/class javadoc snippets pulled from the source files.

Tell me which one you prefer.
