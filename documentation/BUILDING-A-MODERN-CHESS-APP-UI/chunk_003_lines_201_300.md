 }
 Here, 
viewBehind could be the chess board view or a layout containing the game board. A
 radius of ~20f provides a nice frosted look (adjust to taste and performance). Remember to
 remove or reduce the blur when it's no longer needed (you can call 
viewBehind.setRenderEffect(null) to clear it).
 Performance: RenderEffect is hardware-accelerated and efficient on Android 12+ devices, but it
 only works on API 31 and above. If your app targets older devices, you'll need a fallback. One
 common solution is using a third-party library like BlurView (by Dimezis) which internally
 handles blurring a bitmap of the underlying view each frame. BlurView can be added in your
 layout XML (e.g. as a custom view that you position behind your Card) and you provide it with the
 behind view's reference to blur. It even has an attribute for an overlay color. For example,
 BlurView usage in XML might look like: 
<eightbitlab.com.blurview.BlurView
 android:layout_width="match_parent"
 android:layout_height="match_parent"
 app:blurOverlayColor="@color/white_20"/>
 which would blur everything under that view with a 20% white tint
 16
 . This library is helpful if
 you want real-time blur on pre-Android 12. If targeting only API 31+, RenderEffect is preferred
 4
(no external dependency needed). On older Android, another fallback (less dynamic) is
 RenderScript (now deprecated) or simply using a static frosted glass image backdrop.
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