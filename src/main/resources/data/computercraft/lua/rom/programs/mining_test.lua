-- Modern Ore Miner with Vein Mining & Return Home Logic
-- Place fuel (like charcoal) in SLOT 16
-- Place a chest below or behind the starting position to unload mined items automatically.

local distance = 75 -- 75 blocks forward per run
local runIndex = 0

local function loadState()
    if fs.exists("mining_state.txt") then
        local file = fs.open("mining_state.txt", "r")
        if file then
            local data = file.readLine()
            file.close()
            local idx = tonumber(data)
            if idx then
                print("Resuming from saved runIndex: " .. idx)
                return idx
            end
        end
    end
    print("No saved state found. Starting from runIndex: 0")
    return 0
end

local function saveState(idx)
    local file = fs.open("mining_state.txt", "w")
    if file then
        file.writeLine(tostring(idx))
        file.close()
    end
end

local function shouldMine(blockName)
    if not blockName then return false end
    local name = string.lower(blockName)
    -- Exclude coal and copper
    if string.find(name, "coal") or string.find(name, "copper") then
        return false
    end
    if string.find(name, "ore") then
        return true
    end
    return false
end

local function isInventoryFull()
    for slot = 1, 15 do
        if turtle.getItemCount(slot) == 0 then
            return false
        end
    end
    return true
end

local function isTrash(itemName)
    local name = string.lower(itemName)
    
    -- Keep list: ores, raw metals, gems, and valuable components
    local keepKeywords = {
        "ore", "raw", "diamond", "emerald", "lapis", "redstone", "coal", "charcoal",
        "zinc", "tin", "lead", "silver", "nickel", "uranium", "pyrite", "sphalerite",
        "galena", "bauxite", "cinnabar", "sheldonite", "sodolite", "tungsten", 
        "titanium", "iridium", "peridot", "ruby", "sapphire", "desh", "ostrum", 
        "calabrite", "falsite", "cheese", "ice_shard", "gem", "iron", "gold", 
        "copper", "brass", "zinc", "bronze", "steel", "nugget", "ingot", "quartz", 
        "fluix", "silicon", "dust", "sulfur"
    }
    
    for _, kw in ipairs(keepKeywords) do
        if string.find(name, kw) then
            return false
        end
    end
    
    -- Trash list: common vanilla and modded block names
    local trashKeywords = {
        "cobble", "stone", "dirt", "gravel", "sand", "tuff", "deepslate", 
        "granite", "diorite", "andesite", "gabbro", "scoria", "limestone", 
        "calcite", "basalt", "flint", "dripstone", "clay", "obsidian", "sandstone"
    }
    
    for _, kw in ipairs(trashKeywords) do
        if string.find(name, kw) then
            return true
        end
    end
    
    return false
end

local function compressInventory(dropDir)
    for slot = 1, 15 do
        local detail = turtle.getItemDetail(slot)
        if detail and isTrash(detail.name) then
            turtle.select(slot)
            if dropDir == "down" then
                turtle.dropDown()
            else
                turtle.drop()
            end
        end
    end
    turtle.select(1)
end

local function digWithRetry()
    local retries = 0
    while turtle.detect() and retries < 10 do
        turtle.dig()
        sleep(0.1)
        retries = retries + 1
    end
end

local function digUpWithRetry()
    local retries = 0
    while turtle.detectUp() and retries < 10 do
        turtle.digUp()
        sleep(0.1)
        retries = retries + 1
    end
end

local function digDownWithRetry()
    local retries = 0
    while turtle.detectDown() and retries < 10 do
        turtle.digDown()
        sleep(0.1)
        retries = retries + 1
    end
end
-- Relative coordinate, fuel tracker, and facing state for vein mining DFS
local blocksMoved = 0
local rx, ry, rz = 0, 0, 0
local rfacing = 0 -- 0=front, 1=right, 2=back, 3=left
local visited = {}

-- Global coordinate and facing state relative to starting base chest
local gx, gy, gz = 0, 0, 0
local gfacing = 0

local function manageFuel()
    if turtle.getFuelLevel() < 150 then
        turtle.select(16)
        if turtle.refuel(5) then
            print("Refueled from slot 16. Current level: " .. turtle.getFuelLevel())
        else
            for slot = 1, 15 do
                turtle.select(slot)
                if turtle.refuel(0) then
                    turtle.refuel(5)
                    print("Refueled from slot " .. slot .. ". Current level: " .. turtle.getFuelLevel())
                    break
                end
            end
        end
        turtle.select(1)
    end
end

local function getKey(x, y, z)
    return x .. "," .. y .. "," .. z
end

local function turnL()
    turtle.turnLeft()
    rfacing = (rfacing - 1) % 4
    gfacing = (gfacing - 1) % 4
end

local function turnR()
    turtle.turnRight()
    rfacing = (rfacing + 1) % 4
    gfacing = (gfacing + 1) % 4
end

local function moveForward()
    manageFuel()
    digWithRetry()
    local retries = 0
    while not turtle.forward() and retries < 10 do
        turtle.attack()
        digWithRetry()
        sleep(0.5)
        retries = retries + 1
    end
    if retries < 10 then
        if gfacing == 0 then gz = gz + 1
        elseif gfacing == 1 then gx = gx + 1
        elseif gfacing == 2 then gz = gz - 1
        elseif gfacing == 3 then gx = gx - 1
        end
        return true
    end
    return false
end

local function moveUp()
    manageFuel()
    digUpWithRetry()
    local retries = 0
    while not turtle.up() and retries < 10 do
        turtle.attackUp()
        digUpWithRetry()
        sleep(0.5)
        retries = retries + 1
    end
    if retries < 10 then
        gy = gy + 1
        return true
    end
    return false
end

local function moveDown()
    manageFuel()
    digDownWithRetry()
    local retries = 0
    while not turtle.down() and retries < 10 do
        turtle.attackDown()
        digDownWithRetry()
        sleep(0.5)
        retries = retries + 1
    end
    if retries < 10 then
        gy = gy - 1
        return true
    end
    return false
end

local function goForward()
    if moveForward() then
        if rfacing == 0 then rz = rz + 1
        elseif rfacing == 1 then rx = rx + 1
        elseif rfacing == 2 then rz = rz - 1
        elseif rfacing == 3 then rx = rx - 1
        end
        return true
    end
    return false
end

local function goBackward()
    manageFuel()
    if turtle.back() then
        if rfacing == 0 then rz = rz - 1
        elseif rfacing == 1 then rx = rx - 1
        elseif rfacing == 2 then rz = rz + 1
        elseif rfacing == 3 then rx = rx + 1
        end
        if gfacing == 0 then gz = gz - 1
        elseif gfacing == 1 then gx = gx - 1
        elseif gfacing == 2 then gz = gz + 1
        elseif gfacing == 3 then gx = gx + 1
        end
        return true
    end
    -- Turn 180, move, turn 180
    turtle.turnLeft()
    turtle.turnLeft()
    local old_gfacing = gfacing
    gfacing = (gfacing + 2) % 4
    local success = moveForward()
    gfacing = old_gfacing
    turtle.turnLeft()
    turtle.turnLeft()
    if success then
        if rfacing == 0 then rz = rz - 1
        elseif rfacing == 1 then rx = rx - 1
        elseif rfacing == 2 then rz = rz + 1
        elseif rfacing == 3 then rx = rx + 1
        end
        return true
    end
    return false
end

local function goUp()
    if moveUp() then
        ry = ry + 1
        return true
    end
    return false
end

local function goDown()
    if moveDown() then
        ry = ry - 1
        return true
    end
    return false
end

local function getTargetCoords(dirStr)
    local tx, ty, tz = rx, ry, rz
    if dirStr == "up" then
        ty = ty + 1
    elseif dirStr == "down" then
        ty = ty - 1
    elseif dirStr == "front" then
        if rfacing == 0 then tz = tz + 1
        elseif rfacing == 1 then tx = tx + 1
        elseif rfacing == 2 then tz = tz - 1
        elseif rfacing == 3 then tx = tx - 1
        end
    elseif dirStr == "back" then
        if rfacing == 0 then tz = tz - 1
        elseif rfacing == 1 then tx = tx - 1
        elseif rfacing == 2 then tz = tz + 1
        elseif rfacing == 3 then tx = tx + 1
        end
    elseif dirStr == "left" then
        local lf = (rfacing - 1) % 4
        if lf == 0 then tz = tz + 1
        elseif lf == 1 then tx = tx + 1
        elseif lf == 2 then tz = tz - 1
        elseif lf == 3 then tx = tx - 1
        end
    elseif dirStr == "right" then
        local rf = (rfacing + 1) % 4
        if rf == 0 then tz = tz + 1
        elseif rf == 1 then tx = tx + 1
        elseif rf == 2 then tz = tz - 1
        elseif rf == 3 then tx = tx - 1
        end
    end
    return tx, ty, tz
end

local function mineVeinRecursively()
    -- Safety check: if inventory is full, try to dump trash. If still full, abort to avoid losing ores.
    if isInventoryFull() then
        compressInventory("down")
        if isInventoryFull() then
            print("Warning: Inventory full during vein mine! Aborting vein.")
            return
        end
    end

    -- Safety check: if fuel is critically low and we have no fuel items, abort the vein to return home safely
    local totalRequired = blocksMoved + math.abs(rx) + math.abs(ry) + math.abs(rz) + 50
    if turtle.getFuelLevel() < totalRequired then
        manageFuel()
        if turtle.getFuelLevel() < totalRequired then
            print("Warning: Fuel critically low during vein mine! Aborting vein.")
            return
        end
    end

    local directions = {"up", "down", "front", "left", "right", "back"}
    for _, dir in ipairs(directions) do
        local tx, ty, tz = getTargetCoords(dir)
        local key = getKey(tx, ty, tz)
        if not visited[key] then
            -- Inspect block
            local success, data
            if dir == "up" then success, data = turtle.inspectUp()
            elseif dir == "down" then success, data = turtle.inspectDown()
            elseif dir == "front" then success, data = turtle.inspect()
            elseif dir == "left" then
                turnL()
                success, data = turtle.inspect()
                turnR()
            elseif dir == "right" then
                turnR()
                success, data = turtle.inspect()
                turnL()
            elseif dir == "back" then
                turnR()
                turnR()
                success, data = turtle.inspect()
                turnL()
                turnL()
            end

            if success and shouldMine(data.name) then
                visited[key] = true
                -- Move to the block and recurse
                local moved = false
                if dir == "up" then
                    moved = goUp()
                elseif dir == "down" then
                    moved = goDown()
                elseif dir == "front" then
                    moved = goForward()
                elseif dir == "left" then
                    turnL()
                    moved = goForward()
                elseif dir == "right" then
                    turnR()
                    moved = goForward()
                elseif dir == "back" then
                    turnR()
                    turnR()
                    moved = goForward()
                end

                if moved then
                    mineVeinRecursively()
                    -- Return back to previous block
                    if dir == "up" then
                        goDown()
                    elseif dir == "down" then
                        goUp()
                    elseif dir == "front" then
                        goBackward()
                    elseif dir == "left" then
                        goBackward()
                        turnR()
                    elseif dir == "right" then
                        goBackward()
                        turnL()
                    elseif dir == "back" then
                        goBackward()
                        turnL()
                        turnL()
                    end
                end
            else
                -- If it's not a block we want to mine, or if it is empty, mark it visited so we don't inspect again
                visited[key] = true
            end
        end
    end
end

local function returnToVeinStart()
    print("Vein mining complete. Aligning back to tunnel...")
    -- 1. Align Y (vertical)
    local ySafety = 0
    while ry ~= 0 and ySafety < 100 do
        if ry > 0 then
            if not goDown() then sleep(0.5) end
        else
            if not goUp() then sleep(0.5) end
        end
        ySafety = ySafety + 1
    end
    
    -- 2. Align X (side-to-side)
    -- We want to reduce rx to 0.
    local xSafety = 0
    while rx ~= 0 and xSafety < 100 do
        -- Turn to face the correct X direction
        local targetFacing = (rx > 0) and 3 or 1
        while rfacing ~= targetFacing do
            turnL()
        end
        if not goForward() then sleep(0.5) end
        xSafety = xSafety + 1
    end
    
    -- 3. Align Z (forward-back)
    -- We want to reduce rz to 0.
    local zSafety = 0
    while rz ~= 0 and zSafety < 100 do
        -- Turn to face the correct Z direction
        local targetFacing = (rz > 0) and 2 or 0
        while rfacing ~= targetFacing do
            turnL()
        end
        if not goForward() then sleep(0.5) end
        zSafety = zSafety + 1
    end
    
    -- 4. Face original direction (rfacing = 0)
    local turnSafety = 0
    while rfacing ~= 0 and turnSafety < 4 do
        turnL()
        turnSafety = turnSafety + 1
    end
    
    print(string.format("Aligned! Relative position: rx=%d, ry=%d, rz=%d, facing=%d", rx, ry, rz, rfacing))
end

local function returnToBaseChest()
    print("Failsafe: Returning to base chest...")
    
    -- 1. Align Y (vertical) to 0
    local ySafety = 0
    while gy ~= 0 and ySafety < 300 do
        if gy > 0 then
            if not moveDown() then sleep(0.5) end
        else
            if not moveUp() then sleep(0.5) end
        end
        ySafety = ySafety + 1
    end
    
    -- 2. Align X (side-to-side) to 0
    local xSafety = 0
    while gx ~= 0 and xSafety < 300 do
        local targetFacing = (gx > 0) and 3 or 1
        while gfacing ~= targetFacing do
            turnL()
        end
        if not moveForward() then sleep(0.5) end
        xSafety = xSafety + 1
    end
    
    -- 3. Align Z (forward-back) to 0
    local zSafety = 0
    while gz ~= 0 and zSafety < 500 do
        local targetFacing = (gz > 0) and 2 or 0
        while gfacing ~= targetFacing do
            turnL()
        end
        if not moveForward() then sleep(0.5) end
        zSafety = zSafety + 1
    end
    
    -- 4. Face starting direction (gfacing = 0)
    local turnSafety = 0
    while gfacing ~= 0 and turnSafety < 4 do
        turnL()
        turnSafety = turnSafety + 1
    end
    
    print(string.format("At base chest! Coordinates: gx=%d, gy=%d, gz=%d, facing=%d", gx, gy, gz, gfacing))
end

local function mineVein()
    -- Initialize relative coordinates (0,0,0) at the current block position
    rx, ry, rz = 0, 0, 0
    rfacing = 0
    visited = { [getKey(0, 0, 0)] = true }
    
    mineVeinRecursively()
    returnToVeinStart()
    compressInventory("down")
end

local function checkBranchForOres()
    local checkDirections = {"up", "down", "front", "left", "right"}
    local found = false
    for _, d in ipairs(checkDirections) do
        local success, data
        if d == "up" then success, data = turtle.inspectUp()
        elseif d == "down" then success, data = turtle.inspectDown()
        elseif d == "front" then success, data = turtle.inspect()
        elseif d == "left" then
            turtle.turnLeft()
            success, data = turtle.inspect()
            turtle.turnRight()
        elseif d == "right" then
            turtle.turnRight()
            success, data = turtle.inspect()
            turtle.turnLeft()
        end

        if success and shouldMine(data.name) then
            found = true
        end
    end
    return found
end


local function unloadInventory()
    print("Unloading inventory at base...")
    -- Unload mined items strictly into the ore chest below
    for slot = 1, 15 do
        turtle.select(slot)
        if turtle.getItemCount(slot) > 0 then
            turtle.dropDown()
        end
    end
    turtle.select(1)
end

local function refuelAtBase()
    turtle.select(16)
    if turtle.getItemCount(16) < 32 then
        -- Turn around to face the charcoal fuel chest behind
        turnR()
        turnR()
        turtle.suck()
        if not turtle.refuel(0) then
            -- Drop it back if it's not valid fuel
            turtle.drop()
        end
        -- Turn back to face forward
        turnL()
        turnL()
    end
    if turtle.getFuelLevel() < 1500 then
        turtle.refuel()
    end
    turtle.select(1)
end

local function runMiningSession()
    print("Starting a new mining run...")
    blocksMoved = 0
    gx, gy, gz = 0, 0, 0
    gfacing = 0
    
    local distanceToSide = math.ceil(runIndex / 2) * 9
    local direction = (runIndex % 2 == 1) and "right" or "left"
    
    -- Move from base chest to current tunnel offset (digging 2 blocks high)
    if runIndex > 0 then
        print(string.format("Moving to tunnel offset: %d blocks to the %s...", distanceToSide, direction))
        if direction == "right" then
            turnR()
        else
            turnL()
        end
        for s = 1, distanceToSide do
            if moveForward() then
                digUpWithRetry()
            end
        end
        if direction == "right" then
            turnL()
        else
            turnR()
        end
    end
    
    for i = 1, distance do
        manageFuel()
        
        -- Fuel needed is distance back to tunnel start + distance back to base chest + safety buffer
        local fuelNeeded = blocksMoved + distanceToSide + 50
        if turtle.getFuelLevel() < fuelNeeded then
            print("Warning: Low fuel! Returning to base early.")
            break
        end
        
        if isInventoryFull() then
            compressInventory("down")
            if isInventoryFull() then
                print("Inventory full! Returning to base early.")
                break
            end
        end

        if moveForward() then
            blocksMoved = blocksMoved + 1
            print(string.format("Progress: %d/%d blocks", blocksMoved, distance))
            
            -- SCAN SURROUNDING BLOCKS FOR ORES (Up/down at every step)
            local successUp, dataUp = turtle.inspectUp()
            if successUp and shouldMine(dataUp.name) then
                print("Found ore above main tunnel! Beginning vein mining...")
                mineVein()
            end
            local successDown, dataDown = turtle.inspectDown()
            if successDown and shouldMine(dataDown.name) then
                print("Found ore below main tunnel! Beginning vein mining...")
                mineVein()
            end

            -- SCAN SIDE BRANCHES (Only every 5 blocks)
            if blocksMoved % 5 == 0 then
                -- 1. Left branch
                local foundLeft = false
                turnL()
                local leftMoved = 0
                for step = 1, 4 do
                    if moveForward() then
                        leftMoved = leftMoved + 1
                        if checkBranchForOres() then
                            foundLeft = true
                            print("Found ore in left branch! Beginning vein mining...")
                            mineVein()
                        end
                    else
                        break
                    end
                end
                
                -- Backtrack left branch
                turnL()
                turnL()
                for s = 1, leftMoved do
                    moveForward()
                end
                turnL() -- Face forward again
                
                -- 2. Right branch (only if NO ore was found on the left)
                if not foundLeft then
                    turnR()
                    local rightMoved = 0
                    for step = 1, 4 do
                        if moveForward() then
                            rightMoved = rightMoved + 1
                            if checkBranchForOres() then
                                print("Found ore in right branch! Beginning vein mining...")
                                mineVein()
                            end
                        else
                            break
                        end
                    end
                    
                    -- Backtrack right branch
                    turnL()
                    turnL()
                    for s = 1, rightMoved do
                        moveForward()
                    end
                    turnR() -- Face forward again
                end
            end
        else
            print("Unbreakable block or path blocked. Returning home.")
            break
        end
    end
    
    returnToBaseChest()
    
    print("Successfully returned to base chest!")
    compressInventory("front")
    unloadInventory()
    refuelAtBase()
end

-- Main infinite loop
runIndex = loadState()

while true do
    runMiningSession()
    runIndex = runIndex + 1
    saveState(runIndex)
    local nextDistance = math.ceil(runIndex / 2) * 9
    local nextSide = (runIndex % 2 == 1) and "right" or "left"
    print(string.format("Mining run complete. Next tunnel offset: %d blocks to the %s. Starting in 5 seconds...", nextDistance, nextSide))
    sleep(5)
end
