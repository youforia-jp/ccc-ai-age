-- Interactive Exception-Driven AI Recovery Matrix for Turtles
local core = peripheral.find("ai_core")
local selectedModel = "qwen3.5:9b" -- Global selection cache

-- High-visibility industrial panic flashing routine
local function flashPanicAlarm()
    local oldBg = term.getBackgroundColor()
    local oldText = term.getTextColor()
    
    for i = 1, 2 do
        term.setBackgroundColor(colors.red)
        term.clear()
        sleep(0.25)
        term.setBackgroundColor(colors.black)
        term.clear()
        sleep(0.25)
    end
    
    term.setBackgroundColor(colors.black)
    term.setTextColor(colors.white)
    term.clear()
    term.setCursorPos(1, 1)
    print("=========================================")
    print("      [ALERT] RECOVERY NODE ENGAGED       ")
    print("=========================================")
    print("")
    print("[STATUS] Querying base station AI Core...")
end

-- Telemetry compiler routines
local function getInventorySummary()
    local summary = {}
    for slot = 1, 16 do
        local count = turtle.getItemCount(slot)
        if count > 0 then
            local detail = turtle.getItemDetail(slot)
            local name = detail and detail.name or "unknown"
            summary[#summary + 1] = string.format("Slot %d: %dx %s", slot, count, name)
        end
    end
    if #summary == 0 then
        return "empty"
    else
        return table.concat(summary, ", ")
    end
end

local function getSurroundingsSummary()
    local s = {}
    
    local ok, frontDetail = turtle.inspect()
    s[#s + 1] = "Front: " .. (ok and frontDetail.name or "air")
    
    local okUp, upDetail = turtle.inspectUp()
    s[#s + 1] = "Up: " .. (okUp and upDetail.name or "air")
    
    local okDown, downDetail = turtle.inspectDown()
    s[#s + 1] = "Down: " .. (okDown and downDetail.name or "air")
    
    return table.concat(s, ", ")
end

-- Aggressive AI Response Sanitizer Engine
local function cleanAIResponse(buffer)
    -- 1. Extract markdown code blocks with or without 'lua' identifier (case-insensitive)
    local code = buffer:match("```[lL][uU][aA]%s*(.-)%s*```")
    if not code then
        code = buffer:match("```%s*(.-)%s*```")
    end
    if not code then
        code = buffer
    end
    
    -- 2. Strip raw backticks
    code = code:gsub("`", "")
    
    -- 3. Strip trailing/leading carriage returns
    code = code:gsub("\r", "")
    
    return code
end

-- Main recovery processor
local function triggerAIRecovery(predicament)
    if not core then
        print("\n[Error]: No adjacent or linked AI Core found.")
        return false
    end

    flashPanicAlarm()

    -- Compile prompt context
    local surroundings = getSurroundingsSummary()
    local inventory = getInventorySummary()
    local fuel = turtle.getFuelLevel()
    
    local prompt = string.format(
        "I am a mining turtle stuck while trying to %s. Fuel: %s. Surroundings: %s. Inventory summary: %s. Provide a step-by-step Lua recovery macro to clear this obstacle. Output only the executable code block inside markdown code tags.",
        predicament, tostring(fuel), surroundings, inventory
    )

    -- Stream recovery code
    print("\nSending telemetry context...")
    local ok, reqId = pcall(core.streamTelemetry, prompt, selectedModel)
    if not ok then
        print("[Error]: Failed to contact core: " .. tostring(reqId))
        return false
    end

    local buffer = ""
    while true do
        local event, id, token, done = os.pullEvent("ai_token")
        if id == reqId then
            if done then
                buffer = buffer .. token
                break
            else
                buffer = buffer .. token
            end
        end
    end

    -- Clean and compile recovery logic
    local code = cleanAIResponse(buffer)

    print("\n--- Executing Recovery Macro ---")
    local fn, err = load(code, "recovery_macro", "t", _ENV)
    if fn then
        local success, runErr = pcall(fn)
        if success then
            print("Recovery successful!")
            sleep(1.5)
            return true
        else
            print("Macro execution failed: " .. tostring(runErr))
            sleep(2)
            return false
        end
    else
        print("Macro compilation failed: " .. tostring(err))
        sleep(2)
        return false
    end
end

-- Movement wrappers
local function safeMove(direction)
    local moved = false
    if direction == "forward" then
        moved = turtle.forward()
    elseif direction == "up" then
        moved = turtle.up()
    elseif direction == "down" then
        moved = turtle.down()
    elseif direction == "back" then
        moved = turtle.back()
    end
    
    if not moved then
        print("[Exception]: Movement blocked " .. direction)
        local recovered = triggerAIRecovery("move " .. direction)
        if recovered then
            if direction == "forward" then return turtle.forward()
            elseif direction == "up" then return turtle.up()
            elseif direction == "down" then return turtle.down()
            elseif direction == "back" then return turtle.back()
            end
        end
        return false
    end
    return true
end

local function safeDig(direction)
    -- Bypass "Digging Air" false-positive panics
    local blockPresent = false
    if direction == "forward" then
        blockPresent = turtle.detect()
    elseif direction == "up" then
        blockPresent = turtle.detectUp()
    elseif direction == "down" then
        blockPresent = turtle.detectDown()
    end

    if not blockPresent then
        return true -- Instantly successful if empty air
    end

    local dug = false
    if direction == "forward" then
        dug = turtle.dig()
    elseif direction == "up" then
        dug = turtle.digUp()
    elseif direction == "down" then
        dug = turtle.digDown()
    end
    
    if not dug then
        print("[Exception]: Dig blocked " .. direction)
        local recovered = triggerAIRecovery("dig " .. direction)
        if recovered then
            if direction == "forward" then return turtle.dig()
            elseif direction == "up" then return turtle.digUp()
            elseif direction == "down" then return turtle.digDown()
            end
        end
        return false
    end
    return true
end

local function safePlace(direction)
    local placed = false
    if direction == "forward" then
        placed = turtle.place()
    elseif direction == "up" then
        placed = turtle.placeUp()
    elseif direction == "down" then
        placed = turtle.placeDown()
    end
    
    if not placed then
        print("[Exception]: Place blocked " .. direction)
        local recovered = triggerAIRecovery("place " .. direction)
        if recovered then
            if direction == "forward" then return turtle.place()
            elseif direction == "up" then return turtle.placeUp()
            elseif direction == "down" then return turtle.placeDown()
            end
        end
        return false
    end
    return true
end

-- Main task loop demo
local function main()
    -- Non-Neural hardware check guard rail
    if not core then
        term.setBackgroundColor(colors.black)
        term.setTextColor(colors.red)
        term.clear()
        term.setCursorPos(1, 1)
        print("=========================================")
        print("      SYSTEM CONFIGURATION ERROR         ")
        print("=========================================")
        print("")
        print("No linked Kinetic AI Core was detected.")
        print("Please upgrade this hardware internally")
        print("by crafting it with a Kinetic AI Core")
        print("block to install the Neural AI chip.")
        print("")
        print("Shutting down...")
        sleep(5)
        return
    end

    -- Core Model Profile Chooser
    term.clear()
    term.setCursorPos(1, 1)
    print("=========================================")
    print("      KINETIC AI RECOVERY ENGINE         ")
    print("=========================================")
    print("")
    print("Select your target GPU performance profile:")
    print("1) qwen:0.5b   (Low VRAM / Basic Baseline)")
    print("2) qwen3.5:4b  (3GB VRAM / Advanced Default)")
    print("3) qwen3:8b    (5GB VRAM / Intermediate Bridge)")
    print("4) qwen3.5:9b  (7GB VRAM / Quantum Flagship)")
    print("5) qwen2.5:14b (12GB VRAM / Quantum High-End)")
    print("")
    write("Choose profile (1-5) [Default 4]: ")
    local input = read()
    if input == "1" then selectedModel = "qwen:0.5b"
    elseif input == "2" then selectedModel = "qwen3.5:4b"
    elseif input == "3" then selectedModel = "qwen3:8b"
    elseif input == "5" then selectedModel = "qwen2.5:14b"
    else selectedModel = "qwen3.5:9b"
    end
    
    term.clear()
    term.setCursorPos(1, 1)
    print("=========================================")
    print("      KINETIC TURTLE MINING NODE         ")
    print("=========================================")
    print("Profile: " .. selectedModel)
    print("Status: Working standard mining route...")
    print("Press Ctrl+T to terminate.")
    print("-----------------------------------------")
    
    while true do
        safeDig("forward")
        safeMove("forward")
        sleep(1.0)
    end
end

main()
