# Monito Roadmap

## Where this comes from

`App screenshots/` is a set of screenshots from an expense-tracker app you used to use. This roadmap rebuilds a **simpler** version of it — the goal isn't feature parity, it's learning Jetpack Compose properly by building something real, one deliberate phase at a time.

## Ground rules for this rebuild

- **Learning first.** Every phase lists the specific Compose/Android concepts it's meant to teach. If a phase isn't teaching you something new, question whether it needs to exist yet.
- **Simpler on purpose.** The cut list below is deliberate scoping, not laziness — each cut has a reason.
- **Get the schema right early.** Income vs. expense, and categories as their own table (not a hardcoded list), are decided in Phase 0 because retrofitting either one later means a Room migration on top of working code.

## Explicitly cut from the reference app (and why)

| Reference feature | Screenshot | Status | Why |
|---|---|---|---|
| Cloud backup / Google Drive sync | `Settins_2.jpg` | Cut | OAuth + Drive API is infra work, not Compose learning |
| Screen lock / biometric | `Settings_1.jpg` | Cut | Peripheral security feature, low learning value here |
| Multi-language | `Settings_1.jpg` | Cut | i18n plumbing, not Compose-specific |
| Currency switching | `Settings_1.jpg` | Cut | Single currency (₹) is enough for a learning app |
| Carry-forward balance | `Settings_1.jpg` | Cut | Niche accounting feature |
| Calculator-style amount keypad | `Add_Expense.jpg` | Deferred (stretch, Phase 9+) | Fun but a distraction until core CRUD works |
| Photo attachment | `Add_expense_with_expense_image.jpg`, `Dashboard_expense_with_photo.jpg` | Deferred (Phase 9) | Needs camera/gallery permissions + image loading — good once core is solid |
| Recurring transactions | `Expenses_recurring.jpg` | Deferred (Phase 7) | Needs WorkManager/background scheduling — a genuinely new topic, own phase |

## Phases

### Phase 0 — Data model & navigation skeleton
**Goal:** get the schema right before building UI on top of it, and stand up a navigable shell.
**Build:**
- `TransactionEntity`: id, amount, note, date, categoryId (FK). No `type` column — a transaction's expense/income-ness is *derived* from its category, not stored redundantly on the row.
- `CategoryEntity`: id, name, colorHex, type (`EXPENSE`/`INCOME`) — type lives here, once, since it's a property of the category, not the transaction. A transaction's type is always `category.type` via `categoryId`.
- Nav shell: a drawer or bottom nav with placeholder screens for Transactions, Graphs, Categories, Settings.
**Concepts:** Room entity relations (foreign keys), enums in Room via `TypeConverter`, multi-destination `NavHost`, sealed-class routes, `Scaffold` with `NavigationBar`/drawer.
**Reference:** `Dashboard.jpg` (menu icon top-left)

**Design note — type lives on `CategoryEntity` only:** deliberately not duplicated onto `TransactionEntity`. Upside: single source of truth, no risk of a transaction's type drifting from its category's. Cost: anything that needs to split transactions by expense/income (Phase 1's summary row, Phase 4's graph tab toggle) needs a join/lookup against `CategoryEntity` rather than a plain `WHERE type = ...` on the transactions table — DAO queries for those screens should `JOIN categories` or resolve type through the category map in the repository/ViewModel layer, not by adding the column back later.

### Phase 1 — Transactions list (the dashboard)
**Goal:** rebuild the core screen — this is most of what the app actually is.
**Build:**
- Expense / Income / Balance summary row, computed from the current month's transactions.
- List grouped by day, each day showing a subtotal.
- Month navigator (prev/next arrows — skip the custom date-range picker for now).
**Concepts:** grouping a `Flow<List<T>>` by key, `java.time` date math, `LazyColumn` with multiple item types (day header vs. row), state hoisting between summary and list, `derivedStateOf`.
**Reference:** `Dashboard.jpg`

### Phase 2 — Add / edit transaction
**Goal:** a full create+update flow (the old app only had create).
**Build:**
- `ModalBottomSheet` (not a separate screen) with Date, Amount, Category picker, Note.
- Plain numeric `TextField` for amount in v1 (calculator keypad is a later stretch goal).
- Material3 `DatePickerDialog` for date.
- Delete action in the same sheet — hard-delete for now, wired to soft-delete once Phase 6 exists.
**Concepts:** `rememberModalBottomSheetState`, `DatePicker`, one composable handling both add and edit (entity-or-null pattern), form validation, ViewModel one-shot events vs. state.
**Reference:** `Add_Expense.jpg`, `Remove_expense.jpg`

### Phase 3 — Categories screen
**Goal:** categories become real data instead of a hardcoded chip list.
**Build:**
- List all categories with a color swatch.
- Add category: name + pick a color from a fixed palette (skip custom per-category icons — reuse a generic dot/initial).
**Concepts:** a second CRUD entity end-to-end (Dao → Repository → ViewModel → UI), `LazyVerticalGrid`, color-selection UI, deciding what happens to a transaction when its category is deleted (block delete vs. reassign to "Uncategorized").
**Reference:** `Expense_ Category.jpg`, `Add_Expense_Select_category.jpg`

### Phase 4 — Graphs
**Goal:** visualize the data you've been collecting.
**Build:**
- Expense/Income tab toggle.
- Donut chart of spend-by-category with a legend, drawn with Compose `Canvas`/`drawArc` — no charting library, so you learn how a pie chart is just math.
- Date-range filter chips (This month / Last month / All time — skip the full custom range picker).
**Concepts:** the `Canvas` drawing API, converting aggregated sums into angles/sweep, `TabRow`, custom composable layout — the most "pure Compose" phase, no new architecture concepts.
**Reference:** `Graphs_expense.jpg`, `Graph_income.jpg`, `Graph_filter.jpg`

### Phase 5 — Search
**Goal:** filter transactions by text.
**Build:** Material3 `SearchBar` filtering by note/category name, reusing Phase 1's grouped-list UI for results.
**Concepts:** the M3 `SearchBar` component, reactively filtering a `Flow` as text changes, debouncing input.
**Reference:** `Expenses_search_input.jpg`, `Expenses_search_result.jpg`

### Phase 6 — Soft delete (Trash)
**Goal:** replace Phase 2's hard-delete with a real trash, and learn swipe gestures.
**Build:**
- Swipe-to-delete via `SwipeToDismissBox`, moving a transaction to a trashed state (`isDeleted` + `deletedAt`) instead of removing it.
- Trash screen to restore or permanently delete; optional "auto-delete after 7 days" setting.
**Concepts:** `SwipeToDismissBox`, soft-delete data modeling, a `Snackbar` "Undo" action tied to a real state change, not just a message.
**Reference:** `Trash.jpg`

### Phase 7 (stretch) — Recurring transactions
**Goal:** background work — a genuinely new category of Android concept, not just more Compose.
**Build:** `RecurringEntity` (amount, category, frequency, nextDueDate) + a WorkManager job that inserts due transactions.
**Concepts:** `WorkManager`, `PeriodicWorkRequest`, background execution constraints.
**Reference:** `Expenses_recurring.jpg`

### Phase 8 (stretch) — Settings that matter
**Goal:** a couple of real preferences, done the modern way.
**Build:** Theme switch (System/Light/Dark) using Jetpack DataStore (not `SharedPreferences`), applied at the `MonitoTheExpenseManagerTheme` root.
**Concepts:** DataStore Preferences, reading a `Flow<Preferences>` into Compose state at the app root.
**Reference:** `Settings_1.jpg` (Theme row only — currency/language/security/backup are cut, see above)

### Phase 9 (stretch) — Photo attachment
**Goal:** device I/O — camera/gallery integration.
**Build:** attach a photo to a transaction via `ActivityResultContracts.PickVisualMedia`, display it with Coil.
**Concepts:** Activity Result APIs, runtime permissions, Coil for Compose image loading, storing a file URI vs. the file itself.
**Reference:** `Add_expense_with_expense_image.jpg`, `Dashboard_expense_with_photo.jpg`

## Suggested order

Phases 0–6 are the real app — do them in order, each depends on the previous one's data/screens existing. Phases 7–9 are independent stretch goals; pick whichever teaches you something you haven't done yet.
