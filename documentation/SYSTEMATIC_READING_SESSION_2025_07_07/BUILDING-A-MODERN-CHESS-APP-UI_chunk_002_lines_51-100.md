# BUILDING-A-MODERN-CHESS-APP-UI - Chunk 002 (Lines 51-100)

**READ STATUS: ✅ COMPLETELY READ**

## Theme Configuration Details (Lines 51-54)

**Complete Theme Setup**:
```xml
<style name="AppTheme" parent="ThemeOverlay.Material3.DynamicColors.DayNight">
 <!-- ... NoActionBar, etc. can be combined if needed ... -->
</style>
```

## Color Role System (Lines 55-73)

**Dynamic Color Behavior**:
- On supported devices: Material3 theme's color roles (primary, secondary, surface, etc.) replaced with variants extracted from user's wallpaper
- On older Android versions: Falls back to static colors defined in `values/colors.xml`
- **Requirement**: Must define pleasing default color scheme (blue/teal brand colors)

**Material3 Color Roles and Tokens**:
- `colorSurface`
- `?attr/colorPrimary` 
- Other comprehensive set of color roles and tokens
- Can be referenced in layouts and styles for automatic adaptation under dynamic theming

**Dynamic Material You Theming Process**:
- Same app UI adopts different color schemes based on wallpaper
- Android extracts palette: primary, secondary, tertiary, neutral tones from wallpaper
- Applies extracted palette to app's theme
- Chess app's accent and background colors harmonize with user's personal device theme

## Material3 Design Tokens Integration (Lines 74-100)

### Color Tokens (Lines 74-87)
**Best Practice**: Use semantic color attributes rather than hardcoded colors.

**Examples**:
- `?attr/colorPrimary` for primary brand color (dynamic on Android 12+)
- `?attr/colorSurface` for surfaces
- `?attr/colorSurfaceVariant` for emphasized panels

**Dynamic Behavior**: Material3's dynamic overlay automatically updates these to wallpaper-derived colors at runtime.

**Chess App Specific Usage**: 
- Chessboard squares or piece highlights can use `?attr/colorSecondary` or `?attr/colorSecondaryContainer`
- Gets dynamic accent (falling back to teal on older devices)

### Shape Tokens (Lines 88-100)
**Standardized Corner Radius Sizes**: Material3 promotes standardized corner radius sizes.

**XML Shape Appearance References**:
- `?attr/shapeAppearanceSmallComponent`
- `?attr/shapeAppearanceMediumComponent`
- Correspond to predefined corner radii

**Material3 Default Corner Sizes**:
- Extra-small: 4dp
- Small: 8dp  
- Medium: 12dp
- Large: 16dp

**Implementation Example**: MaterialComponents CardView default uses shape appearance for corners.

---

**NEXT CHUNK**: Lines 101-150 (continuing shape tokens and typography tokens)