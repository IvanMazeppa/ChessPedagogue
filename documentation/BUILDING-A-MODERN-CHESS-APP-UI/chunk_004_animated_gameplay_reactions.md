# Chunk 004: Animated UI Reactions for Gameplay Events (Lines 373-519)

## Purpose
Make chess app feel alive and responsive through animations that guide user attention and add high-quality look and feel.

## Piece Movement Animations

### Basic Movement
```kotlin
// Smooth piece sliding
pieceView.animate()
 .x(targetX)
 .y(targetY)
 .setDuration(300)
 .setInterpolator(new AccelerateDecelerateInterpolator())
```

### Enhanced Movement
- **Knight's L-shaped move**: Animate in two segments or use PathInterpolator
- **Bounce landing**: Use OvershootInterpolator for piece going beyond and coming back
- **Physics-based**: SpringAnimation from physics API for natural bounce effect

### Special Moves - Castling
- **Simultaneous animation**: Start animations on both king and rook at same time
- **Emphasis**: Momentarily highlight pieces with faint glow/outline before moving
- **Implementation**: Layer semi-transparent colored View or use ViewOutline for colored shadow
- **Coordination**: Use AnimatorSet to play animations together

## Check Indicator Animations

### Flash Effect
```kotlin
// Warning color flash on king's square
ValueAnimator // alternates alpha of red overlay
// OR
AnimationUtils.loadAnimation // with blink animation XML
```

### Shake Animation
```kotlin
ObjectAnimator.ofFloat(kingView, "translationX", 0f, 15f, -15f, 10f, -10f, 5f, -5f, 0f)
 .setDuration(500)
 .start()
```
- **Enhancement**: Pair with short vibrate using Vibrator for tactile feedback

## Blunder Alerts - Animated "??" Overlays

### Setup
1. Add ImageView (or TextView with large "??") to layout in FrameLayout overlaying board
2. Set initially invisible or `alpha=0`
3. Prepare PNG or SVG/vector drawable for double question mark

### 3D Spin Animation
```kotlin
// Set camera distance to avoid distortion
blunderView.setCameraDistance(8 * blunderView.width)

// Animation sequence
blunderView.visibility = View.VISIBLE
blunderView.alpha = 0f
blunderView.rotationY = 0f
blunderView.animate()
 .rotationY(720f) // two spins around Y axis
 .alpha(1f) // fade in
 .setDuration(800)
 .withEndAction {
 // optionally fade out after delay
 blunderView.animate().alpha(0f).setDuration(300).setStartDelay(500).withEndAction {
 blunderView.visibility = View.GONE
 }
 }
```

### Alternative Effects
- **rotationX**: For flip animation
- **Scale**: Quick pop scale from 0 to 1.2 to 1
- **Lottie animations**: JSON-based vector animations (external dependency)

## Smooth Transitions & Blur for State Changes

### Game End Transition
```kotlin
// Blur and shrink board when results screen appears
boardView.animate()
 .scaleX(0.9f)
 .scaleY(0.9f)
 .alpha(0.5f)
 .setDuration(500)

// Simultaneously fade in results panel
resultPanel.animate()
 .alpha(1f)
 .setDuration(500)
```

### Dynamic Blur Animation
- Gradually increase blur radius on board during transition
- Use ValueAnimator to update blur radius for dynamically adjusting blur

## Material Motion Guidelines

### Design Principles
- **Ease and subtlety**: Significant enough to notice but not jarring
- **Timing**: Blunder "??" appears for 1 second and fades, check highlight pulses twice then stops
- **Interpolators**: Accelerate-decelerate for smooth moves, linear for rotations, bounce for playful effects

### Event-Specific Guidelines

#### Good Move/Achievement
- Gentle particle effect or celebratory icon
- Small confetti burst for checkmate (animated drawables or SVG fade-in)

#### Bad Move/Error
- Warning colors (reds/oranges)
- Jolt animation (shake or flash) to draw attention

#### Neutral State Changes
- Quick crossfade or slide animation for board flipping
- Smooth perspective transitions

## Implementation Framework
Use Android's animation system components:
- **ViewPropertyAnimator**: Simple property animations
- **ObjectAnimator**: More complex property control
- **AnimatorSet**: Coordinate multiple animations
- **Interpolators**: Fine-tune animation feel
- **Physics-based animations**: Natural motion effects

## Benefits
Carefully crafted animations add layer of polish that:
- Significantly improves user experience
- Makes app feel modern and responsive to game state
- Provides visual cues about game events
- Helps users understand consequences of actions (key for teaching app)