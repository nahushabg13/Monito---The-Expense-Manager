# Monito — Project State

Point-in-time snapshot. This file goes stale as soon as you write more code — re-generate it (ask Claude to redo this analysis) rather than trusting it blindly after a work session.

**As of:** 2026-07-21, branch `main_brushup`, on top of commit `ad3a683` with uncommitted changes
**Source of truth for scope/plan:** `ROADMAP.md`. This file is a status report against that plan, not a replacement for it.

## TL;DR

Phase 0 (data model + nav skeleton) is most of the way done. Phases 1–9 — i.e. every actual screen — have no UI code yet, just two placeholder `Text("... screen")` composables. The data layer (Room entities/DAOs/DB) is real and sound, including the enum `TransactionType` + `TypeConverter` wiring.

## Uncommitted changes in the working tree

- **New:** `TransactionType.kt` (enum `EXPENSE`/`INCOME`), `Converters.kt` (`TypeConverter` between `TransactionType` and `String`).
- **Modified:** `AppDatabase.kt` (`@TypeConverters(Converters::class)` registered), `CategoryEntity.kt` (`type` changed from raw `String` to `TransactionType`).
- Compiles cleanly (`./gradlew compileDebugKotlin`).

## Schema decision (changed 2026-07-21)

`type` (`EXPENSE`/`INCOME`) lives on **`CategoryEntity` only** — deliberately not duplicated onto `TransactionEntity`. A transaction's type is derived via `categoryId → category.type`, not stored on the transaction row. This was an explicit roadmap change (see `ROADMAP.md` Phase 0 design note): single source of truth, no risk of a transaction's type drifting from its category's, at the cost of needing a join/lookup against `CategoryEntity` anywhere a screen splits transactions by expense/income (Phase 1's summary row, Phase 4's graph tab toggle).

## What's built

- **`TransactionEntity`** (id, categoryId, amount, note, date) with a `RESTRICT` foreign key to `CategoryEntity` — deletes are blocked while a category has transactions. No `type` column, by design (see above).
- **`CategoryEntity`** (id, type: `TransactionType`, color, name).
- **`TransactionType`** — enum, `EXPENSE`/`INCOME`.
- **`Converters`** — `TypeConverter` for `TransactionType` ↔ `String`, registered on `AppDatabase`.
- **`TransactionDao`** / **`CategoryDao`** — Flow-returning queries, suspend insert/update/delete. Neither has a join query yet (needed once Phase 1/4 need type-aware aggregation).
- **`AppDatabase`** — Room singleton, both entities registered, version 1, converters wired.
- **`MonitoApp`** — registered in the manifest (`android:name=".MonitoApp"`), lazily builds the database.
- **Nav shell** — `NavHost` + bottom `NavigationBar` (nested inside a `BottomAppBar`, worth revisiting — not the usual pattern) with 2 destinations (Transactions, Graphs), each showing a placeholder `Text`.
- **Gradle** — Room 2.7.1 + KSP, Navigation Compose, material-icons-extended all wired (hardcoded coordinates in `app/build.gradle.kts`, not in the version catalog).
- `ui/theme/*` (Color, Theme, Type) — carried over from before the rebuild.

## Phase 0 gaps (should close before starting Phase 1)

1. **Nav shell has 2 of 4 planned destinations.** Categories and Settings placeholders don't exist yet.
2. **Routes are raw strings** (`"transactions"`, `"graphs"`), not the sealed-class routes the Concepts line calls for.
3. **No drawer.** The reference app (see screenshot findings below) uses bottom nav *and* a drawer, not one or the other — current code only has bottom nav.
4. **No DAO query joins `CategoryEntity`** yet — needed for any type-aware aggregation once Phase 1 starts.

Closed since the last snapshot: `type` field decision resolved (now `CategoryEntity`-only, see schema decision above), `TransactionType` enum + `TypeConverter` in place.

## Phases 1–9: not started

No screen beyond the two placeholders exists. Everything in `ROADMAP.md` phases 1 (transaction list), 2 (add/edit), 3 (categories), 4 (graphs), 5 (search), 6 (trash), and the stretch phases 7–9 is still ahead.

## Findings from reviewing all 16 reference screenshots (not yet reflected in `ROADMAP.md`)

These came out of a screenshot-by-screenshot review and refine or extend the roadmap's phase descriptions:

- **Nav is drawer + bottom nav together**, not either/or: bottom nav for Transactions/Graphs, drawer for Categories/Settings/Trash/Recurrings.
- **Transaction rows are fully filled with the category's color**, not just a colored icon/dot.
- **Categories screen is a flat `LazyColumn`**, not the `LazyVerticalGrid` the roadmap's Phase 3 currently specifies — real mismatch to resolve before building that screen.
- **The amount-entry calculator is a full 4-function calculator** (`+ − × ÷`), bigger in scope than "keypad" implies. Still correctly deferred to stretch.
- **The add/edit sheet's header trash icon deletes the attached photo**, confirmed by the "Remove this photo?" dialog — how whole-transaction delete is actually triggered isn't shown in any screenshot. Open question for Phase 2 design.
- **Graphs screen has per-category checkboxes** (toggle a category in/out of the donut) **and a drill-down chevron** per row — neither is in the roadmap's Phase 4 plan. Needs an explicit include/cut decision.
- **Settings has two unscoped items:** a "default landing tab" radio (Transactions vs Graphs) and a daily "Reminder" notification toggle with a time. Not in the cut list, not in any phase.
- **Search does plain substring matching** against the note field (confirmed: "milk" matched a note saved as "Milk + Groceries") — no tokenization needed, simplifies Phase 5.

## Suggested next action

Close the remaining Phase 0 gaps above before starting Phase 1 UI work — in particular deciding on the drawer + bottom nav shell and sealed-class routes, since Phase 1's screen will hang off whichever nav structure you land on now.
