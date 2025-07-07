 time). For emphasis, you might momentarily highlight the rook and king during the move. For
 instance, give them a faint glow or outline before moving. This could be done by layering a semi
transparent colored View (or using ViewOutline to add a colored shadow) that fades in/out
 7
around those pieces. A MotionLayout (ConstraintLayout's subclass for animations) could be
 overkill here, but it is capable of coordinating multiple view animations with one trigger, so it's an
 option if you prefer to declare the animation in XML. Otherwise, manually start the two
 animate() calls or use an 
AnimatorSet to play them together.
 • 
• 
• 
• 
• 
Check Indicator: When the king is in check, it should draw the user's attention. A classic
 approach is to flash the king's square or the king piece in a warning color (often red). You can
 implement a flashing effect by toggling the background of the king's square view with a color
 (e.g., start a 
ValueAnimator that alternates the alpha of a red overlay on the square, or simply
 use 
AnimationUtils.loadAnimation with a blink animation XML). Another idea: make the
 king shake slightly, as if alarmed. A shake animation can be done by translating the king piece
 few pixels left-right quickly. For example: 
ObjectAnimator.ofFloat(kingView, "translationX", 0f, 15f,-15f, 10f,-10f, 5f,-5f, 0f).setDuration(500).start()
 This moves the view in a sequence that creates a shake effect. Pair it with a short vibrate using 
Vibrator for tactile feedback if desired (so the user really notices the check).
 Blunder Alerts (Animated "??" overlays): In chess pedagogy apps, when a blunder or mistake
 is made, it's helpful to clearly indicate it. A fun, eye-catching way is to overlay a big "??" graphic or
 icon on the screen briefly. You could prepare a PNG or, better, an SVG/vector drawable for the
 double question mark symbol. Then, when a blunder is detected, do something like:
 Add an ImageView (or TextView with large, stylized "??") to your layout (in a FrameLayout
 overlaying the board).
 Set it initially invisible or 
alpha=0 .
 When triggering, bring it to front, and animate it. For a 3D spin effect as suggested, you can use
 View property animations on rotation. For instance, call 
blunderView.setCameraDistance(8 * blunderView.width) to set a far camera distance
 (to avoid distortion)
 31
 32
 , then animate: 
blunderView.visibility = View.VISIBLE
 blunderView.alpha = 0f
 blunderView.rotationY = 0f
 blunderView.animate()
 .rotationY(720f)
 // two spins around Y axis
 .alpha(1f)
 // fade in
 .setDuration(800)
 .withEndAction {
 // optionally fade out after a delay
 blunderView.animate().alpha(0f).setDuration(300).setStartDelay(500).withEndAction
 {
 blunderView.visibility = View.GONE
 }
 }
 8
This will cause the "??" to spin into view and then disappear. The camera distance ensures a
 realistic perspective during the rotation (preventing extreme fish-eye distortion) . You
 could also animate 
33
 34
 rotationX for a flip, or even scale (e.g. a quick pop scale from 0 to 1.2 to
 1).
 Another approach is to use Lottie animations (JSON-based vector animations) if you want a fancy pre
made animation for blunders (for example, an animated icon that shakes or a cartoon explosion). Lottie
 can be integrated as a dependency and the animation played in an 
LottieAnimationView . However,
 this introduces an external asset; since our goal is to stick to platform tools, a simple custom animation
 as above should suffice.
 • 
• 
Smooth Transitions & Blur for State Changes: Beyond individual move animations, think of the
 overall UI transitions. For instance, when a game ends and a results screen is shown, you could
 blur the board in the background to indicate that it's inactive, and perhaps zoom it out slightly,
 while fading in the results panel on top. This layered transition makes the experience feel more
 cohesive than a hard cut. You can achieve this by combining animations: e.g.,
 boardView.animate().scaleX(0.9f).scaleY(0.9f).alpha(0.5f).setDuration(500)
 to 
shrink 
and 
dim 
the 
board, 
while 
simultaneously
 resultPanel.animate().alpha(1f).setDuration(500) . If using the API 31 blur, maybe
 also gradually increase blur radius on the board for that duration for a dynamically adjusting
 blur. Android's property animations allow you to animate almost any View property. 
Attention to Material Motion: Since we are using Material3, it's worth noting Material Design's
 motion guidelines emphasize ease and subtlety. UI reactions should be significant enough to
 notice but not jarring. For example, a blunder "??" might appear for 1 second and fade – it
 shouldn't stay too long. A check highlight might pulse twice then stop. Using the Interpolator
 classes can refine the feel: accelerate–decelerate for smooth moves, linear for continuous
 rotations, bounce for playful effects, etc. Android's animation system and interpolators (and even
 physics-based animations) let you fine-tune these.
 28
 Remember that animations should reinforce the event's meaning. As the Android docs say, they serve
 as visual cues to the user about what's happening . For a chess app: - Good move or achievement: