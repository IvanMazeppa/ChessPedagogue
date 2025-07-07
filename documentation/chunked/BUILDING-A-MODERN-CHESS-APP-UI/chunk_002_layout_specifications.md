# Chunk 002: Layout Specifications (Lines 400-499)

## CRITICAL LAYOUT REQUIREMENTS:

### Overall Structure:
- **Material You Integration**: `dynamicLightColorScheme(LocalContext.current)`
- **Full screen layout**: `fillMaxSize()` with `Arrangement.SpaceBetween`
- **16dp padding**: Standard Material 3 spacing

### Captured Pieces Layout:
- **Position**: `Alignment.TopCenter` and `Alignment.BottomCenter`
- **NOT side-mounted** as I incorrectly assumed!
- **Height**: 36dp trays for captured pieces
- **Spacing**: 4dp vertical padding

### Button Layout - CRITICAL CORRECTION:
- **2×4 grid, NOT 3×3** as mentioned elsewhere
- **Buttons**: `["Pause","TTS","AI","A/V","Norm","Design","More"]`
- **Layout**: `chunked(2)` creates 2-button rows
- **Spacing**: 8dp between buttons
- **Position**: Left side of controls row

### Move List Panel:
- **Width**: `140.dp` - specific measurement
- **Position**: Right side of controls row
- **Content**: Title + numbered moves list
- **Typography**: `MaterialTheme.typography.titleMedium` for title

### Chess Board:
- **Container**: `GlassCard` with `size(300.dp)`
- **Centered**: `contentAlignment = Alignment.Center`
- **Weight**: `weight(1f)` for responsive sizing

## GLASS CARD IMPLEMENTATION:
- **Blur**: `RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)` for API 31+
- **Fallback**: 10dp blur for older APIs
- **API Check**: `Build.VERSION.SDK_INT >= 31`

## CRITICAL ERRORS IN MY IMPLEMENTATION:
1. ❌ I assumed side-mounted captured trays - WRONG
2. ❌ I thought 3×3 button grid - WRONG, it's 2×4
3. ❌ I didn't implement the exact button labels listed

## STATUS:
- Read: Lines 400-499
- Key finding: Exact layout structure with measurements
- Next: Read opacity and styling specifications