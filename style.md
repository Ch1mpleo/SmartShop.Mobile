# SmartShop — Design Style Guide

> **Design Principle:** Dark-first, precision-crafted, commercially alive.
> SmartShop should feel like shopping at a concept store that happens to live in your pocket — confident, tactile, and frictionless. Every screen should feel intentional, never templated.

---

## 1. Brand Personality

| Trait | Expression |
|---|---|
| **Modern** | Dark backgrounds, sharp edges, purposeful white space |
| **Trustworthy** | Consistent token use, clear hierarchy, accessible contrast |
| **Energetic** | Electric accent, micro-animations, responsive feedback |
| **Premium** | Refined typography, crisp product photography framing |
| **Human** | Warm neutrals, conversational copy tone, real photography |

---

## 2. Color Palette

The palette is built on **5 tokens only**. Everything in the UI derives from these five.

### Core Tokens

```kotlin
// Color.kt (Compose)
val Background   = Color(0xFF0D0D0D)  // Near-black — primary screen canvas
val Surface      = Color(0xFF1A1A1A)  // Card / sheet / bottom sheet background
val Primary      = Color(0xFFE8FF47)  // Electric Lime — brand accent, CTAs, highlights
val OnBackground = Color(0xFFF0EDE6)  // Warm off-white — primary text on dark
val Muted        = Color(0xFF6B6B6B)  // Mid-gray — secondary text, dividers, placeholders
```

### Extended Semantic Tokens

```kotlin
val OnPrimary       = Color(0xFF0D0D0D)  // Text on Primary (lime) buttons
val Error           = Color(0xFFFF4D4D)  // Destructive / error states
val OnError         = Color(0xFFF0EDE6)
val Success         = Color(0xFF3DDB84)  // Order confirmed, in-stock indicators
val OnSurface       = Color(0xFFF0EDE6)  // Text/icons placed on Surface cards
val SurfaceVariant  = Color(0xFF252525)  // Slightly elevated card variant, input fields
val Outline         = Color(0xFF2E2E2E)  // Dividers, borders, strokes
val Scrim           = Color(0xCC0D0D0D)  // Overlay behind modals / bottom sheets
```

### Usage Rules

- `Primary` (Electric Lime `#E8FF47`) is used **only** for: primary action buttons, active tab indicators, price highlights, badge backgrounds, and interactive icon states.
- Never place `Primary` on a `Surface` background directly — always use `OnPrimary` as the text/icon color on top of it.
- `Muted` is for placeholder text, secondary metadata (ratings count, timestamps), and decorative dividers only — never for actionable elements.
- **No gradients** on interactive elements. Gradients are reserved for hero banners and promotional image overlays only (max 2 stops: `#0D0D0D` → transparent).

---

## 3. Typography

### Font Families

| Role | Font | Source |
|---|---|---|
| **Display / Heading** | `Syne` (Bold 700, ExtraBold 800) | Google Fonts |
| **Body / UI** | `DM Sans` (Regular 400, Medium 500, SemiBold 600) | Google Fonts |

**Why these fonts?**
- **Syne** has a distinctive geometric construction with subtle quirks (irregular letter spacing on its own terms) — it reads as editorial and confident without being cold.
- **DM Sans** is optically balanced for small screen readability, humanist in form, pairs beautifully with Syne without competing.

### Jetpack Compose Setup

```kotlin
// Type.kt
val SyneFamily = FontFamily(
    Font(R.font.syne_bold, FontWeight.Bold),
    Font(R.font.syne_extrabold, FontWeight.ExtraBold)
)

val DMSansFamily = FontFamily(
    Font(R.font.dm_sans_regular, FontWeight.Normal),
    Font(R.font.dm_sans_medium, FontWeight.Medium),
    Font(R.font.dm_sans_semibold, FontWeight.SemiBold)
)
```

### Type Scale

| Token | Font | Weight | Size | Line Height | Use Case |
|---|---|---|---|---|---|
| `DisplayLarge` | Syne | ExtraBold 800 | 32sp | 40sp | Splash screen logo, hero headlines |
| `DisplayMedium` | Syne | Bold 700 | 26sp | 34sp | Section headers on Home |
| `HeadlineLarge` | Syne | Bold 700 | 22sp | 30sp | Product name on Detail screen |
| `HeadlineMedium` | Syne | Bold 700 | 18sp | 26sp | Category titles, Cart totals |
| `TitleLarge` | DM Sans | SemiBold 600 | 16sp | 24sp | Bottom nav labels (active), Dialog titles |
| `TitleMedium` | DM Sans | SemiBold 600 | 14sp | 22sp | Card product names, List item titles |
| `BodyLarge` | DM Sans | Regular 400 | 15sp | 24sp | Product descriptions, Chat messages |
| `BodyMedium` | DM Sans | Regular 400 | 13sp | 20sp | Secondary body copy, onboarding copy |
| `LabelLarge` | DM Sans | SemiBold 600 | 14sp | 20sp | Button labels, Tab labels |
| `LabelMedium` | DM Sans | Medium 500 | 12sp | 18sp | Chips, Tags, Filter labels |
| `LabelSmall` | DM Sans | Medium 500 | 10sp | 16sp | Badges, Timestamps, Meta |

### Typography Rules

- Letter spacing on `DisplayLarge` and `DisplayMedium`: **-0.5sp** (tight, editorial feel).
- Letter spacing on `LabelLarge` (buttons): **+0.5sp** for readability at small size.
- Body text line height must be **1.5–1.6× the font size** at all times.
- Prices and monetary values always use `HeadlineMedium` or `HeadlineLarge` weight in `Primary` color.
- **Never** use Syne below 14sp — it loses its character at small sizes.

---

## 4. Spacing System

Based on an **8dp grid**. All spacing values are multiples of 4.

```kotlin
// Spacing.kt
object Spacing {
    val xxs  = 4.dp
    val xs   = 8.dp
    val sm   = 12.dp
    val md   = 16.dp   // Base unit — most common padding
    val lg   = 24.dp
    val xl   = 32.dp
    val xxl  = 48.dp
    val xxxl = 64.dp
}
```

### Layout Margins

| Context | Value |
|---|---|
| Screen horizontal padding | `16dp` |
| Card internal padding | `16dp` |
| Section vertical gap | `24dp` |
| Between list items | `12dp` |
| Between related elements | `8dp` |
| Bottom nav height | `64dp` (+ system insets) |

---

## 5. Shape & Corner Radius

SmartShop uses **asymmetric-leaning rounded corners** — not pill-shaped, not sharp. The system feels architectural.

```kotlin
// Shape.kt
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),   // Chips, Badges, Tags
    small      = RoundedCornerShape(10.dp),  // Input fields, small cards
    medium     = RoundedCornerShape(16.dp),  // Product cards, Bottom Sheets
    large      = RoundedCornerShape(20.dp),  // Dialogs, Modals, Featured banners
    extraLarge = RoundedCornerShape(28.dp),  // Primary CTA buttons, FAB
)
```

### Shape Rules

- Product cards use `medium` (16dp) with a **1dp `Outline` color border** — no shadow, border creates definition on dark backgrounds.
- Primary buttons use `extraLarge` (28dp) — pill-adjacent but not full pill.
- Bottom sheets use `large` (20dp) on top corners only.
- Never mix sharp corners (`0dp`) with the rounded system.

---

## 6. Iconography

- **Icon set:** [Phosphor Icons](https://phosphoricons.com/) — `Regular` weight for inactive states, `Fill` weight for active/selected states.
- All icons sized at **24dp × 24dp** in standard contexts, **20dp × 20dp** in dense lists.
- Bottom navigation icons: **26dp × 26dp**.
- Cart badge icon uses a **filled** variant of the cart icon with a `Primary` (#E8FF47) badge pill overlaid at the top-right, containing `LabelSmall` text in `OnPrimary` color.
- **No emoji substitutes** for icons under any circumstance.

### Icon Color States

| State | Color |
|---|---|
| Default (inactive) | `Muted` (`#6B6B6B`) |
| Active / Selected | `Primary` (`#E8FF47`) |
| Destructive action | `Error` (`#FF4D4D`) |
| Success state | `Success` (`#3DDB84`) |
| On colored surface | `OnPrimary` (`#0D0D0D`) |

---

## 7. Component Specifications

### 7.1 Buttons

#### Primary Button
```
Background:     Primary (#E8FF47)
Text:           OnPrimary (#0D0D0D)  |  LabelLarge, SemiBold, +0.5sp
Height:         52dp
Corner:         extraLarge (28dp)
Horizontal pad: 24dp
State — Pressed:  scale(0.97) + background darkens to #D4EB30
State — Disabled: Background #2E2E2E, Text #6B6B6B
```

#### Secondary / Outlined Button
```
Background:     Transparent
Border:         1dp Outline (#2E2E2E)
Text:           OnBackground (#F0EDE6)  |  LabelLarge, SemiBold
Height:         52dp
Corner:         extraLarge (28dp)
State — Pressed:  Background SurfaceVariant (#252525)
```

#### Ghost / Text Button
```
Background:     Transparent
Text:           Primary (#E8FF47)  |  LabelLarge, SemiBold
No border, no background
Used for:       secondary actions, "View All", skip links
```

#### Icon Button (Circular)
```
Size:           44dp × 44dp
Background:     SurfaceVariant (#252525)
Corner:         50% (fully circular)
Icon:           24dp, OnBackground
State — Active: Background Primary, Icon OnPrimary
```

---

### 7.2 Product Card

Two variants: **Grid Card** (used in product listing) and **List Card** (used in search results / cart).

#### Grid Card
```
Width:          (screen width - 16*2 - 12) / 2   ≈ 168dp on 360dp screen
Aspect ratio:   Image 1:1 (square), content below
Corner:         medium (16dp)
Border:         1dp Outline (#2E2E2E)
Background:     Surface (#1A1A1A)

Layout (top → bottom):
  [Product Image]     — fills width, 1:1 ratio, corner top-only clipped
  [Brand Label]       — LabelSmall, Muted, 12dp left pad, 8dp top pad
  [Product Name]      — TitleMedium, OnBackground, 12dp pad, 2 lines max, ellipsis
  [Price Row]         — HeadlineMedium Primary  |  original price Muted strikethrough
  [Add to Cart icon]  — trailing IconButton 44dp, bottom-right of price row
```

#### List Card (Search / Cart)
```
Height:         88dp
Corner:         medium (16dp)
Background:     Surface (#1A1A1A)
Border:         1dp Outline

Layout (left → right):
  [Image]       72dp × 72dp, corner small (10dp), 8dp from left edge
  [Text block]  Brand (LabelSmall Muted) → Name (TitleMedium 2 lines) → Price (TitleLarge Primary)
  [Action]      Quantity stepper or Remove icon, trailing
```

---

### 7.3 Input Fields

```
Height:         52dp
Corner:         small (10dp)
Background:     SurfaceVariant (#252525)
Border:         1dp Outline (#2E2E2E)
Text:           BodyLarge, OnBackground
Placeholder:    BodyLarge, Muted
Label (above):  LabelMedium, Muted, 4dp below label

State — Focused:    Border 1.5dp Primary (#E8FF47)
State — Error:      Border 1.5dp Error (#FF4D4D) + error message LabelSmall Error below
State — Disabled:   Background #1A1A1A, Text Muted, opacity 0.4
```

**Search Bar** is a special variant:
```
Height:         48dp
Corner:         extraLarge (28dp) — pill shape
Leading icon:   Search (Phosphor, Muted), 20dp
Trailing icon:  Filter / Clear, 20dp
Background:     SurfaceVariant (#252525)
```

---

### 7.4 Bottom Navigation Bar

```
Height:         64dp + system nav bar inset
Background:     Surface (#1A1A1A)
Top border:     1dp Outline (#2E2E2E)
Items:          5 tabs — Home, Categories, Cart, Map, Chat

Per Tab:
  Icon:         26dp, Muted (inactive) / Primary (active)
  Label:        LabelSmall, Muted (inactive) / Primary (active)
  Active indicator: 32dp wide × 3dp tall pill, Primary, above icon (not background highlight)
```

**Cart Tab Badge:**
```
Badge shape:    Circular pill, min 18dp diameter
Background:     Primary (#E8FF47)
Text:           LabelSmall, OnPrimary, Bold
Position:       Top-right of cart icon, 4dp offset
```

---

### 7.5 Bottom Sheets

```
Background:     Surface (#1A1A1A)
Corner:         large (20dp) top-left + top-right only
Handle bar:     32dp × 4dp, Outline color, centered, 8dp from top
Horizontal pad: 16dp
Overlay scrim:  Scrim (#CC0D0D0D)

Used for:       Filters, Sort options, Payment method selection, Address form
```

---

### 7.6 Chips & Filter Tags

```
Height:         32dp
Corner:         extraSmall (6dp)
Padding:        8dp horizontal, 6dp vertical
Font:           LabelMedium, DM Sans Medium

State — Default:  Background SurfaceVariant, text OnBackground, border 1dp Outline
State — Selected: Background Primary, text OnPrimary, no border
State — Pressed:  scale(0.95) spring animation
```

---

### 7.7 Toast / Snackbar

```
Background:     OnBackground (#F0EDE6)
Text:           BodyMedium, Background (#0D0D0D)
Corner:         medium (16dp)
Action text:    LabelLarge, Primary shown as dark text on light bg — use #0D0D0D
Duration:       2500ms default, 4000ms for errors
Position:       Bottom of screen, 16dp from bottom nav, 16dp horizontal margin
Swipe to dismiss: horizontal swipe only
```

---

### 7.8 Product Detail Screen

Key layout principles:
- **Hero image** takes full width, 60% of screen height, scrollable behind a collapsing app bar.
- Image background uses a **subtle scrim-to-transparent gradient** at bottom to bleed into `Background`.
- Price is always shown in `Primary` color at `HeadlineLarge` size — it should be the most visually dominant piece of text on the screen after the product name.
- "Add to Cart" button is a **fixed bottom bar**: full-width `Primary` button, 52dp height, 16dp padding, above system nav inset.
- Product images use a horizontal pager with dot indicators using `Primary` for active, `Outline` for inactive.

---

### 7.9 Chat Screen (Live Support)

```
Background:     Background (#0D0D0D)

User bubble:
  Background:   Primary (#E8FF47)
  Text:         BodyLarge, OnPrimary
  Corner:       16dp all sides, 4dp bottom-right (sharp tail)
  Alignment:    trailing (right)

Support bubble:
  Background:   Surface (#1A1A1A)
  Border:       1dp Outline
  Text:         BodyLarge, OnBackground
  Corner:       16dp all sides, 4dp bottom-left (sharp tail)
  Alignment:    leading (left)

Timestamp:      LabelSmall, Muted, centered between message groups
Input bar:      Surface background, 1dp top Outline border, 52dp height
Send button:    Icon-only circular 44dp, Primary background, OnPrimary arrow icon
```

---

### 7.10 Map / Store Locator Screen

```
Map occupies:   100% of screen below top app bar
Map style:      Dark map tiles (use Google Maps dark style JSON or Mapbox dark preset)
Store marker:   Custom pin — Primary (#E8FF47) filled circle, 12dp diameter + OnPrimary store icon

Location card (bottom sheet pull-up):
  Shows:        Store name (TitleLarge), Address (BodyMedium Muted), Open hours (LabelMedium)
  CTA:          "Get Directions" — Primary outlined button, full width
```

---

## 8. Motion & Animation

SmartShop uses **spring physics** for interactive feedback and **eased curves** for transitions. No linear animations.

### Spring Presets

```kotlin
object Springs {
    val Default  = spring<Float>(dampingRatio = 0.7f, stiffness = 300f)
    val Bouncy   = spring<Float>(dampingRatio = 0.5f, stiffness = 400f)
    val Stiff    = spring<Float>(dampingRatio = 0.9f, stiffness = 600f)
}
```

### Key Motion Rules

| Interaction | Animation |
|---|---|
| Button press | `scale(0.97f)` spring, 150ms |
| Card press | `scale(0.98f)` spring, 120ms |
| Screen transition | Shared element (product image) + fade, 300ms |
| Bottom sheet open | Slide up + fade in, `spring Default` |
| Toast appear | Slide up from bottom, fade, 250ms |
| Cart badge count change | `scale(1.3f)` bounce then back, `Bouncy` spring |
| Tab switch | Icon `scale(1.15f)` + color tween, 200ms |
| Chip select | Background color tween 150ms + `scale(0.95f)` press |
| Add to Cart | Button pulse (`scale 1.0 → 1.04 → 1.0`) + cart badge bounce |
| Loading state | Shimmer sweep on `SurfaceVariant` placeholders, 1200ms loop |

### Transition Curves

```kotlin
val EaseOutQuart  = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)   // Enter transitions
val EaseInOutCubic = CubicBezierEasing(0.65f, 0f, 0.35f, 1f) // Exit transitions
```

---

## 9. Elevation & Depth

On dark backgrounds, elevation is communicated through **surface color lifting**, not shadows.

| Level | dp | Surface Color | Usage |
|---|---|---|---|
| Level 0 | 0dp | `Background` `#0D0D0D` | Screen canvas |
| Level 1 | 1dp | `Surface` `#1A1A1A` | Cards, bottom nav |
| Level 2 | 2dp | `SurfaceVariant` `#252525` | Input fields, pressed card state |
| Level 3 | 4dp | `#2C2C2C` | Active dropdowns, focused modals |

> No drop shadows. Borders (`1dp Outline`) create card definition. Scrim handles modal depth.

---

## 10. Screen-by-Screen Application

### Onboarding / Splash
- Full `Background` screen.
- SmartShop wordmark centered: `DisplayLarge`, Syne ExtraBold, `Primary` color.
- Tagline below: `BodyLarge`, `Muted`.
- Animated: wordmark fades in, then tagline slides up with `Default` spring.

### Login / Sign Up
- Screen padding: `24dp` horizontal.
- Top: App logo mark (icon only, 48dp) + `HeadlineLarge` title.
- Social auth buttons (Google/Facebook): `Surface` background, `Outline` border.
- Divider: "or continue with email" — `LabelMedium` `Muted` with 1dp `Outline` lines either side.
- Primary action button always at bottom of form, sticks above keyboard.

### Home
- Top bar: Logo left, notification bell + avatar right.
- Hero banner: Full-width horizontal pager, 200dp tall, promotional imagery with `Scrim` gradient overlaid text.
- Section headers: `HeadlineMedium` Syne Bold + "View All" `LabelLarge` Primary ghost button, always in same row.
- Product grid: 2 columns, `12dp` gap, grid cards.

### Product Listing / Category
- Sticky top bar: `Search Bar` + `Filter` icon button.
- Filter chips horizontal scroll below search.
- Sort: bottom sheet trigger.
- Grid: 2 columns standard, 1 column toggle option.

### Product Detail
- Collapsing image hero (parallax scroll effect).
- Sticky bottom: Price + "Add to Cart" primary button.
- Reviews section: star rating in `Primary`, review count in `Muted`.
- Related products: horizontal scroll list.

### Cart
- Each item: `List Card` with quantity stepper (`-` `count` `+` row).
- Swipe left to reveal `Error` red delete action.
- Bottom summary card: sticky, `Surface` background, shows subtotal/total in `HeadlineMedium`.
- "Proceed to Checkout" — Primary button, full width.

### Checkout
- Step indicator: 3 steps (Address → Payment → Confirm), `Primary` for completed/active, `Outline` for upcoming.
- Payment method cards: `Surface` cards with brand logo + radio selection via `Primary` indicator.
- Order summary: collapsible section.

### Order Confirmation
- Centered layout.
- Animated checkmark: `Success` green, drawn with `DrawPath` animation.
- `DisplayMedium` "Order Confirmed!" in `OnBackground`.
- Order number in `LabelMedium` `Muted`.
- Two actions: "Track Order" primary, "Continue Shopping" ghost.

---

## 11. Accessibility

- Minimum touch target: **44dp × 44dp** for all interactive elements.
- Text contrast: All text-on-background combinations must meet **WCAG AA** (4.5:1 for normal text, 3:1 for large text).
  - `OnBackground` on `Background`: ratio ~17:1 ✓
  - `OnPrimary` on `Primary`: ratio ~10:1 ✓
  - `Muted` on `Background`: ratio ~4.7:1 ✓ (just meets AA)
  - `Muted` on `Surface`: ratio ~4.2:1 — only use for non-critical text (metadata, timestamps)
- All icons have `contentDescription` set.
- Bottom nav provides `semanticsRole = Role.Tab`.
- Loading shimmer has `Modifier.semantics { contentDescription = "Loading..." }`.
- Minimum text size: **12sp** (`LabelSmall`) — never smaller.

---

## 12. Voice & Copy Tone

| Context | Tone | Example |
|---|---|---|
| Buttons | Active, verb-first | "Add to Cart" not "Cart" |
| Empty states | Friendly, helpful | "Nothing here yet — let's find something you'll love." |
| Errors | Clear, not technical | "Something went wrong. Give it another try." |
| Success | Brief, warm | "You're in! Welcome to SmartShop." |
| Prices | Always show currency symbol first | "₫ 1,200,000" or "$ 49.99" |

---

## 13. Do's and Don'ts

### Do
- Use `Primary` lime sparingly — it should pop when it appears.
- Maintain consistent 8dp grid spacing throughout.
- Keep product images in consistent aspect ratios (1:1 grid, 4:3 detail hero).
- Use `Syne` only for headings and key UI moments.
- Let dark space breathe — resist the urge to fill every pixel.

### Don't
- Don't use gradients on buttons or nav bars.
- Don't use `Primary` as a text color for body copy — it's an accent, not a neutral.
- Don't place two `Primary` color elements adjacent to each other — they cancel each other out.
- Don't use Syne for any body copy or labels.
- Don't use box shadows — use surface color levels and borders instead.
- Don't use more than 5 colors in any single screen.

---