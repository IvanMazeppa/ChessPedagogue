# 🚀 Quick Start Guide for IntelliJ IDEA 2025.1.2

## 📱 **Step 1: Open IntelliJ IDEA**
1. Double-click the IntelliJ IDEA icon on your desktop
2. Wait for it to fully load

## 📂 **Step 2: Open Your Project**

### **If you see the Welcome Screen:**
1. Click **"Open"** button
2. Navigate to: `D:\Users\dilli\AndroidStudioProjects\ChessPedagogue\ChessPedagogueConfigurator`
3. **Select the ChessPedagogueConfigurator folder** (not a file inside it)
4. Click **"OK"**

### **If another project is already open:**
1. Go to **File** → **Open** in the top menu
2. Navigate to: `D:\Users\dilli\AndroidStudioProjects\ChessPedagogue\ChessPedagogueConfigurator`
3. **Select the ChessPedagogueConfigurator folder**
4. Click **"OK"**

## ⚙️ **Step 3: Let IntelliJ Set Up Maven**

IntelliJ will automatically detect your `pom.xml` file and:

1. **Show a notification**: "Maven projects need to be imported"
   - Click **"Import Maven Projects"** or **"Enable Auto-Import"**

2. **Download dependencies**: Watch the progress bar in bottom-right corner
   - This will take 2-3 minutes
   - IntelliJ is downloading JavaFX and other libraries

3. **Index files**: Another progress bar will appear
   - This is normal and expected

## 🚀 **Step 4: Run Your GUI**

Once the setup is complete:

1. **Find the main file**: Look for `ChessPedagogueConfiguratorSimple.java` in the left panel:
   ```
   src/main/java/com/chesspedagogue/configurator/ChessPedagogueConfiguratorSimple.java
   ```

2. **Run it**: Right-click on the file and choose:
   - **"Run 'ChessPedagogueConfiguratorSimple.main()'"**

3. **Your GUI should open!** 🎉

## 🔧 **If You Get JavaFX Errors**

If you see "JavaFX runtime components are missing":

1. Go to **Run** → **Edit Configurations**
2. Find your run configuration
3. In **"VM options"** field, add:
   ```
   --module-path "target/lib" --add-modules javafx.controls,javafx.fxml
   ```
4. Click **OK** and run again

## ✅ **Success! What You'll See**

Your Chess Pedagogue Configurator will open with these tabs:
- 🎭 **Conversation Templates** (fully functional!)
- 👥 **Master Personality** 
- ⚙️ **Game Flow**
- 📊 **Live Monitor**
- 🔧 **Advanced Settings**

You can immediately start creating and testing conversation templates for your chess AI masters!

## 🆘 **If Something Goes Wrong**

**Common Issues:**
- **"Cannot resolve symbol"**: Maven import is still in progress, wait a bit longer
- **"Class not found"**: Make sure you selected the ChessPedagogueConfigurator folder, not the parent ChessPedagogue folder
- **"No JavaFX"**: Use the VM options from the "JavaFX Errors" section above

**Need Help?** Just tell me:
- What step you're on
- What you see on your screen
- Any error messages (copy/paste them)

## 🎯 **Next Steps**

Once this is working:
1. Create your first conversation template
2. Test different settings with the sliders
3. Export the configuration for your Android app
4. Get back to developing those amazing AI personalities! 🎭