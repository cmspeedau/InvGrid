# DEV punch list

## v0.2 — Panel live view (current)
- [x] GUI screen: 9x6 grid area, search box, player inventory
- [x] Menu with 54 virtual read-only slots + player slots
- [x] `ModMenus` registration + screen binding
- [x] **Snapshot sync packet** (`SyncGridSnapshotPacket`, S2C)
- [x] **Extract packet** (`ExtractFromGridPacket`, C2S) — click/shift-click in GUI
- [x] **Filter packet** (`UpdateFilterPacket`, C2S) — server-side search, echoes fresh snapshot
- [x] Screen renders items from snapshot + hover tooltips
- [ ] Wire `pushSnapshot` into `GridManager` (needs flood-fill index)
- [ ] Implement `InventoryPanelMenu.tryExtract` against real grid index
- [ ] Fix `stillValid` distance check to use grid hub position
- [ ] Background texture art (placeholder grey in repo)

## v0.2 — Bio Burner
- [ ] Fuel table + burn loop in `BioBurnerBlockEntity`
- [ ] Push FE to adjacent panels/hub
- [ ] BlockEntityType registration

## v0.3
- [ ] Grid Card item (hub binding)
- [ ] Cable block with connectable shape
- [ ] Grid range/size limits enforced server-side
