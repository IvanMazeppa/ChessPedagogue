 shapeAppearanceCornerMedium (medium round
 corners) . You can apply these in styles. E.g., to give a view large rounded corners: 
<item name="shapeAppearance">?attr/shapeAppearanceCornerLarge</item>
 or use a specific style like 
@style/ShapeAppearance.Material3.Corner.Large . This ensures your
 panels and dialog corners are consistently rounded in a modern style without manually specifying every
 radius. - Typography tokens: Material3 provides 15 standardized text styles (e.g. DisplayLarge,
 HeadlineSmall, TitleMedium, BodyLarge, LabelSmall, etc.) . In XML you can use Material3's
 TextAppearance 
11
 styles. 
For 
instance, 
set 
android:textAppearance="@style/
 TextAppearance.Material3.TitleLarge" for a heading in a dialog, or use theme attributes like ?
 attr/textAppearanceBodyMedium on a TextView to automatically pick up the theme's BodyMedium
 style. You can override the font family or size by extending these text appearances in your styles if
 needed. Using these tokens makes it easy to adjust global typography from the theme – for example, if
 2
you want all Title texts to use a chess-themed font, you can override 
textAppearanceTitleLarge in
 your theme to point to a custom style.
 By setting up the Material3 theme with dynamic colors and token-based styles, you lay the foundation:
 all default Material components (Buttons, Cards, TopAppBar, etc.) will now follow Material You
 behaviors. The next steps will build on this – applying the glassmorphism aesthetic and adding the
 interactive flourishes.
 Glassmorphism Design: Translucent Panels with Blur Effects
 12
 Glassmorphism is a popular modern UI trend characterized by frosted-glass like panels – translucent
 surfaces with a blurred background . In our chess app, this style can elevate the look of menus, side
 panels (e.g. move list, settings popups), or overlays (like pause screens), aligning with the blue/teal
 accented, modern aesthetic. Achieving this involves two main things: transparency and blur.
 • 
Translucent UI Panels: To create a glass-like panel, use a semi-transparent background on your
 View. For example, define a color in 
colors.xml such as:
 <color name="glass_panel_bg">#80FFFFFF</color> <!-- white at 50% opacity -->
 or if you prefer a tinted glass, use a subtle blue/teal with transparency (e.g. 
#80123456 ). In XML
 layouts or styles, apply this as the background or backgroundTint of your panel view. A convenient
 approach is to use MaterialCardView (from Material Components) for such panels because it supports
 stroke and corner radius easily:
 <com.google.android.material.card.MaterialCardView
 style="@style/GlassPanelCard"
 android:layout_width="match_parent"
 android:layout_height="wrap_content"
 app:cardBackgroundColor="@color/glass_panel_bg"
 app:strokeColor="@color/white_20"
 outline-->
 app:strokeWidth="1dp"
 app:cardElevation="0dp">
 <!-- Panel content here (texts, etc.) -->
 </com.google.android.material.card.MaterialCardView>
 In 
styles.xml , you might define 
<!-
e.g.
 GlassPanelCard as: 
white
 20%
 opacity
 for
 <style name="GlassPanelCard" parent="Widget.Material3.CardView.Filled">
 <item name="shapeAppearance">?attr/shapeAppearanceCornerMedium</item>
 <item name="cardElevation">0dp</item>
 <!-- Background color and stroke can also be set here or directly in 
layout as above -->
 </style>
 3
This gives a card with medium rounded corners, no drop-shadow, a translucent fill, and an optional thin
 border. The semi-transparent fill allows background content to show through faintly. For readability, 
ensure enough opacity – too transparent and text becomes illegible; too opaque and the glass effect is
 lost. A 10–20% opacity white or surface-colored background is common, with a subtle border or inner
 shadow to enhance the "glass" look.
 • 
• 
Applying Blur (Frosted Background): Transparency alone gives a see-through panel, but the
 magic of frosted glass comes from blurring whatever is behind the panel . Android now
 provides high-performance blur APIs to achieve this. There are two scenarios:
 13
 14
 Blur within the same window (in-app views): Android 12 (API 31) introduced RenderEffect
 which can blur the content of a View hierarchy. For example, you can blur an image or an entire
 layout by calling 
view.setRenderEffect(RenderEffect.createBlurEffect(radiusX, 
15
 radiusY, Shader.TileMode.CLAMP)) . This will render the view (and its children) with a
 Gaussian blur of the specified radius. To use it for glassmorphism, one approach is to blur the
 background behind your panel. If your UI layout has, say, an ImageView or a SurfaceView
 drawing the chess board behind the panel, you could apply a RenderEffect blur to that
 background view whenever the panel is shown. This way, the board beneath appears defocused
 through the translucent card. Another approach is to wrap the relevant part of your UI in a
 container and blur that container except the panel. For instance, you could overlay a blur on the
 entire activity background when a dialog/panel opens. A simple code example to blur a View (or
 ViewGroup) in Kotlin: 
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
 val radius = 20f
 viewBehind.setRenderEffect(RenderEffect.createBlurEffect(radius,
 radius, Shader.TileMode.CLAMP))