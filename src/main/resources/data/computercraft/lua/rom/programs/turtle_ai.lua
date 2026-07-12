-- Interactive Exception-Driven AI Recovery Matrix for Turtles
local core = peripheral.find("ai_core")

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
    local ok, reqId = pcall(core.streamTelemetry, prompt, "qwen3.5:4b")
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

    -- Extract Lua code block
    local code = buffer:match("```lua%s*(.-)%s*```") or buffer:match("```%s*(.-)%s*```") or buffer

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
    term.clear()
    term.setCursorPos(1, 1)
    print("=========================================")
    print("      KINETIC TURTLE MINING NODE         ")
    print("=========================================")
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
