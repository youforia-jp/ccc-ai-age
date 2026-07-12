local stepCount = 0

print("=========================================")
print("      KINETIC TURTLE - MINING NODE       ")
print("=========================================")
print("Running infinite mining loop. Press Ctrl+T to stop.")
print("-----------------------------------------")

while true do
    turtle.dig()           -- clear block in front (ignores air)
    local moved = turtle.forward()

    if moved then
        stepCount = stepCount + 1
        print(string.format("[%d] Moved forward successfully.", stepCount))
    else
        print(string.format("[%d] Warning: Movement blocked.", stepCount))
    end

    sleep(1.0)
end
