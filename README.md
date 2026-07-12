# CC:C AI Age

> **Build `0.32`** — Phase 1, 2, 3, & 4 complete: Kinetic AI Core block + CC:T peripheral + Ollama Link + Create Integration + 3-Tier Core System + Run Compatibility

A Minecraft 1.20.1 Fabric mod that bridges **CC: Tweaked** computers with **Create**'s kinetic network through an AI-powered peripheral, connecting Lua scripting with a local [Ollama](https://ollama.com) LLM backend.

---

## ✨ Features (v0.32)

| Feature | Status | Description |
|---|---|---|
| **Custom Creative Tab** | ✅ Implemented | Mod items are available under the CC:C AI Age creative tab. |
| **Kinetic AI Core** block | ✅ Implemented | Brass-tier block (requires pickaxe, metal sounds). |
| **Block Entity Registration** | ✅ Implemented | Linked to `KineticAICoreBlockEntity` type. |
| **CC:T Peripheral Discovery** | ✅ Implemented | Discovered adjacent to computers using `peripheral.find("ai_core")`. |
| **Ollama Async Streaming** | ✅ Implemented | Calls local Ollama `http://localhost:11434/api/generate` asynchronously and streams NDJSON responses. |
| **Create Kinetic Integration** | ✅ Implemented | Scans adjacent block entities for speed/stress metrics and exposes them to Lua. |
| **Kinetic Power Enforcements** | ✅ Implemented | Restricts AI queries. Prompt generation requires a minimum of `16 RPM` and stalled networks (overstressed) block requests. |

---

## 🏗️ Development Roadmap

### Phase 1 — Block & Registration ✅
- `KineticAICoreBlock` — extends `Block`, implements `BlockEntityProvider`.
- `ModBlocks` — registers block + `BlockItem` into the Redstone creative tab.
- `ModBlockEntities` — registers `BlockEntityType<KineticAICoreBlockEntity>`.
- Resource files: blockstates, models, lang, loot table, pickaxe mining tag.

### Phase 2 — CC: Tweaked Peripheral ✅
- `KineticAICoreBlockEntity` peripheral interface is delegated to an inner class `KineticAICorePeripheral` to avoid class inheritance/method clashes (specifically `getType()`).
- Peripheral type: `"ai_core"`.
- `CCTweakedPlugin` registered via the `"computercraft"` Fabric entrypoint.

### Phase 3 — Ollama HTTP Streaming ✅
- Asynchronous Java HTTP client calling `http://localhost:11434/api/generate`.
- Token-by-token streaming pushed back to Lua via `ai_token` events.
- Per-computer request lifecycle cancellation (cleanup on computer detaching or block breaking).

### Phase 4 — Create Kinetic Integration ✅
- Reads Create shaft speed/stress from adjacent kinetic network via Java reflection.
- Exposes metrics via `getKineticData()` Lua method.
- Restricts AI telemetry queries if adjacent network speed is `< 16 RPM` or if the network is overstressed.

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

> **Note:** CC:C Bridge is optional — the peripheral core works with vanilla CC: Tweaked.

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

---

## 🧪 Testing the Peripheral (in-game)

1. Place a **Kinetic AI Core** block.
2. Supply rotational power to it from any adjacent block (minimum 16 RPM).
3. Place a **CC: Tweaked Computer** adjacent to it.
4. Run the following Lua script to test the telemetry data and print a streaming Ollama generation:

```lua
local core = peripheral.find("ai_core")
if not core then
    print("No AI Core found!")
    return
end

-- 1. Query kinetic network status
local data = core.getKineticData()
print("Speed: " .. data.speed .. " RPM")
print("Stress load: " .. data.stressPercent .. "%")
print("Powered: " .. tostring(data.isPowered))
print("Overstressed: " .. tostring(data.isOverstressed))

-- 2. Stream generation from local Ollama (requires Ollama running locally)
print("\nContacting Ollama...")
local ok, reqId = pcall(core.streamTelemetry, "Write a haiku about gears.", "llama3")
if not ok then
    print("Failed: " .. reqId)
    return
end

-- Loop until the end token event is caught
while true do
    local event, id, token, done = os.pullEvent("ai_token")
    if id == reqId then
        if token then
            io.write(token)
        end
        if done then
            print()
            break
        end
    end
end
```

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
│   └── KineticAICoreBlockEntity.java    BlockEntity + KineticAICorePeripheral + Reflection Helpers
└── integration/
    └── CCTweakedPlugin.java             CC:T "computercraft" entrypoint plugin
```

---

## 📜 License

CC0-1.0 — public domain. Feel free to learn from and build on this.
