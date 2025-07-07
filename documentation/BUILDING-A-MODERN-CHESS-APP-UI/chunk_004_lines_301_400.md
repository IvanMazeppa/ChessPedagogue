 piece with a glow (by sampling the view and mixing colors) when in check. While custom shaders
 aren't common for everyday apps, API 35 gives that power if you want truly unique visuals. Keep
 in mind performance and device support (limit to high-end devices or use sparingly, as complex
 shaders can be costly).
 • 
• 
• 
• 
• 
Material 3 Components: Ensure you use Material3 versions of widgets in XML. This means
 using components like 
<com.google.android.material.button.MaterialButton> for
 fancy buttons (which will automatically use dynamic color and Material3 styling), Material3
 TopAppBar for your toolbars (with updated large title styles and scroll behavior),
 MaterialTextView (to get the Material textAppearance styles easily), MaterialSwitch, etc. The
 Material Components library will handle styling these according to your theme. For example, a
 MaterialButton by default uses the 
?attr/colorPrimary as its background tint (so it will
 match your dynamic accent color). Material3 also introduced new container styles (filled,
 outlined, elevated) for many components:
 Use Outlined styles where appropriate (e.g. an outlined card or button for less emphasis). For
 instance, a Card with 
26
 style="@style/Widget.Material3.CardView.Outlined" will have a
 stroke and no elevation .
 Use Elevated variants to draw attention. An Elevated Card
 27
 (Widget.Material3.CardView.Elevated) has a slight shadow and uses a lower surface
 color by default
 . Perhaps the active game board or the move list could be in an elevated
 container to pop out.
 Take advantage of MaterialShape capabilities. Material3 components respect shape theming 
by adjusting 
shapeAppearanceSmall/Medium/Large in your theme, you can globally change
 corner radii on buttons, text fields, etc. For example, if you want all buttons to be pill-shaped,
 you can provide a ShapeAppearance with fully rounded corners as the small component shape.
 This might not be needed for a chess app, but it's good to know (maybe you want dialog corners
 more rounded than default – just override 
shapeAppearanceMediumComponent in theme to
 e.g. 16dp).
 Material You Animations: Material3 components also come with built-in motion for state
 changes (like touch ripples, transitions when using 
MaterialContainerTransform for
 fragment transitions, etc.). While these are more on the UX side, be aware that using these
 components gives you consistency and polish "for free." For instance, a 
MaterialButton or
 MaterialCardView will handle pressed/released animations (ripples) in the new Material style
 automatically (the ripple color also adapts to dynamic theme, e.g. uses a desaturated version of
 your accent).
 6
• 
Edge-to-Edge Design: A modern app typically draws behind system bars for an immersive look.
 Since Android 15 and Material You encourage colorful, edge-to-edge UIs, consider configuring
 your activity layouts to be full-screen with translucent system bars. In your theme, you can set: 
<item name="android:statusBarColor">@android:color/transparent</item>
 <item name="android:navigationBarColor">@android:color/transparent</
 item>
 <item name="android:windowLightStatusBar">true</item> <!-- adjust 
depending on wallpaper contrast -->
 and use 
WindowInsetsController or theme attributes to ensure icons are visible (light/dark)
 appropriately. Material3 has an 
EdgeToEdge theme variant as well. Edge-to-edge allows the
 wallpaper or your app content to extend under the status bar; combined with dynamic theming,
 this can make your app feel integrated with the device. For example, the chessboard could
 extend to the top of the screen behind a translucent status bar, with the Material3 TopAppBar
 coloring the status bar area to match. Or the splash screen image could bleed into the system
 bars area, giving a seamless look.
 In summary, Android 15's UI toolkit gives us the ability to create a polished, modern interface with
 relatively little custom drawing code – just by configuring Material3 correctly and using RenderEffect/
 Window blurs. Next, we focus on making the app interactive and lively, so that chess gameplay events
 are communicated visually to the user.
 Animated UI Reactions for Gameplay Events
 To make the chess app feel alive and responsive, we should animate important gameplay events.
 Animations not only guide the user's attention but also add a high-quality look and feel to the app .
 Below are suggestions for key events, with implementation tips using the classic View system (no
 Jetpack Compose needed):
 28
 • 
• 
Piece Movement Animations: Rather than pieces "teleporting" from one square to another,
 animate their motion. If your chess pieces are ImageViews or custom Views on a grid, you can
 use property animations to move them. For example, if a user moves a piece from A2 to A4, you
 can 
call
 pieceView.animate().x(targetX).y(targetY).setDuration(300).setInterpolator(new 
AccelerateDecelerateInterpolator()) . This will smoothly slide the piece to the new
 coordinates. Ensure the animation runs on the UI thread (post it or use an 
ObjectAnimator if
 you prefer). For a knight's L-shaped move, you might animate in two segments (or use a
 PathInterpolator to follow an L path). These subtle animations make the game feel more
 dynamic. You can even add a slight ease-out bounce when a piece lands: for example, use an
 OvershootInterpolator so the piece goes a few pixels beyond and comes back, mimicking
 inertia. (Android's 
29
 30
 SpringAnimation from the physics API could also achieve this naturally for
 a bounce effect .) 
Castling and Special Moves: Castling involves moving two pieces (king and rook)
 simultaneously – you can animate them together (start animations on both views at the same