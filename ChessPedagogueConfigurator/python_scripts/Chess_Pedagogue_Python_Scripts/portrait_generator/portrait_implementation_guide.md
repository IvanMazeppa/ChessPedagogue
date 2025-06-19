
# Chess Master Portraits Implementation Guide

## Files Generated:
- `portrait_card_[master].png` - For selection cards (rectangular, up to 150x150px)
- `portrait_speaking_[master].png` - For speaking indicator (circular, up to 120x120px)

## Android Layout Usage:

### In your card layouts, replace the existing ImageView:
```xml
<ImageView
    android:layout_width="80dp"
    android:layout_height="80dp"
    android:src="@drawable/portrait_card_alekhine"
    android:contentDescription="Alexander Alekhine portrait"
    android:scaleType="centerCrop" />
```

### For speaking indicator:
```xml
<ImageView
    android:id="@+id/coachPortrait"
    android:layout_width="60dp"
    android:layout_height="60dp"
    android:src="@drawable/portrait_speaking_alekhine"
    android:contentDescription="Coach Alekhine"
    android:visibility="gone" />
```

### In your Activity code:
```java
// Show speaking portrait
ImageView coachPortrait = findViewById(R.id.coachPortrait);
String masterName = getSelectedMaster(); // e.g., "alekhine"
String drawableName = "portrait_speaking_" + masterName;
int resourceId = getResources().getIdentifier(drawableName, "drawable", getPackageName());
coachPortrait.setImageResource(resourceId);
coachPortrait.setVisibility(View.VISIBLE);
```

## Master Keys:
alekhine, anand, botvinnik, capablanca, carlsen, fischer, 
karpov, kasparov, kramnik, lasker, morphy, tal
        