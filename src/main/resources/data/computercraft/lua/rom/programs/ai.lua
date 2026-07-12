-- Interactive 5-Tier AI Core Interface Matrix
term.clear()
term.setCursorPos(1,1)

local core = peripheral.find("ai_core")
if not core then
    print("Error: No Kinetic AI Core peripheral detected adjacent to this computer.")
    return
end

print("=========================================")
print("      KINETIC AI MATRIX TERMINAL         ")
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

-- Map numerical input to our 5 defined backend profiles
local selectedModel = "qwen3.5:9b"
if input == "1" then selectedModel = "qwen:0.5b"
elseif input == "2" then selectedModel = "qwen3.5:4b"
elseif input == "3" then selectedModel = "qwen3:8b"
elseif input == "5" then selectedModel = "qwen2.5:14b"
end

print("\nConnecting to GPU runtime node...")
sleep(0.5)
print("The model is ready and functional. Go ahead and ask something!")
print("(Type 'exit' or 'quit' to close the matrix loop)")
print("-----------------------------------------")

while true do
    write("\nYOU> ")
    local prompt = read()
    
    if prompt == "exit" or prompt == "quit" or prompt == "" then
        print("\nDisconnecting from core node. Goodbye.")
        break
    end
    
    write("AI> ")
    local ok, reqId = pcall(core.streamTelemetry, prompt, selectedModel)
    
    if not ok then
        print("\n[Casing Error]: " .. tostring(reqId))
    else
        while true do
            local event, id, token, done = os.pullEvent("ai_token")
            if id == reqId then
                if token then
                    io.write(token)
                end
                if done then
                    print("")
                    break
                end
            end
        end
    end
end