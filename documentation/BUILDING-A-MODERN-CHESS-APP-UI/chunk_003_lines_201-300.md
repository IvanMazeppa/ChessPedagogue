# BUILDING-A-MODERN-CHESS-APP-UI.md - Chunk 3 (Lines 201-300)

• 
22
 Blur across windows (window background blur): Android 12 also added cross-window blur
 APIs that blur what's behind your window, ideal for dialogs or pop-ups . For instance, a modal
 dialog can blur the activity behind it by setting window blur parameters. If you have a separate
 Activity or Dialog for, say, a pause menu or settings screen, you can enable a blur behind effect: 
17
 ◦ 
In your dialog's theme, set 
android:windowIsTranslucent=true and 
android:windowBackgroundBlurRadius=... (or call 
dialog.getWindow().setBackgroundBlurRadius(radius) in code)
 18
 . Combine
 this with a translucent window background color. This results in a beautiful depth effect:
 the entire app behind the dialog is blurred (giving a frosted backdrop) . 
19
 ◦ 
There's also 
20
 Window#setBlurBehindRadius() for blurring everything behind a
 window (even other apps). This is mostly useful for system dialogs or maybe an overlay
 Activity. Use these with care (and note that not all devices or OEMs allow heavy blur for
 performance reasons ).
 21
 17
 Frosted glass blur effect: The screenshot above shows a translucent dialog with a blurred background
 ("Hello blurry world!"). Android's background blur API creates this frosted glass panel by blurring the
 content behind the window . To achieve a similar effect in-app, combine a translucent panel
 (MaterialCardView with an alpha-tinted background) and apply a blur to whatever is behind that panel
 . The result is a modern glassmorphic UI element that adds depth while maintaining readability of
 foreground content.
 In practice, for our chess app using classic Views, you might use either the in-window blur (RenderEffect
 on a view) or open certain overlays as dialogs with window blur. For example, the splash screen could
 have a blurred background image with your logo centered, or the "competitive mode" screen might
 show a translucent overlay for match settings with the live game blurred behind it, signaling focus on
 the settings panel.
 Tip: When using blur, always pair it with a slight translucent tint (usually a neutral or on-brand color)
 on the blurred panel. The tint (e.g. white at 20% opacity) over the blur helps ensure text on the panel is
 legible 
23
 and gives that milky glass appearance . In Material3, you can use
 materialCardViewStyle or theming to ensure all such panels use a consistent translucent overlay
 color (which could even come from dynamic color – e.g. use dynamic surface color with alpha for a
 subtly tinted glass that matches the wallpaper palette).
 Leveraging Android 15 UI Features: Blur, Shaders, and Material
 Components
 Android 15 (API 35) builds upon the UI capabilities introduced in Android 12–14. Our chess app can take
 advantage of these latest features to enhance appearance and performance:
 • 
Real-time Blur with RenderEffect: As discussed, RenderEffect (API 31+) lets you blur views
 easily. By API 35, most modern devices support this efficiently. Use it to create dynamic focus
 effects – for example, blurring the game board when a pause menu appears, then restoring
 clarity when resumed. You can animate this blur (animate the radius from 0 to a higher value) to
 make the transition smooth. A simple way is using a 
ValueAnimator that updates the blur
 5
radius and calls 
setRenderEffect repeatedly. Since this is all GPU accelerated, the animation
 will appear fluid.
 • 
Advanced Shaders (AGSL): Android 13 introduced Android Graphics Shading Language
 (AGSL) and the 
RuntimeShader /
 RuntimeShaderEffect APIs. This means you can write
 custom fragment shader code to apply to Views for creative effects. For example, you could
 create a shader that overlays a subtle vignette or gradient on the chessboard, or one that
 produces a ripple distortion for an "invalid move" indication. You can apply a RuntimeShader via
 RenderEffect.createRuntimeShaderEffect(shader, "background") on a view .
 The shader can access the original content as a uniform (the 
25
 24
 "background" uniform
 represents the view's content ). This is quite advanced, but one practical use-case: an
 animated background for menus (perhaps a moving gradient or noise pattern behind a
 translucent panel) to give a premium feel. Another idea: a shader that highlights a square or
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