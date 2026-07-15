# CC:C AI Age

> **Build `0.55`** — Phase 6 fully complete: Combo Wireless Modems, proper CC:T turtle upgrade integration (native naming, slots, and left/right rendering), and modernized crafting hooks.

A Minecraft 1.20.1 Fabric mod that bridges **CC: Tweaked** computers with **Create**'s theme through an AI-powered peripheral, connecting Lua scripting with a local [Ollama](https://ollama.com) LLM backend.

---

## ✨ Features (v0.55)

| Feature | Status | Description |
|---|---|---|
| **Custom Creative Tab** | ✅ Implemented | Mod items are available under the CC:C AI Age creative tab, including pre-assembled AI turtles. |
| **3-Tier AI Progression** | ✅ Implemented | Progression system featuring Basic (uses `qwen:0.5b`), Advanced (default configurations), and Quantum cores. |
| **Combo Wireless Modems** | ✅ Implemented | Advanced/Quantum Cores can be crafted with Wireless Modems. Equipping this combo item automatically mounts a CC:T wireless modem to the turtle's invisible `BOTTOM` slot. |
| **Native CC:T Integration** | ✅ Implemented | Turtles use CC:T's native upgrade system (using standard `ITurtleUpgrade`), inheriting proper dynamic naming (e.g. "Mining Advanced AI Turtle") and side-rendering. |
| **Stationary Neural Upgrades** | ✅ Implemented | Standard computers can be upgraded with AI Cores in a crafting grid, converting them into "AI Powered Computers" dynamically. |
| **Ollama Async Streaming** | ✅ Implemented | Calls local Ollama `http://localhost:11434/api/generate` asynchronously and streams NDJSON responses directly to Lua `ai_token` events. |
| **Lua Script Injection** | ✅ Implemented | Mounts a read-only directory `"ai"` on the root of any connected computer, providing out-of-the-box scripts. |
| **Interactive Welcome Screen** | ✅ Implemented | Shows an in-game welcome screen on world load with shortcuts to configuring local model sizes. |

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
