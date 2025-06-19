# Tournament Player Name Fix - SpectatorGameActivity Integration

## Issue Identified ❌
SpectatorGameActivity was always showing "fischer vs tal" regardless of which tournament game was selected because:

1. **Name Format Mismatch**: Tournament passes full display names like "Bobby Fischer", "Viswanathan Anand"
2. **SpectatorGameActivity expects internal names**: like "fischer", "anand" 
3. **Wrong Processing Order**: Tournament mode check happened AFTER fallback logic kicked in

## Root Cause Analysis
```
TournamentGameViewer: "Bobby Fischer vs Viswanathan Anand" 
                     ↓ (Intent extras)
SpectatorGameActivity: getPlayersFromIntent() 
                     ↓ (processes fallbacks first)
                   Result: "tal vs fischer" (fallbacks applied)
                     ↓ (tournament check too late)
                   checkTournamentModeExtras() (overrides ignored)
```

## Fixes Applied ✅

### 1. **Consolidated Tournament Processing**
- Moved tournament mode detection to `getPlayersFromIntent()` 
- Tournament processing now happens BEFORE fallback logic
- Proper order ensures tournament players are used

### 2. **Added Name Conversion Function**
```java
private String convertDisplayNameToInternalName(String displayName) {
    switch (displayName.trim()) {
        case "Bobby Fischer": return "fischer";
        case "Viswanathan Anand": return "anand";
        case "Mikhail Tal": return "tal";
        // ... all 16 masters mapped
    }
}
```

### 3. **Fixed Processing Flow**
```java
private void getPlayersFromIntent() {
    // 1. Check tournament mode FIRST
    if (isTournamentMode) {
        String master1 = intent.getStringExtra("MASTER_1");
        String master2 = intent.getStringExtra("MASTER_2");
        
        // 2. Convert display names to internal names
        whitePlayer = convertDisplayNameToInternalName(master1);
        blackPlayer = convertDisplayNameToInternalName(master2);
        
        // 3. Return early (skip fallbacks)
        return;
    }
    
    // 4. Regular fallback logic for non-tournament mode
    // ...
}
```

## Expected Results ✅

1. **Correct Tournament Games**: SpectatorGameActivity will now show the actual selected tournament game
2. **Proper Name Conversion**: "Bobby Fischer vs Viswanathan Anand" → "fischer vs anand"
3. **Tournament Context Preserved**: Round, quality, and game type still displayed
4. **Fallback Safety**: Regular spectator mode still works with fallbacks

## Test Cases Covered
- ✅ Tournament mode with full display names
- ✅ Tournament mode with internal names (fallback)  
- ✅ Regular spectator mode (non-tournament)
- ✅ Unknown master names (graceful degradation)

The tournament viewer should now properly launch the selected games instead of always defaulting to "fischer vs tal"! 🏆