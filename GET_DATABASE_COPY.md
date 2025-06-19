# 🗃️ How to Get a Safe Database Copy

## Method 1: ADB Pull (Recommended)

### **Step 1: Enable Developer Mode & USB Debugging**
1. Go to Android Settings → About Phone
2. Tap "Build Number" 7 times
3. Go to Settings → Developer Options
4. Enable "USB Debugging"

### **Step 2: Connect Device & Pull Database**
```bash
# Connect your device via USB
adb devices

# Pull the database (adjust package name if needed)
adb shell "run-as com.example.chesspedagogue cp /data/data/com.example.chesspedagogue/databases/chess_games.db /sdcard/chess_games_copy.db"
adb pull /sdcard/chess_games_copy.db ./chess_games_study.db

# Clean up
adb shell "rm /sdcard/chess_games_copy.db"
```

### **Step 3: Open in Database Explorer**
1. Start your Chess Pedagogue Configurator GUI
2. Go to Database Explorer tab
3. Click "📂 Browse DB File"
4. Select `chess_games_study.db`
5. Explore safely without affecting the live app!

## Method 2: App Export Feature (If Available)

### **Add Export Button to Android App**
```java
// In SettingsActivity.java, add this button
Button exportDbButton = new Button(this);
exportDbButton.setText("📤 Export Database Copy");
exportDbButton.setOnAction(e -> exportDatabaseCopy());

private void exportDatabaseCopy() {
    try {
        // Source database
        File dbFile = new File(getDatabasePath("chess_games.db").getAbsolutePath());
        
        // Destination (Downloads folder)
        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File exportFile = new File(downloadsDir, "chess_pedagogue_export_" + 
            System.currentTimeMillis() + ".db");
        
        // Copy database
        Files.copy(dbFile.toPath(), exportFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        Toast.makeText(this, "Database exported to Downloads: " + exportFile.getName(), 
            Toast.LENGTH_LONG).show();
            
    } catch (Exception e) {
        Log.e("Export", "Failed to export database", e);
        Toast.makeText(this, "Export failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
```

## Method 3: Emulator Access (Easiest)

### **If Using Android Emulator:**
```bash
# Emulator databases are easier to access
adb shell
su  # (if rooted emulator)
cp /data/data/com.example.chesspedagogue/databases/chess_games.db /sdcard/
exit
adb pull /sdcard/chess_games.db ./chess_games_study.db
```

## Method 4: App Backup (Android 6+)

### **Using Android Backup**
```bash
# Create app backup
adb backup -f backup.ab com.example.chesspedagogue

# Extract backup (requires Android Backup Extractor tool)
# Download from: https://github.com/nelenkov/android-backup-extractor
java -jar abe.jar unpack backup.ab backup.tar
tar -xvf backup.tar
# Database will be in apps/com.example.chesspedagogue/db/
```

## Database File Locations

### **Typical Android Paths:**
```
/data/data/com.example.chesspedagogue/databases/chess_games.db
/data/data/com.example.chesspedagogue/databases/chess_games.db-journal
/data/data/com.example.chesspedagogue/databases/chess_games.db-wal
```

### **What You'll Find:**
- **saved_games**: Your game history
- **master_positions**: Historical chess positions from masters
- **master_relationships**: AI relationship data
- **emotional_reactions**: Emotional intelligence data
- **topic_memory**: Conversation topics and fatigue
- **user_profiles**: Your user profile data

## Safety Tips

### **✅ Safe Practices:**
1. **Always work with copies** - never modify the original
2. **Export before experiments** - create backups before testing
3. **Use Database Explorer read-only mode** - prevents accidental changes
4. **Test queries on copies first** - validate before applying to live data

### **❌ Never Do This:**
1. **Don't modify the live database** while the app is running
2. **Don't delete original database files** 
3. **Don't run experimental queries** on live data
4. **Don't share databases** with personal data

## Quick Start Commands

### **Get Database Right Now:**
```bash
# Most common scenario - Android device with USB debugging
adb shell "run-as com.example.chesspedagogue cp /data/data/com.example.chesspedagogue/databases/chess_games.db /sdcard/chess_copy.db"
adb pull /sdcard/chess_copy.db ./my_chess_study.db
adb shell "rm /sdcard/chess_copy.db"

# Open in Database Explorer
# 1. Start Chess Pedagogue Configurator
# 2. Database Explorer tab → Browse DB File
# 3. Select my_chess_study.db
# 4. Explore safely!
```

This gives you a **completely safe sandbox** to explore your data without any risk to your live chess app! 🎯