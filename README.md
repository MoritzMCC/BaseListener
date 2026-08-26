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
    <version>v2.0.0</version>
</dependency>
```

---

## Quick Start

Extend `BaseListener` and implement your event methods.

A full working example can be found in:

```
de.moritzmcc.example.ExampleListener
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

* Exactly one parameter must extend `Event`
* Additional parameters can be resolved via a `ParameterResolver` (e.g. `@Inject Player`, `@PlayersNearby List<Player>`)

---

### `@Async`

Executes the handler asynchronously using the Bukkit scheduler.

---

### `@RequiresPlayer`

Ensures that a player is available for the event.

Supports:

* `PlayerEvent`
* `EntityEvent` where the entity is a `Player`

Provides access via:

\`\`\`
getPlayer()
\`\`\`

---

### `@CancelIf`

Cancels the event and skips execution if a condition is met.

Parameters:

* `condition`: a `CancelCondition` implementation evaluated against the event
* `cancel`: if `true`, always cancels regardless of the condition

Only has an effect on `Cancellable` events.

---

### `@Limit`

Limits how often a handler can execute within a given timeframe.

Parameters:

* `limit`: maximum executions
* `resetAfter`: reset time in seconds
* `scope`: `PLAYER` (per player) or `GLOBAL` (shared across all players)

Behavior:

* Blocks execution after limit is reached
* Resets after specified time
* Cancels event if possible

---

### `@Cooldown`

Enforces a cooldown between executions of a handler.

Parameters:

* `seconds` / `milliseconds`: cooldown duration
* `scope`: `PLAYER` (per player) or `GLOBAL` (shared across all players)

Behavior:

* Cancels the event while the cooldown is active
* Cooldown starts fresh on every allowed execution

---

### `@Throttle`

Rate-limits how often a handler can run within a sliding time window, independent of player.

Parameters:

* `calls`: maximum allowed calls per window
* `perSeconds`: window length in seconds

Behavior:

* Skips execution once the call limit for the current window is exceeded

---

### `@Permission`

Requires the player associated with the event to hold a specific permission.

Parameters:

* `permission`: the permission node to check

---

### `@Gamemode`

Requires the player to be in a specific game mode.

Parameters:

* `value`: the required `GameMode`

---

### `@Holding`

Requires the player to be holding a specific item in their main hand.

Parameters:

* `value`: the required `Material`

---

### `@IsEntityType`

Requires the event's entity to be of a specific type.

Parameters:

* `value`: the required `EntityType`

---

### `@Log`

Logs information about the event when the handler runs.

Parameters:

* `playerName`, `eventName`, `location`, `blockType`, `id`: toggle which details are logged (booleans)
* `message`: additional custom text appended to the log line

---

### `@Delay`

Delays execution of the handler by a number of ticks.

Parameters:

* `ticks`: delay before the method is invoked

Can be combined with `@Async` to delay asynchronous execution as well.

---

### `@Inject` (parameter annotation)

Purely documentational marker for an injected parameter. Resolution is handled entirely by the registered `ParameterResolver`s — the annotation itself carries no logic.

---

### `@PlayersNearby` (parameter annotation)

Injects a `List<Player>` of players near the entity/player associated with the event.

Parameters:

* `radius`: search radius in blocks
* `minPlayers`: minimum number of nearby players required; if not met, an empty list is injected instead

---

## Player Access

Instead of manually extracting the player from each event, use:

\`\`\`
getPlayer()
\`\`\`

The framework automatically resolves the player when possible.

---

## Custom Annotations

The framework is designed to be extensible via custom annotations.

### Steps

1. Define a custom annotation
2. Implement an `AnnotationHandler`
3. Register the handler using:

\`\`\`
AnnotationRegistry
\`\`\`

---

## Example

A complete usage example demonstrating all features is available in:

\`\`\`
de.moritzmcc.example
\`\`\`

---

## Execution Flow

\`\`\`
Event Fired
   ↓
Find Matching Methods
   ↓
Process Annotations
   ↓
All Conditions Passed?
   ↓ yes
Execute Method (sync/async)
\`\`\`

---

## Important Notes

* Handlers are matched by type hierarchy: a handler declared for an abstract/parent event type also runs for its subtypes
* Async handlers must be thread-safe
* Exceptions inside handlers are caught and logged
* Each handler method must have exactly one `Event` parameter; further parameters are resolved via `ParameterResolver`s

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
