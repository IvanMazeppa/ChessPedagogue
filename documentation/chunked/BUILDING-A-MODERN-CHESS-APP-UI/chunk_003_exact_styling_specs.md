# Chunk 003: Exact Styling Specifications (Lines 500-599)

## CRITICAL GLASS STYLING SPECS:

### GlassCard Implementation:
- **Blur Application**: `.graphicsLayer { renderEffect = blur }`
- **Background**: `MaterialTheme.colorScheme.surface.copy(alpha = 0.18f)` - EXACT 18% opacity
- **Corner Radius**: `RoundedCornerShape(24.dp)` - EXACT specification
- **Border**: `1.dp` with `onSurface.copy(alpha = 0.08f)` - 8% opacity border
- **Padding**: `8.dp` internal padding

### GlassButton Specs:
- **Height**: `48.dp` - EXACT button height
- **Layout**: `Row` with center alignment
- **Interaction**: `.clickable(onClick = onClick)`
- **Content**: `RowScope` for button content

### CapturedTray Specs:
- **Height**: `36.dp` - EXACT height specification
- **Padding**: `vertical = 4.dp`
- **Spacing**: `spacedBy(4.dp)` between pieces
- **Layout**: Horizontal `Row`

## MAJOR UPGRADE SPECIFICATIONS (Lines 581-595):

### Enhanced Features Identified:
1. **Warm-tinted light squares** - board contrast enhancement
2. **Side-mounted captured trays** - CONTRADICTS earlier top/bottom spec!
3. **Radial glow behind move list** - pulsing animation
4. **Material You accents** - dynamic palette integration
5. **3×3 button grid** - CONTRADICTS earlier 2×4 spec!
6. **Elevated move list** - stronger blur and shadow

### CRITICAL CONTRADICTION FOUND:
- Lines 452-466: Shows 2×4 button grid implementation
- Line 591: Mentions "Compact 3×3 grid for seven buttons"
- Lines 434-444: Shows top/bottom captured trays
- Line 585: Mentions "Side-mounted captured trays"

## IMPLEMENTATION PRIORITY:
**The document contains conflicting specifications!** Next session must:
1. Clarify which layout to use (2×4 vs 3×3 buttons)
2. Clarify captured tray placement (top/bottom vs side-mounted)
3. Implement the clarified specifications exactly

## STATUS:
- Read: Lines 500-599
- CRITICAL: Found contradictory specifications
- Next: Continue reading for more layout details