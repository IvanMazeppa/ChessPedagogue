Building a Modern Chess App UI (Android 15,
 Material 3, Classic Views)

 Overview: We aim to create an advanced, aesthetically polished chess app interface using Android 15
 (API 35) features in the XML-based View system. This involves adopting Material Design 3 (Material
 You) for dynamic theming, and implementing a glassmorphic visual style (translucent panels with blur)
 accented by blues/teals. We will leverage the latest API 35 UI tools – such as RenderEffect for blurs and
 even custom shaders – alongside Material 3 components. The design will also enhance gameplay
 feedback with animated UI reactions for events (castling, check, blunders, etc.), e.g. blunder alerts
 with "??" overlays or smooth piece motion. The splash screen and a new "competitive mode" screen
 already use this style; our goal is to extend it across all screens (currently legacy) with consistent
 Material 3 theming, dynamic colors, and polished animations. Below is a comprehensive guide with
 examples and best practices.
 Enabling Material 3 and Dynamic Theming (Material You)
 Material 3 Setup: Start by integrating Material Design 3 components. In your Gradle dependencies, use
 the latest Material Components for Android library (which supports Material3). Next, update your app
 theme to a Material3 parent. For example, in styles.xml: 
<style name="AppTheme" parent="Theme.Material3.DayNight.NoActionBar">
 <!-- Material3 color, shape, and typography assignments will go here -->
 </style>
 This ensures your app uses Material3 styling by default (which includes updated widgets like the new
 Switch, checkboxes, MaterialToolbar, etc.). Material3 is built to support dynamic theming (Material You)
 out-of-the-box on Android 12+, but we need to enable it.
 1
 2
 Dynamic Color (Material You): Material You's dynamic color derives a custom palette from the user's
 wallpaper and applies it to your app's theme . To leverage this, use the DynamicColors API. In
 your Application class, enable dynamic theming at runtime: 
class MyApplication : Application() {
 override fun onCreate() {
 super.onCreate()
 DynamicColors.applyToActivitiesIfAvailable(this) // Enable Material 
You colors
 }
 }
 Calling 
3
 DynamicColors.applyToActivitiesIfAvailable(...) will automatically apply dynamic
 color schemes on Android 12+ devices . Your app theme should be set up to accept the dynamic
 overlay. 
Google 
provides 
a 
ready-made 
theme 
overlay,
 1
4
 ThemeOverlay.Material3.DynamicColors.DayNight , which you can use as your app theme's
 parent . For example: 
<style name="AppTheme"
 parent="ThemeOverlay.Material3.DynamicColors.DayNight">
 <!-- ... NoActionBar, etc. can be combined if needed ... -->
 </style>
 Using this parent (or calling the DynamicColors API as above) ensures that on supported devices, your
 Material3 theme's color roles (primary, secondary, surface, etc.) will be replaced with variants extracted
 from the user's wallpaper. On older Android versions, it will fall back to the static colors you provide (so
 be sure to define a pleasing default color scheme, e.g. your blue/teal brand colors, in your 
values/
 colors.xml ). Material3 defines a comprehensive set of color roles and tokens; for instance, 
colorSurface , 
?attr/
 ?attr/colorPrimary , etc., which you can reference in layouts and styles so that
 they automatically adapt under dynamic theming . 
5
 1
 2
 6
 Dynamic Material You theming: The same app UI can adopt different color schemes based on the
 wallpaper. Android extracts a palette (primary, secondary, tertiary, neutral tones) from the wallpaper
 and applies them to your app's theme . This means your chess app's accent and background
 colors will harmonize with each user's personal device theme, as illustrated above with different tonal
 variations.
 Integrating Material3 Tokens in XML: In Material3, design tokens for color, shape, and typography
 let you maintain consistency. Many Material3 attributes are available for XML use: - Color tokens: Use
 semantic color attributes rather than hardcoded colors. For example, use 
?attr/colorPrimary for
 your primary brand color (dynamic on Android 12+), 
?attr/colorSurface for surfaces, 
?attr/
 colorSurfaceVariant for emphasized panels, etc. Material3's dynamic overlay will automatically
 update these to wallpaper-derived colors at runtime . For instance, you might set your
 chessboard squares or piece highlights to use 
7
 5
 ?attr/colorSecondary or 
?attr/
 colorSecondaryContainer to get a dynamic accent (falling back to teal on older devices). - Shape
 tokens: Material3 promotes standardized corner radius sizes. In XML, you can reference shape
 appearance 
styles 
like 
?attr/shapeAppearanceSmallComponent , 
?attr/
 shapeAppearanceMediumComponent , etc., which correspond to predefined corner radii (Material3's
 default corner sizes are 4dp for extra-small, 8dp small, 12dp medium, 16dp large ). For example,
 MaterialComponents CardView default uses 
8
 10
 9