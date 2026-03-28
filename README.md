# BaseListener Framework

![Java](https://img.shields.io/badge/Java-17+-orange)
![Platform](https://img.shields.io/badge/Platform-Bukkit%20%7C%20Spigot-green)
![License](https://img.shields.io/badge/License-MIT-blue)

A lightweight, annotation-driven event listener framework for Bukkit/Spigot plugins.
It replaces traditional listeners with a clean, declarative, and extensible system.

---

## Features

* Annotation-based event handling (`@Listen`)
* Async execution via `@Async`
* Custom annotation system (conditions, filters, limits)
* Automatic player resolution (`getPlayer()`)
* Extensible via `AnnotationHandler`
* Minimal boilerplate and clean structure

---

## Installation

### Maven (JitPack)

Add the JitPack repository:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency:

```xml
<dependency>
    <groupId>com.github.MoritzMCC</groupId>
    <artifactId>BaseListener</artifactId>
    <version>v1.0.0</version>
</dependency>
```

---

### Important

* `artifactId` **must match your GitHub repository name**
* `version` = Git tag (recommended) or commit hash

Example:

```bash
git tag v1.0.0
git push origin v1.0.0
```

---

## Quick Start

Extend `BaseListener` and implement your event methods.

A full working example can be found in:

```
de.MoritzMCC.example.ExampleListener
```

---

## How It Works

1. Scans all methods annotated with `@Listen`
2. Matches them to event types
3. Processes annotations via registered `AnnotationHandler`s
4. Executes the method (synchronously or asynchronously)

---

## Built-in Annotations

### `@Listen`

Marks a method as an event handler.

**Requirements:**

* Exactly one parameter
* Parameter must extend `Event`

---

### `@Async`

Executes the handler asynchronously using the Bukkit scheduler.

---

### `@requiresPlayer`

Ensures that a player is available for the event.

Supports:

* `PlayerEvent`
* `EntityEvent` where the entity is a `Player`

Provides access via:

```
getPlayer()
```

---

### `@cancelIf`

Cancels the event and skips execution if a condition is met.

---

### `@Limit`

Limits how often a handler can execute within a given timeframe.

Parameters:

* `limit`: maximum executions
* `resetAfter`: reset time in seconds

Behavior:

* Blocks execution after limit is reached
* Resets after specified time
* Cancels event if possible

---

## Player Access

Instead of manually extracting the player from each event, use:

```
getPlayer()
```

The framework automatically resolves the player when possible.

---

## Custom Annotations

The framework is designed to be extensible via custom annotations.

### Steps

1. Define a custom annotation
2. Implement an `AnnotationHandler`
3. Register the handler using:

```
BaseListener.registerAnnotationHandler(...)
```

---

## Example

A complete usage example demonstrating all features is available in:

```
de.MoritzMCC.example
```

---

## Execution Flow

```
Event Fired
   ↓
Find Matching Methods
   ↓
Process Annotations
   ↓
All Conditions Passed?
   ↓ yes
Execute Method (sync/async)
```

---

## Important Notes

* Exact event matching (no inheritance-based dispatch)
* Async handlers must be thread-safe
* Exceptions inside handlers are caught and logged
* Each handler method must have exactly one parameter

---

## Design Goals

* Reduce boilerplate in Bukkit listeners
* Improve readability and maintainability
* Enable modular logic through annotations
* Keep runtime overhead minimal

---

## License

MIT License – free to use, modify and distribute.

---

## Author

MoritzMCC
