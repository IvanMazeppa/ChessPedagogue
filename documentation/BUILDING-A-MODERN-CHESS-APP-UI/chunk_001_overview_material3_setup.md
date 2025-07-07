# Chunk 001: Overview & Material 3 Setup (Lines 1-127)

## Document Overview
- **Goal**: Create advanced, aesthetically polished chess app interface using Android 15 (API 35) features in XML-based View system
- **Key Technologies**: Material Design 3 (Material You), glassmorphic visual style with blue/teal accents
- **Features**: RenderEffect blurs, custom shaders, animated UI reactions for chess events
- **Scope**: Extend modern style across all screens with consistent Material 3 theming

## Material 3 Setup

### Theme Configuration
```xml
<style name="AppTheme" parent="Theme.Material3.DayNight.NoActionBar">
 <!-- Material3 color, shape, and typography assignments will go here -->
</style>
```

### Dynamic Color (Material You) Implementation
```kotlin
class MyApplication : Application() {
 override fun onCreate() {
 super.onCreate()
 DynamicColors.applyToActivitiesIfAvailable(this) // Enable Material You colors
 }
}
```

**Alternative Theme Approach:**
```xml
<style name="AppTheme" parent="ThemeOverlay.Material3.DynamicColors.DayNight">
 <!-- ... NoActionBar, etc. can be combined if needed ... -->
</style>
```

### Material3 Tokens in XML

#### Color Tokens
- Use semantic color attributes: `?attr/colorPrimary`, `?attr/colorSurface`, `?attr/colorSurfaceVariant`
- Dynamic overlay automatically updates to wallpaper-derived colors on Android 12+
- Fallback to static blue/teal brand colors on older devices

#### Shape Tokens
- Standard corner radius sizes: 4dp (extra-small), 8dp (small), 12dp (medium), 16dp (large)
- Reference: `?attr/shapeAppearanceSmallComponent`, `?attr/shapeAppearanceMediumComponent`
- Example: `<item name="shapeAppearance">?attr/shapeAppearanceCornerLarge</item>`

#### Typography Tokens
- 15 standardized text styles: DisplayLarge, HeadlineSmall, TitleMedium, BodyLarge, LabelSmall
- Usage: `android:textAppearance="@style/TextAppearance.Material3.TitleLarge"`
- Theme attributes: `?attr/textAppearanceBodyMedium`

## Foundation Complete
Material3 theme with dynamic colors and token-based styles provides foundation for:
- All default Material components follow Material You behaviors
- Ready for glassmorphism aesthetic and interactive flourishes