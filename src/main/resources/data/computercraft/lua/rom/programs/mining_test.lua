print("=========================================")
print("      STARTING STATIC MINING ROUTE       ")
print("=========================================")

for i = 1, 5 do
    print(string.format("Executing progress node: %d/5", i))

    -- Blindly try to move forward.
    -- If blocked by Obsidian/Stone, the environment wrapper catches it here!
    local success = turtle.forward()

    if not success then
        print("Static warning: Forward movement failed.")
    else
        print("Static status: Moved forward successfully.")
    end

    sleep(1.5)
end

print("=========================================")
print("      STATIC TEST ROUTE COMPLETED        ")
print("=========================================")
