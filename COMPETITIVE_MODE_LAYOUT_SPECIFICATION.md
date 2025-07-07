# 🏆 COMPETITIVE MODE LAYOUT SPECIFICATION

## 📱 Target Device Specifications
- **Device**: Samsung Galaxy S23 Ultra
- **Resolution**: 1440px horizontal × 3088px vertical  
- **API Level**: Android 15 (API 35)
- **Features**: 120Hz display, AGSL hardware acceleration, edge-to-edge content

## 🎯 CRITICAL LAYOUT REQUIREMENTS

### **📐 Chess Board Positioning**
- **Width**: Nearly edge-to-edge (utilize ~1400px of 1440px screen width)
- **Margin**: Minimal margins (~20px each side) for coordinate labels
- **Position**: Centered horizontally, positioned below header panel
- **Coordinate Labels**: 
  - **Files (a-h)**: Bottom edge of board, centered under each column
  - **Ranks (1-8)**: Left edge of board, centered beside each row
  - **Sync**: CRITICAL - coordinates must align exactly with board squares

### **🎨 Visual Effects Requirements**
- **Glassmorphism**: All panels use translucent glass effects with 15-25% opacity
- **Gradient Background**: Deep blue-purple gradient matching reference screenshots
- **AGSL Shaders**: Hardware-accelerated blur effects on Samsung S23 Ultra
- **Glowing Edges**: Subtle glow effects on all glass panels
- **Animations**: Physics-based piece capture effects with dramatic flying pieces

### **📊 Bottom Section Layout (50/50 Split)**

#### **Left Side (50% width)**
1. **Captured Pieces Panel**
   - Position: Directly below chess board
   - Content: Compact display with multipliers (e.g., "5x pawn")
   - Style: Horizontal layout, glassmorphic background

2. **Control Buttons Panel** 
   - Position: Below captured pieces
   - Content: 7-8 control buttons in grid layout
   - Buttons: PAUSE, SURRENDER, TTS, VOICE, AI, PERSO, VOICE SET, VALIDATE
   - Style: Glass button backgrounds with glow effects

#### **Right Side (50% width)**
1. **Move List Panel**
   - Position: Full height of bottom section
   - Content: Scrollable move history display
   - Size: **MAXIMUM POSSIBLE** - use entire right half space
   - Style: Large glassmorphic panel with proper text contrast

### **🚨 CRITICAL GUARDRAILS COMPLIANCE**

#### **Features That Must NEVER Be Removed:**
- ✅ **Physics-based piece capture animations** (EnhancedCaptureEffects.java)
- ✅ **AGSL hardware-accelerated shaders** (AGSLManager.java, WorkingGlassEffects.java)
- ✅ **Glassmorphism panel effects** with proper blur and transparency
- ✅ **Deep blue-purple gradient backgrounds** matching reference images
- ✅ **All 7-8 control buttons** as specified by user requirements
- ✅ **Voice functionality** and master AI personality systems
- ✅ **Edge-to-edge layout** with proper inset handling

#### **Layout Rules:**
- **Board Dimensions**: User specified "just under edge-to-edge" for 1440px screen
- **Coordinate Sync**: Numbers and letters must align perfectly with board squares
- **Move List Size**: User specified "as large as possible" - use full right half
- **Bottom Space**: Optimize for captured pieces + buttons while maintaining glassmorphism

## 🔧 IMPLEMENTATION SPECIFICATIONS

### **Chess Board Container**
```xml
<!-- Target dimensions for 1440px screen -->
<ConstraintLayout 
    android:layout_width="0dp"              <!-- Full width minus minimal margins -->
    android:layout_marginStart="20dp"       <!-- Space for rank labels -->
    android:layout_marginEnd="20dp"         <!-- Symmetrical margins -->
    android:layout_height="wrap_content">
    
    <!-- Chess board: ~1400px width for near-edge-to-edge -->
    <ChessBoardView 
        android:layout_width="0dp"
        android:layout_height="0dp"
        app:layout_constraintDimensionRatio="1:1" />
        
    <!-- Coordinate labels properly positioned -->
</ConstraintLayout>
```

### **Bottom Section (Perfect 50/50)**
```xml
<LinearLayout 
    android:orientation="horizontal"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <!-- LEFT: Captured pieces + buttons (50%) -->
    <LinearLayout 
        android:layout_width="0dp"
        android:layout_weight="1"
        android:orientation="vertical">
        <!-- Captured pieces panel -->
        <!-- Control buttons grid -->
    </LinearLayout>
    
    <!-- RIGHT: Move list (50%) -->
    <CardView 
        android:layout_width="0dp"
        android:layout_weight="1"
        android:minHeight="300dp">           <!-- Maximum height available -->
        <!-- Move history content -->
    </CardView>
</LinearLayout>
```

### **Coordinate Label Positioning**
```xml
<!-- Rank labels (1-8) - LEFT side -->
<LinearLayout 
    android:orientation="vertical"
    android:layout_width="20dp"
    android:layout_height="0dp"
    app:layout_constraintHeight_default="spread">
    <!-- 8 TextViews, each height="0dp" weight="1" -->
</LinearLayout>

<!-- File labels (a-h) - BOTTOM -->
<LinearLayout 
    android:orientation="horizontal" 
    android:layout_width="0dp"
    android:layout_height="20dp">
    <!-- 8 TextViews, each width="0dp" weight="1" -->
</LinearLayout>
```

## ✅ SUCCESS CRITERIA

### **Visual Quality Standards**
- [ ] Chess board nearly edge-to-edge (~1400px width on 1440px screen)
- [ ] Coordinate labels perfectly aligned with board squares
- [ ] Move list panel uses maximum available space (full right half)
- [ ] Captured pieces visible between board and buttons
- [ ] All glassmorphism effects working with proper transparency
- [ ] Deep blue-purple gradient background active
- [ ] AGSL shaders rendering correctly on Samsung S23 Ultra

### **Functional Requirements**
- [ ] All 7-8 control buttons working and accessible  
- [ ] Physics-based piece capture animations active
- [ ] Voice functionality operational
- [ ] Master AI personality system responding
- [ ] Edge-to-edge layout with proper system bar handling
- [ ] 120Hz smooth performance maintained

### **Layout Verification**
- [ ] Perfect 50/50 split in bottom section
- [ ] No wasted space in layout
- [ ] Proper spacing between elements
- [ ] Glass effects applied to all panels
- [ ] Coordinate numbers (1-8) align with board rows
- [ ] Coordinate letters (a-h) align with board columns

## 🚨 CHANGE LOG
- **2025-07-03**: Initial specification created based on user feedback
- **Issue**: Board not edge-to-edge, coordinates misaligned, move list too small
- **Requirements**: Respect guardrails, maintain all graphics features, optimize layout

---

**CRITICAL**: This specification must be followed exactly. Any changes require explicit user approval to prevent feature regression and maintain tournament-grade visual quality.