# Chunk 002: Glassmorphism Design - Translucent Panels with Blur Effects (Lines 128-269)

## Glassmorphism Fundamentals
**Definition**: Modern UI trend characterized by frosted-glass like panels – translucent surfaces with blurred background

**Implementation**: Two main components - transparency and blur
- Elevates look of menus, side panels (move list, settings popups), overlays (pause screens)
- Aligns with blue/teal accented, modern aesthetic

## Translucent UI Panels

### Color Definition
```xml
<color name="glass_panel_bg">#80FFFFFF</color> <!-- white at 50% opacity -->
<!-- OR tinted glass: -->
<color name="glass_tinted_bg">#80123456</color> <!-- subtle blue/teal with transparency -->
```

### MaterialCardView Implementation
```xml
<com.google.android.material.card.MaterialCardView
 style="@style/GlassPanelCard"
 android:layout_width="match_parent"
 android:layout_height="wrap_content"
 app:cardBackgroundColor="@color/glass_panel_bg"
 app:strokeColor="@color/white_20"
 app:strokeWidth="1dp"
 app:cardElevation="0dp">
 <!-- Panel content here (texts, etc.) -->
</com.google.android.material.card.MaterialCardView>
```

### Style Definition
```xml
<style name="GlassPanelCard" parent="Widget.Material3.CardView.Filled">
 <item name="shapeAppearance">?attr/shapeAppearanceCornerMedium</item>
 <item name="cardElevation">0dp</item>
 <!-- Background color and stroke can also be set here or directly in layout -->
</style>
```

**Key Specifications:**
- Medium rounded corners, no drop-shadow, translucent fill, optional thin border
- **Opacity Range**: 10–20% opacity for readability vs glass effect balance
- Too transparent = illegible text; too opaque = lost glass effect

## Applying Blur (Frosted Background)

### In-Window Blur (RenderEffect - API 31+)
**Primary Method:**
```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
 val radius = 20f
 viewBehind.setRenderEffect(RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP))
}
```

**Usage Approaches:**
1. Blur background behind panel (chess board view)
2. Blur entire activity background when dialog/panel opens
3. Wrap UI in container and blur container except panel

**Performance Notes:**
- Hardware-accelerated and efficient on Android 12+ devices
- Only works API 31+, requires fallback for older devices
- Remove blur: `viewBehind.setRenderEffect(null)`

### Fallback for Pre-Android 12
**BlurView Library (by Dimezis):**
```xml
<eightbitlab.com.blurview.BlurView
 android:layout_width="match_parent"
 android:layout_height="match_parent"
 app:blurOverlayColor="@color/white_20"/>
```
- Handles blurring bitmap of underlying view each frame
- Has overlay color attribute
- Useful for real-time blur on pre-Android 12

### Cross-Window Blur (API 31+)
**For Dialogs/Pop-ups:**
- Set `android:windowIsTranslucent=true`
- Set `android:windowBackgroundBlurRadius=...`
- OR call `dialog.getWindow().setBackgroundBlurRadius(radius)`
- Combine with translucent window background color
- `Window#setBlurBehindRadius()` for system-wide blur

## Implementation Tip
**Always pair blur with translucent tint:**
- Usually neutral or on-brand color (e.g. white at 20% opacity)
- Ensures text legibility and milky glass appearance
- Can use dynamic surface color with alpha for wallpaper-matching glass

## Practical Chess App Usage
- Splash screen: blurred background image with centered logo
- Competitive mode: translucent overlay for match settings with live game blurred behind
- Move list panels, settings popups with glassmorphism style