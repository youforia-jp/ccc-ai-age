import urllib.request
import json
import os

MODS = [
    {
        "name": "CC: Tweaked",
        "type": "modrinth",
        "slug": "cc-tweaked",
        "filename": "cc-tweaked-1.20.1-fabric-1.120.0.jar"
    },
    {
        "name": "Forge Config API Port",
        "type": "modrinth",
        "slug": "forge-config-api-port",
        "filename": "ForgeConfigAPIPort-v8.0.3-1.20.1-Fabric.jar"
    },
    {
        "name": "Porting Lib",
        "type": "modrinth",
        "slug": "porting_lib",
        "filename": "porting_lib-2.3.15+1.20.1.jar"
    },
    {
        "name": "Milk Lib",
        "type": "direct",
        "url": "https://mvn.devos.one/snapshots/io/github/tropheusj/milk-lib/1.2.60/milk-lib-1.2.60.jar",
        "filename": "milk-lib-1.2.60.jar"
    },
    {
        "name": "Reach Entity Attributes",
        "type": "direct",
        "url": "https://maven.jamieswhiteshirt.com/libs-release/com/jamieswhiteshirt/reach-entity-attributes/2.4.0/reach-entity-attributes-2.4.0.jar",
        "filename": "reach-entity-attributes-2.4.0.jar"
    }
]

output_dir = r"c:\Users\juanp\Desktop\ccc AI Age\mods_to_install"
os.makedirs(output_dir, exist_ok=True)

headers = {
    "User-Agent": "youforia-jp/ccc-ai-age/1.0.0 (juanp@youforia.net)"
}

def get_latest_version(slug):
    url = f"https://api.modrinth.com/v2/project/{slug}/version?game_versions=%5B%221.20.1%22%5D&loaders=%5B%22fabric%22%5D"
    req = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(req) as response:
            data = json.loads(response.read().decode())
            if not data:
                return None
            return data[0]
    except Exception as e:
        print(f"Error fetching version for {slug}: {e}")
        return None

for mod in MODS:
    dest_path = os.path.join(output_dir, mod["filename"])
    
    if mod["type"] == "modrinth":
        print(f"Finding latest 1.20.1 Fabric release for {mod['name']}...")
        version = get_latest_version(mod["slug"])
        if not version:
            print(f"Could not find compatible version for {mod['name']}")
            continue
        
        files = version.get("files", [])
        if not files:
            print(f"No files found for {mod['name']}")
            continue
            
        primary_file = next((f for f in files if f.get("primary")), files[0])
        download_url = primary_file["url"]
    else:
        print(f"Using direct URL for {mod['name']}...")
        download_url = mod["url"]
        
    print(f"Downloading to {mod['filename']} from {download_url}...")
    
    try:
        req = urllib.request.Request(download_url, headers=headers)
        with urllib.request.urlopen(req) as response, open(dest_path, 'wb') as out_file:
            out_file.write(response.read())
        print(f"Successfully downloaded {mod['name']} to {dest_path}")
    except Exception as e:
        print(f"Error downloading {mod['name']}: {e}")

print("\nDone! All mods downloaded to:", output_dir)
