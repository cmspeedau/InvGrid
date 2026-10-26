# InvGrid

**InvGrid** is a standalone tech-storage mod for Minecraft: networked **Inventory Panels** that aggregate items from connected inventories, with tiered hardware, remote access, and visual storage crates.

Inspired by the best of Ender IO's inventory panel concept — rebuilt as its own modern mod.

## Core Pillars
1. **The Panel is the interface** — physical, tiered panels placed where you work, not terminals everywhere.
2. **Grid, not network** — lightweight grid formed by a Grid Hub + Cables + Panels. No heavy ME-style simulation.
3. **Interoperability first** — reads chests, barrels, drawers, and other mods' inventories via standard capabilities.

## Blocks & Items
| Block | Purpose |
|---|---|
| Grid Hub | Heart of the grid; 1 per grid, defines range/bounds |
| InvGrid Cable | Connects hubs ↔ panels ↔ inventories |
| Inventory Panel (Basic) | View/search/extract from grid |
| Inventory Panel (Advanced) | Filtering, sorting, crafting slot, bulk extract |
| Inventory Panel (Elite) | Multi-grid access, wireless-link anchor |
| Storage Crate | Visual-fill crate — model grows stacked boxes as it fills |
| Remote Control | Opens a linked panel from anywhere in-dimension |
| Grid Card | Binds a panel/remote to a specific hub |

## Roadmap
- **v0.1** — Grid Hub, cable, Basic panel, Storage Crate (read-only grid)
- **v0.2** — Extract/insert, server-side search index, Advanced panel, Remote Control
- **v0.3** — Elite tier, Grid Cards, JEI/REI integration
- **v1.0** — Config, sounds, advancement tab, polish

## Building
- Minecraft: 1.20.1 (Forge)
- Java: 17

```bash
deco (or) ./gradlew build
```

## License
MIT
