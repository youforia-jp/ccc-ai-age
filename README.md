# CC:C AI Age

> **Build `0.33`** — Phase 1, 2, 3, & 4 complete: Kinetic AI Core block + CC:T peripheral + Ollama Link + Create Integration + 3-Tier Core System + Background Setup Handler + Lua Script Mounting

A Minecraft 1.20.1 Fabric mod that bridges **CC: Tweaked** computers with **Create**'s kinetic network through an AI-powered peripheral, connecting Lua scripting with a local [Ollama](https://ollama.com) LLM backend.

---

## ✨ Features (v0.33)

| Feature | Status | Description |
|---|---|---|
| **Custom Creative Tab** | ✅ Implemented | Mod items are available under the CC:C AI Age creative tab. |
| **3-Tier Block Progression** | ✅ Implemented | Progression system featuring Basic (requires >= 32 RPM, uses `qwen:0.5b`), Advanced (requires >= 16 RPM), and Quantum (requires 0 RPM / self-powered) cores. |
| **CC:T Peripheral Discovery** | ✅ Implemented | Discovered adjacent to computers using `peripheral.find("ai_core")`. |
| **Ollama Async Streaming** | ✅ Implemented | Calls local Ollama `http://localhost:11434/api/generate` asynchronously and streams NDJSON responses. |
| **Background Setup Handler** | ✅ Implemented | Non-blocking startup handler that checks if Ollama is online, launches `ollama serve` if offline, and pre-pulls `qwen:0.5b` asynchronously. |
| **Lua Script Injection** | ✅ Implemented | Automatically mounts `assets/ccc-ai-age/lua/` as a read-only directory `"ai"` on the root of any connected computer. |
| **Create Kinetic Integration** | ✅ Implemented | Scans adjacent block entities for speed/stress metrics and exposes them to Lua. |

---

## 🏗️ Development Roadmap

### Phase 1 — Block & Registration ✅
- `KineticAICoreBlock` — extends `Block`, implements `BlockEntityProvider`.
- `ModBlocks` — registers block + `BlockItem` into the Redstone creative tab.
- `ModBlockEntities` — registers `BlockEntityType<KineticAICoreBlockEntity>`.
- Resource files: lang, blockstates, models, loot table, pickaxe tags.

### Phase 2 — CC: Tweaked Peripheral ✅
- `KineticAICoreBlockEntity` peripheral interface is delegated to an inner class `KineticAICorePeripheral` to avoid class inheritance/method clashes (specifically `getType()`).
- Peripheral type: `"ai_core"`.

### Phase 3 — Ollama HTTP Streaming ✅
- Asynchronous Java HTTP client calling `http://localhost:11434/api/generate`.
- Token-by-token streaming pushed back to Lua via `ai_token` events.
- Per-computer request lifecycle cancellation (cleanup on computer detaching or block breaking).

### Phase 4 — Create Kinetic Integration & 3-Tier System ✅
- Reads Create shaft speed/stress from adjacent kinetic network via Java reflection.
- Exposes metrics via `getKineticData()` Lua method.
- Enforces 3 tiers of core blocks:
  - **Basic:** Requires `>= 32 RPM` to run. Overrides prompt generation to force `qwen:0.5b` and injects primitive matrix behavior instructions.
  - **Advanced:** Enforces `>= 16 RPM` and standard Ollama request configurations.
  - **Quantum:** Requires `0 RPM` / self-powered, bypassing all rotational checks.

### Phase 5 — Background Setup Handler & Mounting ✅
- `OllamaSetupHandler` — runs background startup checks to see if Ollama is online, starts it if missing, and pre-pulls `qwen:0.5b`.
- Automatic resource mounting — mounts read-only directory `ai` onto the computer when it attaches, unmounts it on detaching.

---

## 📦 Dependencies

| Mod | Version | Required? |
|---|---|---|
| Minecraft | 1.20.1 | ✅ |
| Fabric Loader | ≥ 0.19.3 | ✅ |
| Fabric API | 0.92.9+1.20.1 | ✅ |
| CC: Tweaked | 1.116.1 (Fabric) | ✅ |
| Create Fabric | 0.5.1-j | Suggested / Required for Kinetics |
| CC:C Bridge | 1.70 | Suggested |

---

## 🔧 Setup

### Prerequisites
- JDK 17+
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

## 🧪 Testing the Peripheral (in-game)

1. Place any **Kinetic AI Core** block (Basic, Advanced, or Quantum).
2. Connect rotational power if using Basic or Advanced (minimum 32 RPM for Basic, 16 RPM for Advanced).
3. Place a **CC: Tweaked Computer** adjacent to it.
4. The directory `/ai/` will be automatically mounted. Run the script:
   ```bash
   ai/stream "Write a haiku about gears"
   ```
   This will call the adjacent peripheral and stream the response directly on the computer screen!

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
└── integration/
    └── CCTweakedPlugin.java             CC:T "computercraft" entrypoint plugin
```

---

## 📜 License

CC0-1.0 — public domain. Feel free to learn from and build on this.
