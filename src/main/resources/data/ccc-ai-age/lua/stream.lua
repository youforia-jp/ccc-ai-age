-- Automated AI Core Streaming Utility Script
local args = {...}
local prompt = args[1]

if not prompt then
    print("Usage: stream \"[your prompt text here]\"")
    return
end

local core = peripheral.find("ai_core")
if not core then
    print("Error: No AI Core peripheral detected next to this terminal.")
    return
end

-- Check kinetic stability before launching
local status = core.getKineticData()
if not status.isPowered then
    print("Error: The network grid is spinning below authorization thresholds.")
    return
end

print("Connecting to GPU runtime node...")
local ok, reqId = pcall(core.streamTelemetry, prompt)
if not ok then
    print("Core Rejected Request: " .. tostring(reqId))
    return
end

-- Clean token loop listener
while true do
    local event, id, token, done = os.pullEvent("ai_token")
    if id == reqId then
        if token then io.write(token) end
        if done then 
            print("\n")
            break 
        end
    end
end