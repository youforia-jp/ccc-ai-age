-- Interactive AI Core Interface
term.clear()
term.setCursorPos(1,1)

local core = peripheral.find("ai_core")
if not core then
    print("Error: No AI Core peripheral detected. Attach an AI Core block adjacent to this computer, or equip an AI Core turtle upgrade.")
    return
end

print("=========================================")
print("          AI CORE TERMINAL               ")
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

print("\nConnecting to AI runtime...")
sleep(0.5)
print("The model is ready. Go ahead and ask something!")
print("(Type 'exit' or 'quit' to close)")
print("-----------------------------------------")

while true do
    write("\nYOU> ")
    local prompt = read()
    
    if prompt == "exit" or prompt == "quit" or prompt == "" then
        print("\nDisconnecting from AI core. Goodbye.")
        break
    end
    
    write("AI> ")
    local ok, reqId = pcall(core.streamTelemetry, prompt, selectedModel)
    
    if not ok then
        print("\n[Error]: " .. tostring(reqId))
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