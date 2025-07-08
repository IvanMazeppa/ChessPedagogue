# BUILDING-A-MODERN-CHESS-APP-UI - Chunk 004 (Lines 151-200)

**READ STATUS: ✅ COMPLETELY READ**

## MaterialCardView Complete Implementation (Lines 151-175)

### Complete XML Structure (Lines 151-154)
```xml
app:strokeWidth="1dp"
app:cardElevation="0dp">
<!-- Panel content here (texts, etc.) -->
</com.google.android.material.card.MaterialCardView>
```

### Style Definition (Lines 155-169)
**GlassPanelCard Style in styles.xml**:
```xml
<style name="GlassPanelCard" parent="Widget.Material3.CardView.Filled">
    <item name="shapeAppearance">?attr/shapeAppearanceCornerMedium</item>
    <item name="cardElevation">0dp</item>
    <!-- Background color and stroke can also be set here or directly in layout as above -->
</style>
```

**Note**: Background color and stroke can be set either in style or directly in layout.

### Visual Result and Best Practices (Lines 170-175)
**Card Characteristics**:
- Medium rounded corners
- No drop-shadow  
- Translucent fill
- Optional thin border

**Background Effect**: Semi-transparent fill allows background content to show through faintly.

**Opacity Guidelines for Readability**:
- **Too transparent**: Text becomes illegible
- **Too opaque**: Glass effect is lost
- **Recommended**: 10-20% opacity white or surface-colored background
- **Enhancement**: Subtle border or inner shadow to enhance "glass" look

## Blur Implementation Introduction (Lines 176-182)

**Key Principle**: Transparency alone gives see-through panel, but the magic of frosted glass comes from blurring whatever is behind the panel.

**Android Blur APIs**: Android now provides high-performance blur APIs.

**Two Blur Scenarios**:
1. Blur within the same window (in-app views)
2. Cross-window blur (covered later)

## In-App Blur with RenderEffect (Lines 183-200)

### API Introduction (Lines 183-188)
**Android 12 (API 31) RenderEffect**: Introduced ability to blur content of View hierarchy.

**Implementation Method**:
```kotlin
view.setRenderEffect(RenderEffect.createBlurEffect(radiusX, radiusY, Shader.TileMode.CLAMP))
```

**Effect**: Renders the view (and its children) with Gaussian blur of specified radius.

### Glassmorphism Application Approaches (Lines 189-195)

**Approach 1 - Background Blur**: Blur the background behind your panel.
- Example: If UI layout has ImageView or SurfaceView drawing chess board behind panel
- Apply RenderEffect blur to background view when panel is shown
- Result: Board beneath appears defocused through translucent card

**Approach 2 - Container Blur**: Wrap relevant UI part in container and blur container except the panel.
- Example: Overlay blur on entire activity background when dialog/panel opens

### Code Implementation (Lines 195-200)
**Simple Kotlin Example**:
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    val radius = 20f
    viewBehind.setRenderEffect(RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP))
```

**API Version Check**: Requires Android 12+ (Build.VERSION_CODES.S)
**Blur Radius**: 20f provides nice frosted look (adjustable for taste and performance)

---

**NEXT CHUNK**: Lines 201-250 (continuing RenderEffect implementation and performance considerations)