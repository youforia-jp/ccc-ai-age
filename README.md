# CC:C AI Age

> **Build `0.1`** — Phase 1 & 2 complete: Kinetic AI Core block + CC: Tweaked peripheral

A Minecraft 1.20.1 Fabric mod that bridges **CC: Tweaked** computers with **Create**'s kinetic network through an AI-powered peripheral, with a planned connection to a local [Ollama](https://ollama.com) LLM backend.

---

## ✨ Features (v0.1)

| Feature | Status |
|---|---|
| **Kinetic AI Core** block (brass-tier, pickaxe required) | ✅ Implemented |
| Block Entity with CC: Tweaked peripheral registration | ✅ Implemented |
| `peripheral.find("ai_core")` discoverable by CC computers | ✅ Implemented |
| `streamTelemetry()` Lua method (stub — returns status string) | ✅ Implemented |
| Async Ollama HTTP streaming (token-by-token to Lua coroutine) | 🔜 Phase 3 |
| Create kinetic integration (speed/stress sensors) | 🔜 Phase 4 |

---

## 🏗️ Development Roadmap

### Phase 1 — Block & Registration ✅
- `KineticAICoreBlock` — extends `Block`, implements `BlockEntityProvider`
- `ModBlocks` — registers block + `BlockItem` into the Redstone creative tab
- `ModBlockEntities` — registers `BlockEntityType<KineticAICoreBlockEntity>`
- Resource files: blockstates, models, lang, loot table, pickaxe mining tag

### Phase 2 — CC: Tweaked Peripheral ✅
- `KineticAICoreBlockEntity` implements `IPeripheral` directly
- Peripheral type: `"ai_core"` (find with `peripheral.find("ai_core")`)
- `streamTelemetry()` → stub returning `"AI Core Online: Standing by for Ollama link."`
- `CCTweakedPlugin` registered via the `"computercraft"` Fabric entrypoint

### Phase 3 — Ollama HTTP Streaming 🔜
- Java async HTTP client calling `http://localhost:11434/api/generate`
- Token-by-token streaming pushed back to Lua via `os.pullEvent("ai_token")`
- Per-computer request lifecycle (attach/detach, cancellation)

### Phase 4 — Create Kinetic Integration 🔜
- Read Create shaft speed/stress from adjacent kinetic network
- Expose via `getKineticData()` Lua method
- Kinetic power requirement for AI queries

---

## 📦 Dependencies

| Mod | Version | Required? |
|---|---|---|
| Minecraft | 1.20.1 | ✅ |
| Fabric Loader | ≥ 0.19.3 | ✅ |
| Fabric API | 0.92.9+1.20.1 | ✅ |
| CC: Tweaked | 1.116.1 (Fabric) | ✅ |
| Create Fabric | 0.5.1-j | Suggested |
| CC:C Bridge | 1.70 | Suggested |

> **Note:** Versions are subject to change as compatibility is tested. CC:C Bridge is optional — the peripheral core works with vanilla CC: Tweaked.

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
./gradlew runServer   # Dedicated server
```

### CC:C Bridge dependency note
CC:C Bridge is listed as a `modCompileOnly` dep via CurseMaven. If CurseMaven fails to resolve it (author may disable 3rd-party sharing), download the jar from [Modrinth](https://modrinth.com/mod/cccbridge) or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/ccc-bridge), place it in a `libs/` folder, and switch to the local-jar option commented out in `build.gradle`.

---

## 🧪 Testing the Peripheral (in-game)

1. Place a **Kinetic AI Core** block (Redstone creative tab)
2. Place a **CC: Tweaked Computer** adjacent to it
3. Open the computer and run:

```lua
local core = peripheral.find("ai_core")
if core then
    print(core.streamTelemetry())
else
    print("No AI Core found!")
end
```

Expected output: `AI Core Online: Standing by for Ollama link.`

---

## 📁 Package Structure

```
src/main/java/net/ccc_ai_age/
├── CCCAIAge.java                        Main mod initializer
├── ModBlocks.java                       Block + BlockItem registry
├── ModBlockEntities.java                BlockEntityType registry
├── block/
│   └── KineticAICoreBlock.java          Block class (BlockEntityProvider)
├── blockentity/
│   └── KineticAICoreBlockEntity.java    BlockEntity + IPeripheral + @LuaFunction
└── integration/
    └── CCTweakedPlugin.java             CC:T "computercraft" entrypoint plugin
```

---

## 📜 License

CC0-1.0 — public domain. Feel free to learn from and build on this.
