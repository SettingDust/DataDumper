# DataDumper — AGENTS.md

## Project Overview

DataDumper is a cross-platform, cross-version Minecraft mod that provides registry data export functionality for datapack developers. Core features:

- Export registry keys and entry JSON via the in-game command `/datadumper`, with regex and tag filtering support
- Automatically export JSON resource files that fail to parse, for easier debugging
- Prevent world creation crashes caused by invalid datapacks

Mod ID: `data_dumper`, root package: `settingdust.data_dumper`.

## Architecture

### Build Framework

The project uses [Cloche](https://github.com/terrarium-earth/cloche) (`earth.terrarium.cloche`) as the Gradle build framework, providing unified management of multi-loader (Fabric / Forge / NeoForge) and multi-version (1.20.1 / 1.21.1 / 26.1.2) compilation, mapping, and packaging. Cloche version is `0.18.11-dust.16` (custom fork).

Other key Gradle plugins:
- `kotlin("jvm")` + `kotlin("plugin.serialization")` — Kotlin 2.3.20
- `com.palantir.git-version` — Version numbers generated from git tags
- `com.gradleup.shadow` — Fat JAR packaging with service file merging

### Source Directory Structure

```
src/
├── common/                      # Cross-platform shared code (no loader dependencies)
│   ├── common/                  # Shared across all versions: SPI interfaces, command logic, utilities
│   │   ├── main/kotlin/         # Core business logic (Commands.kt, DataDumper.kt, etc.)
│   │   └── test/kotlin/         # GameTest shared interface (DataDumperTests.kt)
│   ├── 20.1/                    # Minecraft 1.20.1 version-specific implementation
│   │   ├── main/kotlin/         # SPI implementation classes
│   │   ├── main/java/           # Mixin classes
│   │   └── main/resources/      # META-INF/services registration + Mixin config
│   ├── 21.1/                    # Minecraft 1.21.1 version-specific implementation
│   └── 26.1/                    # Minecraft 26.1.2 version-specific implementation
├── fabric/                      # Fabric loader-specific code
│   ├── common/main/             # Fabric shared code (entrypoints, LoaderAdapter, etc.)
│   ├── 20.1/                    # Fabric 1.20.1 (includes main + test)
│   ├── 21.1/                    # Fabric 1.21.1
│   └── 26.1/                    # Fabric 26.1.2
├── forge/game/                  # Forge loader-specific code (1.20.1 only)
│   ├── main/
│   └── test/
└── neoforge/game/               # NeoForge loader-specific code
    ├── common/main/             # NeoForge shared SPI interfaces
    ├── 21.1/main/               # NeoForge 1.21.1 implementation
    └── 26.1/main/               # NeoForge 26.1.2 implementation
```

### Version Layering

- **common/common** — Fully version-agnostic code: SPI interface definitions, command tree construction, export logic
- **common/{20.1, 21.1, 26.1}** — Version-specific SPI implementations + Mixin classes
- **{fabric, forge, neoforge}/common** — Loader shared code (entrypoint registration, loader-specific SPI interfaces)
- **{fabric, forge, neoforge}/{version}** — Loader + version-specific implementations and tests

Dependency chain: `common/common` ← `common/{version}` ← `{loader}/{version}`

### SPI (ServiceLoader) Cross-Version Abstraction

The project uses `java.util.ServiceLoader` to achieve cross-version API abstraction. All version differences are shielded through SPI interfaces:

**SPI interface definition location**: `src/common/common/main/kotlin/settingdust/data_dumper/util/`

Existing SPI interfaces (shared):
| Interface | Responsibility |
|-----------|---------------|
| `IdentifierFactory` | Create/parse `CommonIdentifier` (ResourceLocation/Identifier) |
| `RegistryAccessAdapter` | Version differences in `RegistryAccess.lookup` method |
| `RegistryAdapter` | Version differences in `Registry.getTags` / `getOrCreateTag` |
| `ResourceKeyAdapter` | Version differences in `ResourceKey.createRegistryKey` / `location()` |
| `TagKeyAdapter` | Version differences in `TagKey.create` / `TagKey.of` |
| `ArgumentTypeAdapter` | Version differences in `ResourceLocationArgument.id()` / `IdentifierArgumentType.identifier()` |
| `ComponentEventAdapter` | Version differences in `HoverEvent` / `ClickEvent` construction |
| `DataResultAdapter` | Version differences in `DataResult.getOrThrow` signature |
| `SuggestResourceProvider` | Version differences in `SharedSuggestionProvider.suggestResource` |
| `LoaderAdapter` | Loader-related operations (gameDir, isClient, isModLoaded) |
| `MinecraftVersionNameProvider` | Get current MC version name |
| `MinecraftAdapter` | Minecraft instance abstraction |
| `Entrypoint` | Cross-loader entrypoint lifecycle |

**Loader-specific SPI**:
- **Fabric**: `ArgumentTypeRegistryAdapter` (defined under `src/fabric/`, Fabric-only)
- **NeoForge**: `NeoForgeAdapter`, `PermissionAdapter` (defined in `src/neoforge/game/common/`, NeoForge-only)

### Mixin — CommonIdentifier Interface Injection

`CommonIdentifier` is the unified resource identifier interface, injected directly into Minecraft's native classes via Mixin:
- 1.20.1 / 1.21.1: `ResourceLocationMixin implements CommonIdentifier` → injected into `ResourceLocation`
- 26.1: `IdentifierMixin implements CommonIdentifier` → injected into `Identifier`

This way, `ResourceLocation` / `Identifier` instances are directly `CommonIdentifier` at runtime, with no wrapping needed.

Other Mixins:
- `CreateWorldScreenMixin` — Prevents crashes caused by invalid datapacks
- `RegistryDataLoaderMixin` / `RegistryLoadTaskPendingRegistrationMixin` — Captures and exports registry entries that fail to parse

### Container Packaging

The final artifact is a universal JAR embedding all loader and version sub-JARs:
- Fabric container — contains fabric:20.1 / 21.1 / 26.1 include jars
- Forge container — contains forge:game include jar
- NeoForge container — first merges neoforge:game:21.1 + 26.1, then packages into the container
- Finally, `shadowMergedJar` merges all three containers into a single release JAR

## Build Commands

```bash
# Full build (compile + package all platforms and versions)
./gradlew build

# Compile check only (no packaging)
./gradlew classes

# Run Fabric 1.20.1 client
./gradlew :versionFabric201Client

# Run GameTest (Fabric 1.20.1)
./gradlew :fabric201RunGameTest
```

> **Note**: Cloche auto-generates target-related task names in the format `{featureName}{TaskType}`. You can view specific task names via `./gradlew tasks`.

## Development Workflow

### Adding a New SPI Interface

1. **Define the interface** — Create the interface file in `src/common/common/main/kotlin/settingdust/data_dumper/util/`:
   ```kotlin
   interface MyAdapter {
       companion object : MyAdapter by ServiceLoaderUtil.findService()
       
       fun someMethod(): SomeType
   }
   ```
   - companion object delegates via `by ServiceLoaderUtil.findService()`
   - Interface methods prefer extension function style (`fun ReceiverType.method()`)

2. **Version implementation** — Create the implementation class under `src/common/{version}/main/kotlin/`:
   ```kotlin
   class MyAdapterImpl : MyAdapter {
       override fun someMethod(): SomeType = // version-specific implementation
   }
   ```

3. **Register the service** — Create a file under `src/common/{version}/main/resources/META-INF/services/` for each version:
   - Filename: `settingdust.data_dumper.util.MyAdapter`
   - Content: `settingdust.data_dumper.v{version}.util.MyAdapterImpl`

4. **Loader-specific SPI** — If the SPI is only used by a specific loader:
   - **Fabric**: Interface defined in `src/fabric/common/main/`, implementation in `src/fabric/{version}/`
   - **NeoForge**: Interface defined in `src/neoforge/game/common/main/`, implementation in `src/neoforge/game/{version}/`
   - Do not place loader-specific SPI in `src/common/`

### Adding a New Mixin

1. Create the Mixin class (Java) under the corresponding version directory:
   - `src/common/{version}/main/java/settingdust/data_dumper/v{version_underscore}/mixin/`
2. Register it in the corresponding version's Mixin config JSON:
   - `src/common/{version}/main/resources/data_dumper.{version_underscore}.mixins.json`
3. Verify that `mixins.from(...)` in `build.gradle.kts` includes the config

### Module Layering Rules

- **Shared SPI interfaces** (across all loaders and versions) → `src/common/common/`
- **Loader shared SPI** → `src/{loader}/common/`
- **NeoForge-specific SPI** → Interface in `src/neoforge/game/common/`, implementation in `src/neoforge/game/{version}/`
- **Fabric-specific SPI** → Interface in `src/fabric/common/`, implementation in `src/fabric/{version}/`
- SPI interfaces follow the **single responsibility principle** — each interface abstracts only one version difference

## Testing

### GameTest Architecture

Tests use the Minecraft GameTest framework with a **common interface + platform delegation** architecture:

1. **Shared test interface**: `src/common/common/test/kotlin/settingdust/data_dumper/test/DataDumperTests.kt`
   - Defined as `interface DataDumperTests` with default implementations for all test methods
   - Uses `companion object : DataDumperTests` for static access
   - Tests cover all commands: registries, registry, registry+tag, registry+regex, entry, entry+tag, entry+regex

2. **Platform delegation**: Each Fabric version creates a GameTest class under `src/fabric/{version}/test/`:
   ```kotlin
   class DataDumperGameTest : FabricGameTest, DataDumperTests {
       @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
       override fun testRegistriesCommand(helper: GameTestHelper) = super.testRegistriesCommand(helper)
       // ... other test methods follow the same pattern
   }
   ```
   - Implements `FabricGameTest` (or corresponding platform interface) + `DataDumperTests`
   - Each method override adds the `@GameTest` annotation and delegates to `super`

3. **Fabric GameTest configuration**: Must explicitly declare the `fabric-gametest` entrypoint in the Fabric target metadata in `build.gradle.kts`:
   ```kotlin
   entrypoint("fabric-gametest") {
       adapter = "kotlin"
       value = "$group.fabric.v{version}.test.DataDumperGameTest"
   }
   ```

All test logic is kept in `src/common/common/test/`; platform directories only contain bridging code.

## Code Style

- **Languages**: Kotlin (business logic) + Java (Mixin classes)
- **Formatting**: ktfmt style, see `.editorconfig`
  - Indentation: 4 spaces
  - Max line width: 100 characters
  - Star imports disabled (`name_count_to_use_star_import = 9999`)
  - Trailing commas allowed
- **SPI interface conventions**:
  - companion object delegates via `by ServiceLoaderUtil.findService()`, allowing the interface itself to be used as a singleton
  - Methods prefer extension function style: `fun ReceiverType.method()` over `fun method(receiver: ReceiverType)`
  - companion object may include convenience extensions (e.g., `SuggestResourceProvider`'s Iterable overload)
- **Package naming**: Version implementation classes go in `v{version_underscore}` sub-packages (e.g., `v20_1`, `v21_1`, `v26_1`)

## Key Directories

| Path | Description |
|------|-------------|
| `src/common/common/main/kotlin/settingdust/data_dumper/` | Core business: commands, exporters, argument types |
| `src/common/common/main/kotlin/settingdust/data_dumper/util/` | SPI interface definitions + utilities |
| `src/common/{20.1,21.1,26.1}/main/` | Version-specific SPI implementations + Mixins |
| `src/fabric/common/main/` | Fabric shared code (entrypoints, LoaderAdapter) |
| `src/fabric/{20.1,21.1,26.1}/` | Fabric version implementations + GameTest |
| `src/forge/game/` | Forge 1.20.1 implementation |
| `src/neoforge/game/common/main/` | NeoForge shared SPI interfaces |
| `src/neoforge/game/{21.1,26.1}/main/` | NeoForge version implementations |
| `.github/workflows/` | CI: build.yml (build), publish.yml (publish to Modrinth/CurseForge) |

## Common Patterns

### SPI Abstraction Pattern

```kotlin
// 1. Interface definition (src/common/common/)
interface FooAdapter {
    companion object : FooAdapter by ServiceLoaderUtil.findService()
    
    fun Bar.doSomething(): Baz
}

// 2. Usage — call directly via companion import
import settingdust.data_dumper.util.FooAdapter.Companion.doSomething

val result = bar.doSomething()  // Transparently delegates to version implementation

// 3. Version implementation (src/common/20.1/)
class FooAdapterImpl : FooAdapter {
    override fun Bar.doSomething(): Baz = bar.versionSpecificMethod()
}

// 4. Service registration (src/common/20.1/main/resources/META-INF/services/settingdust.data_dumper.util.FooAdapter)
settingdust.data_dumper.v20_1.util.FooAdapterImpl
```

### CommonIdentifier Mixin Pattern

```java
// src/common/20.1/main/java/.../mixin/ResourceLocationMixin.java
@Mixin(ResourceLocation.class)
public abstract class ResourceLocationMixin implements CommonIdentifier {
    // ResourceLocation already has namespace + path fields/methods; Mixin automatically satisfies the interface contract
}
```

### GameTest Interface Delegation Pattern

```kotlin
// 1. Shared interface (src/common/common/test/)
interface DataDumperTests {
    companion object : DataDumperTests
    fun testFoo(helper: GameTestHelper) { /* default implementation */ }
}

// 2. Platform bridge (src/fabric/{version}/test/)
class DataDumperGameTest : FabricGameTest, DataDumperTests {
    @GameTest(template = FabricGameTest.EMPTY_STRUCTURE)
    override fun testFoo(helper: GameTestHelper) = super.testFoo(helper)
}
```

## Important Notes

1. **Do not proactively run build verification** — Unless the user explicitly requests it, do not execute `./gradlew build` or similar build commands. The project build depends on the complex Cloche framework and multi-version Minecraft dependencies, which may not fully build in a local environment.
2. **SPI interface single responsibility** — Each SPI interface should abstract only one version difference. Do not merge multiple unrelated version differences into the same interface.
3. **Loader-specific SPI layering** — NeoForge-specific SPI goes in `src/neoforge/game/common/`, Fabric-specific SPI goes in `src/fabric/common/`. Do not pollute the shared SPI space in `src/common/`.
4. **Mixins use Java** — All Mixin classes must be written in Java (SpongePowered Mixin framework requirement); all other business code uses Kotlin.
5. **Version naming convention** — Versions use underscores in code (`v20_1`, `v21_1`, `v26_1`) and dots in directories and configs (`20.1`, `21.1`, `26.1`).
6. **Java version** — Build uses Java 25 (CI), project targets Java 17+.
7. **Dependency management** — Managed via `versionCatalogs` DSL in `settings.gradle.kts`; all third-party dependencies are referenced through `catalog.xxx`.
8. **Copier template** — The project uses a copier template; template updates must be performed manually by the user.
