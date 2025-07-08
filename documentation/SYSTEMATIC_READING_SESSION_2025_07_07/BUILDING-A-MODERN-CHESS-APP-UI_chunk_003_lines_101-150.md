# BUILDING-A-MODERN-CHESS-APP-UI - Chunk 003 (Lines 101-150)

**READ STATUS: ✅ COMPLETELY READ**

## Shape Tokens Implementation (Lines 101-107)

**Specific Implementation Examples**:
- `shapeAppearanceCornerMedium` for medium round corners
- Apply in styles with: `<item name="shapeAppearance">?attr/shapeAppearanceCornerLarge</item>`
- Alternative: Use specific style like `@style/ShapeAppearance.Material3.Corner.Large`

**Benefits**: Ensures panels and dialog corners are consistently rounded in modern style without manually specifying every radius.

## Typography Tokens (Lines 107-123)

**Material3 Text Styles**: Provides 15 standardized text styles:
- DisplayLarge
- HeadlineSmall  
- TitleMedium
- BodyLarge
- LabelSmall
- etc.

**XML Implementation**:
- Use `android:textAppearance="@style/TextAppearance.Material3.TitleLarge"` for dialog headings
- Use theme attributes like `?attr/textAppearanceBodyMedium` on TextView for automatic theme pickup

**Customization**: Can override font family or size by extending text appearances in styles.

**Global Typography Control**: Using tokens makes it easy to adjust global typography from theme. Example: Override `textAppearanceTitleLarge` in theme to point to custom style for chess-themed font across all title texts.

## Foundation Summary (Lines 124-127)

**Material3 Setup Result**: By setting up Material3 theme with dynamic colors and token-based styles:
- All default Material components (Buttons, Cards, TopAppBar, etc.) follow Material You behaviors
- Foundation laid for next steps: glassmorphism aesthetic and interactive flourishes

## Glassmorphism Introduction (Lines 128-133)

**Definition**: Popular modern UI trend characterized by frosted-glass like panels with translucent surfaces and blurred background.

**Chess App Application**: Style can elevate:
- Menus
- Side panels (move list, settings popups)  
- Overlays (pause screens)
- Aligns with blue/teal accented, modern aesthetic

**Core Requirements**: Two main things needed:
1. Transparency
2. Blur

## Translucent UI Panels Implementation (Lines 134-150)

### Color Definition (Lines 135-140)
**Semi-transparent Background**: Use semi-transparent background on View.

**Color Examples**:
```xml
<color name="glass_panel_bg">#80FFFFFF</color> <!-- white at 50% opacity -->
```

**Tinted Glass Option**: Use subtle blue/teal with transparency (e.g. `#80123456`)

**Application**: Apply as background or backgroundTint of panel view in XML layouts or styles.

### MaterialCardView Implementation (Lines 141-150)
**Recommended Approach**: Use MaterialCardView from Material Components for glass panels.

**Benefits**: Supports stroke and corner radius easily.

**Implementation Example**:
```xml
<com.google.android.material.card.MaterialCardView
    style="@style/GlassPanelCard"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:cardBackgroundColor="@color/glass_panel_bg"
    app:strokeColor="@color/white_20"
```

---

**NEXT CHUNK**: Lines 151-200 (continuing MaterialCardView implementation and blur effects)