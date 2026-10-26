# Changelog

## 0.2.0 — Extraction & Storage (unreleased)

### Added
- **GridStorageCellBlock / BlockEntity** — 27-slot storage cell, hopper-compatible, joins the grid via `IGridStorageCell`.
- **GridFloodFill** — BFS grid discovery (panels + cells), 4096-member cap.
- **GridIndex** — aggregated item view (one merged stack per unique item type).
- **GridManager** — rebuild cadence (dirty-flag or 10-tick), server-side filter, snapshot push.
- **GridExtraction** — server-side item extraction across all cells in the grid, nearest-first, caps at 4096 per request.
- **ExtractFromGridPacket** — click = one stack, shift-click = all of type; extracted items go to player inventory, overflow drops at feet.
- Panel recipe (3 iron / chest / 3 iron), storage cell recipe (same but chest-centered).

### Fixed
- Read-only grid snapshot slots can no longer be quick-moved or dragged.
- Filter packets validated server-side before applying to the index.

### Known gaps
- No grid power requirement yet (Bio Burner planned).
- No tiered storage cells yet (basic 27-slot cell only).
