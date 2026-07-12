# CC:C AI Age

> **Build `0.50`** — Phase 5 fully complete: Kinetic AI Core block, CC:T peripheral, Ollama Link, Neural Hardware Integration (NBT/Recipe upgrades), Dynamic Block Entity Persistence, Config welcome screen, and environment test ROM utilities.

A Minecraft 1.20.1 Fabric mod that bridges **CC: Tweaked** computers with **Create**'s theme through an AI-powered peripheral, connecting Lua scripting with a local [Ollama](https://ollama.com) LLM backend.

---

## ✨ Features (v0.50)

| Feature | Status | Description |
|---|---|---|
| **Custom Creative Tab** | ✅ Implemented | Mod items are available under the CC:C AI Age creative tab with the exception of neural items (must be crafted by combining AI chip with Computer / Turtle). |
| **3-Tier AI Progression** | ✅ Implemented | Progression system featuring Basic (uses `qwen:0.5b`), Advanced (default configurations), and Quantum cores. |
| **Decoupled Kinetic Network** | ✅ Implemented | Blocks are self-powered, resolving previous kinetic constraints and RPM stress requirements for a clean architecture. |
| **Neural Hardware Upgrades** | ✅ Implemented | Upgrades standard computers/turtles with any AI Core block in a crafting grid, writing `NeuralAI` and `NeuralTier` NBT tags. |
| **Name Localization** | ✅ Implemented | Prepends `"Neural "` to the display name of upgraded item stacks dynamically. |
| **Virtual Peripheral Hooking** | ✅ Implemented | Registers a virtual `ai_core` peripheral on the `BOTTOM` virtual side for Neural machines. Physical slots remain 100% open. |
| **Block Placement & Break Persistence** | ✅ Implemented | Mixins preserve custom `NeuralAI` NBT tags across block placement and mining/breaking lifecycle events. |
| **Ollama Async Streaming** | ✅ Implemented | Calls local Ollama `http://localhost:11434/api/generate` asynchronously and streams NDJSON responses. |
| **Lua Script Injection** | ✅ Implemented | Mounts read-only directory `"ai"` on the root of any connected computer. |
| **Interactive Welcome Screen** | ✅ Implemented | Shows an in-game welcome screen on world load, with an "Open Config" shortcut to open the configuration JSON. |

---

## 🏗️ Development Roadmap

### Phase 1 — Block & Registration ✅
- `KineticAICoreBlock` — extends `Block`, implements `BlockEntityProvider`.
- `ModBlocks` — registers block + `BlockItem` into the Redstone creative tab.
- `ModBlockEntities` — registers `BlockEntityType<KineticAICoreBlockEntity>`.

### Phase 2 — CC: Tweaked Peripheral ✅
- Peripheral type: `"ai_core"`.

### Phase 3 — Ollama HTTP Streaming ✅
- Asynchronous Java HTTP client calling `http://localhost:11434/api/generate`.
- Token-by-token streaming pushed back to Lua via `ai_token` events.

### Phase 4 — Decoupled System ✅
- Exposes metrics via `getKineticData()` Lua method with mock telemetry.
- Enforces 3 tiers of core blocks (Basic, Advanced, Quantum) without rotational RPM speed restrictions.

### Phase 5 — Neural Hardware & Interactive Configuration ✅
- **Shapeless Crafting Recipe:** Craft standard CC:T computers, pocket computers, and turtles with AI Cores to create Neural versions.
- **Persistent Placement and Breaking:** Mixins target block placement and drop generation to ensure neural upgrades are never lost.
- **Client Welcome Screen:** Launches a customized screen upon world load to configure mod parameters directly.
- **LUA ROM Utilities:** Mounted `turtle_mining_ai.lua` for exception recovery mining and `mining_test.lua` for static path testing.

---

## 📦 Dependencies

| Mod | Version | Required? |
|---|---|---|
| Minecraft | 1.20.1 | ✅ |
| Fabric Loader | ≥ 0.19.3 | ✅ |
| Fabric API | 0.92.9+1.20.1 | ✅ |
| CC: Tweaked | 1.116.1 (Fabric) | ✅ |

---

## 🔧 Setup

### Prerequisites
- JDK 17 or JDK 21
- [Fabric development environment](https://docs.fabricmc.net/develop/getting-started/creating-a-project#setting-up)

### Building
```bash
./gradlew build
```
Output jar: `build/libs/ccc-ai-age-1.0.0.jar`

### Running in dev
```bash
./gradlew runClient   # Minecraft client with mod loaded
```

---

## 🧪 Testing the Neural Upgrade (in-game)

1. Combine an **Advanced Turtle** with a **Any Kinetic AI Core** in a crafting table to produce a **Neural Advanced Turtle**.
2. Place the Neural Advanced Turtle down.
3. Open its terminal. The `/ai/` ROM directory is automatically mounted.
4. Run the test script:
   ```bash
   mining_test
   ```
   This executes an infinite dig-and-move routine tracking your coordinate progress!
5. Or run the AI mining recovery script:
   ```bash
   turtle_mining_ai
   ```
   It automatically communicates with the virtual bottom peripheral `ai_core` to stream mining commands.

---

## 📁 Package Structure

```
src/main/java/net/ccc_ai_age/
├── CCCAIAge.java                        Main mod initializer
├── ModBlocks.java                       Block + BlockItem registry
├── ModBlockEntities.java                BlockEntityType registry
├── ModItemGroups.java                   Creative Tab registry
├── OllamaSetupHandler.java              Background Ollama setup and model pre-pull helper
├── api/
│   └── AITier.java                      Core tier enum (BASIC, ADVANCED, QUANTUM)
├── block/
│   └── KineticAICoreBlock.java          Block class with tier properties
├── blockentity/
│   └── KineticAICoreBlockEntity.java    BlockEntity + KineticAICorePeripheral + Reflection Helpers
├── recipe/
│   └── NeuralTurtleRecipe.java          Shapeless Neural Upgrade crafting handler
├── client/
│   ├── CCCAIAgeClient.java              Client entrypoint
│   └── ConfigNotificationScreen.java    Interactive Welcomer / Config launcher screen
├── mixin/
│   ├── AbstractComputerBlockMixin.java  Block placement state preservation mixin
│   ├── AbstractComputerBlockEntityMixin.java Base NBT load/save Mixin
│   ├── TurtleBlockMixin.java            Turtle break drop preservation mixin
│   ├── ComputerBlockMixin.java          Computer break drop preservation mixin
│   ├── BlockItemMixin.java              ItemStack block item placement state copier
│   ├── NeuralComputerMixin.java         Computer virtual bottom peripheral registry
│   ├── NeuralTurtleMixin.java           Turtle virtual bottom peripheral registry
│   ├── TurtleBrainMixin.java            Turtle peripheral updates interceptor
│   └── ItemStackMixin.java              Dynamically prefixes Neural to upgraded items
└── integration/
    └── CCTweakedPlugin.java             CC:T "computercraft" entrypoint plugin
```

---

## 📜 License

CC0-1.0 — public domain. Feel free to learn from and build on this.
