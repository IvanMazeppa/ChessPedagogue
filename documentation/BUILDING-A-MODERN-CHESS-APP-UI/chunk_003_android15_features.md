# Chunk 003: Android 15 UI Features - Blur, Shaders, and Material Components (Lines 270-372)

## Android 15 (API 35) Capabilities
Building upon UI capabilities from Android 12–14, leveraging latest features for enhanced appearance and performance.

## Real-time Blur with RenderEffect

### Dynamic Focus Effects
- **Usage**: Blur game board when pause menu appears, restore clarity when resumed
- **Animation**: Animate blur radius from 0 to higher value for smooth transitions
- **Implementation**: 
```kotlin
// Use ValueAnimator to update blur radius and call setRenderEffect repeatedly
// All GPU accelerated for fluid animation
```

### Performance
- Efficiently supported on most modern devices by API 35
- Hardware acceleration ensures smooth performance

## Advanced Shaders (AGSL)

### Android Graphics Shading Language (API 33+)
- **APIs**: `RuntimeShader` / `RuntimeShaderEffect`
- **Capability**: Write custom fragment shader code for creative effects

### Practical Applications
1. **Subtle vignette or gradient** on chessboard
2. **Ripple distortion** for "invalid move" indication
3. **Animated background** for menus (moving gradient/noise pattern behind translucent panel)
4. **Piece highlighting** with glow when in check (sample view and mix colors)

### Implementation
```kotlin
// Apply RuntimeShader via RenderEffect.createRuntimeShaderEffect(shader, "background")
// "background" uniform represents the view's content
```

### Considerations
- Advanced feature, use sparingly
- Limit to high-end devices for complex shaders
- Performance cost for complex shaders

## Material 3 Components

### Essential Widget Updates
**Replace with Material3 versions:**
- `<com.google.android.material.button.MaterialButton>` for fancy buttons
- Material3 TopAppBar with updated large title styles and scroll behavior
- MaterialTextView for Material textAppearance styles
- MaterialSwitch, etc.

### Container Styles
1. **Outlined styles**: `style="@style/Widget.Material3.CardView.Outlined"` (stroke, no elevation)
2. **Elevated variants**: `Widget.Material3.CardView.Elevated` (slight shadow, lower surface color)
3. **Shape theming**: Adjust `shapeAppearanceSmall/Medium/Large` globally

### Automatic Features
- **Dynamic color integration**: MaterialButton uses `?attr/colorPrimary` automatically
- **Material You animations**: Built-in motion for state changes, touch ripples
- **Consistency**: Components handle styling according to theme automatically

## Edge-to-Edge Design

### Modern Immersive Look
**Theme Configuration:**
```xml
<item name="android:statusBarColor">@android:color/transparent</item>
<item name="android:navigationBarColor">@android:color/transparent</item>
<item name="android:windowLightStatusBar">true</item> <!-- adjust depending on wallpaper contrast -->
```

### Implementation
- Use `WindowInsetsController` or theme attributes for proper icon visibility
- Material3 has `EdgeToEdge` theme variant
- Wallpaper/app content extends under status bar
- Combined with dynamic theming for device integration

### Chess App Examples
- Chessboard extends to top behind translucent status bar
- Material3 TopAppBar colors status bar area to match
- Splash screen image bleeds into system bars for seamless look

## Summary Benefits
Android 15's UI toolkit enables:
- Polished, modern interface with minimal custom drawing code
- Proper Material3 configuration + RenderEffect/Window blurs
- Focus on interactive and lively chess gameplay events
- Visual communication to users through UI reactions