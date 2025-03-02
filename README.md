# AfroRadNoDrop

A Bukkit/Spigot plugin that integrates with Slimefun to manage radioactive item drops in Minecraft servers.

## Overview

AfroRadNoDrop prevents players from dropping radioactive items (items with "Radiation level:" in their lore) in specified worlds. It offers configurable options to either cancel drops or delete the items entirely, both for manual drops and death drops. This plugin requires Slimefun to function.

## Features

- Prevents dropping radioactive items in non-permitted worlds
- Configurable option to delete items on manual drop (`delete-on-drop`)
- Configurable option to delete radioactive items from death drops (`delete-on-death`)
- Customizable messages with color code support
- Reload command with permission system
- Integration with Slimefun for radioactive item detection
- Detailed logging for drop prevention and item deletion

## Installation

1. Ensure you have a Spigot/Paper server running with Slimefun installed.
2. Download the latest `AfroRadNoDrop.jar` from the [Releases](https://github.com/afroNinja420/AfroRadNoDrop/releases) page.
3. Place the JAR file in your server's `plugins` folder.
4. Restart your server to generate the default configuration file.

## Configuration

The plugin creates a `config.yml` file in `plugins/AfroRadNoDrop/` with the following default settings:

```yaml
# List of worlds where dropping radioactive items is allowed
permitted-worlds:
  - oneblock
  - oneblock_nether

# If true, the item will be deleted when dropped in a non-permitted world
delete-on-drop: false

# If true, radioactive items will be deleted from inventory drops when a player dies in a non-permitted world
delete-on-death: true

# In game messages (use '&' for color codes)
# Color codes: &0-9 (numbers), &a-f (letters), &k-o (formatting), &r (reset)
messages:
  noDrop: "&4You cannot drop radioactive items in this world!"
  itemDestroyed: "&4The radioactive item has been destroyed!"
  noPermission: "&4You do not have permission to use this command!"
```

### Config Options
- `permitted-worlds`: List of world names where radioactive items can be dropped freely
- `delete-on-drop`: If `true`, radioactive items are deleted when dropped manually in non-permitted worlds; if `false`, the drop is cancelled
- `delete-on-death`: If `true`, radioactive items are removed from death drops in non-permitted worlds
- `messages`: Customizable messages with Minecraft color code support (`&`)

## Commands

- `/afroradnodrop reload`
  - Reloads the configuration file
  - Permission: `afroradnodrop.reload`
  - Tab completion supported

## Permissions

- `afroradnodrop.reload`: Allows use of the reload command
  - Default: OP only

## Usage

1. Configure `permitted-worlds` to include worlds where radioactive items should be freely droppable.
2. Set `delete-on-drop` and `delete-on-death` according to your server's needs:
   - `false` for preventative measures (drop cancellation or normal death drops)
   - `true` to destroy radioactive items in non-permitted worlds
3. Customize messages in the `messages` section using Minecraft color codes.
4. Use `/afroradnodrop reload` to apply changes without restarting the server.

### Example Scenarios
- In a world not listed in `permitted-worlds`:
  - With `delete-on-drop: false`: Players cannot drop radioactive items (action cancelled)
  - With `delete-on-drop: true`: Radioactive items are deleted when dropped
  - With `delete-on-death: true`: Radioactive items are removed from death drops
- In a permitted world: All drops function normally regardless of settings

## Requirements

- **Server**: Spigot or Paper (latest versions recommended)
- **Dependency**: Slimefun (must be installed and enabled)
- **Java**: 8 or higher

## Building from Source

1. Clone the repository:
   ```bash
   git clone https://github.com/afroNinja420/AfroRadNoDrop.git
   ```
2. Navigate to the project directory and build with Maven:
   ```bash
   mvn clean package
   ```
3. Find the compiled JAR in the `target` folder.

## Issues and Contributions

- Report bugs or suggest features via [Issues](https://github.com/afroNinja420/AfroRadNoDrop/issues).
- Pull requests are welcome! Fork the repo and submit your changes.

## License

[MIT License](LICENSE) - Feel free to use, modify, and distribute this plugin.

---

Created by afroNinja420
