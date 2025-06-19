# 🎮 Chess Pedagogue GUI Setup Guide

## 📁 Current Structure (Perfect!)

Your current structure is ideal:
```
ChessPedagogue/                          ← Your Android project root
├── app/                                 ← Android app
├── build.gradle                         ← Android build files
├── ChessPedagogueConfigurator/          ← New GUI tool (already there!)
│   ├── pom.xml                         ← Maven config
│   ├── src/main/java/...               ← GUI source code
│   └── README.md                       ← Instructions
└── [all your other Android files]
```

This is **exactly what we want** - two parallel projects sharing the same root!

## 🚀 Step-by-Step Setup Guide

### Step 1: Check Java Installation
```bash
# Open command prompt and check Java version
java -version
```
**You need Java 11 or higher.** If you don't have it:
- Download from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
- Install it (just like any normal program)

### Step 2: Check Maven Installation
```bash
# Check if Maven is installed
mvn -version
```

**If Maven isn't installed:**
- Download from [Maven website](https://maven.apache.org/download.cgi)
- Extract to a folder like `C:\maven`
- Add `C:\maven\bin` to your Windows PATH
- **Or use your IDE** (IntelliJ/Eclipse can handle Maven automatically)

### Step 3: Choose Your Approach

**Option A: Using Command Line (Simplest)**
```bash
# Navigate to the configurator folder
cd ChessPedagogue/ChessPedagogueConfigurator

# Run the application
mvn clean javafx:run
```

**Option B: Using IntelliJ IDEA (Recommended for beginners)**
1. Open IntelliJ IDEA
2. **File** → **Open** 
3. Select the `ChessPedagogueConfigurator` folder
4. IntelliJ will detect it's a Maven project and set everything up
5. Click the **Run** button

**Option C: Using Eclipse**
1. Open Eclipse
2. **File** → **Import** → **Existing Maven Projects**
3. Browse to `ChessPedagogueConfigurator` folder
4. Eclipse will import and configure everything

## 🔧 What If You Don't Have Maven?

No problem! Here are alternatives:

### Simple JAR Approach
I can create a version that doesn't need Maven - just a simple JAR you double-click to run.

### IDE Integration
Most modern IDEs (IntelliJ, Eclipse, VS Code) can handle JavaFX projects without external Maven installation.

## 📱 Integration with Your Android Project

The beauty of this setup is that both projects can **share configuration files**:

```
ChessPedagogue/
├── shared_config/                       ← New folder for shared settings
│   └── chess_config.json               ← Both projects read/write this
├── app/                                 ← Your Android app reads config
└── ChessPedagogueConfigurator/         ← GUI writes config
```

## 🎯 What Should We Do First?

I recommend this order:

1. **Check if you have Java 11+** (most important)
2. **Try opening in IntelliJ/Eclipse** (easiest if you have an IDE)
3. **If that works, we'll add the missing utility classes** 
4. **Then connect it to your Android app**

## ❓ Quick Questions to Help You:

1. **Do you have IntelliJ IDEA, Eclipse, or VS Code installed?**
2. **Are you comfortable with command line, or prefer GUI tools?**
3. **What's your Java situation?** (Check with `java -version`)

Based on your answers, I can give you the most appropriate path forward. The project structure is already perfect - we just need to get the development environment set up for you!

## 🚀 After Installing IntelliJ

### Step 1: Open the Project
1. Launch IntelliJ IDEA
2. Click **"Open"** (not "New Project")
3. Navigate to your `ChessPedagogue/ChessPedagogueConfigurator` folder
4. Select the folder and click **"OK"**

### Step 2: Let IntelliJ Set Up Maven
- IntelliJ will automatically detect the `pom.xml` file
- It will ask to **"Import Maven project"** - click **"Yes"**
- Let it download dependencies (this may take a few minutes)
- You'll see a progress bar in the bottom right

### Step 3: Configure JavaFX
If IntelliJ gives you JavaFX errors:
1. Go to **File** → **Project Structure**
2. Under **SDKs**, make sure you have Java 11 or higher
3. Under **Modules**, make sure the project is using the right Java version

### Step 4: Run the Application
1. Find `ChessPedagogueConfigurator.java` in the project explorer
2. Right-click on it
3. Select **"Run ChessPedagogueConfigurator.main()"**
4. The GUI should launch!

## 🔧 If You Encounter Issues

### "JavaFX runtime components are missing"
Add these VM options in your run configuration:
```
--module-path /path/to/javafx/lib --add-modules javafx.controls,javafx.fxml
```

### Maven Dependencies Not Downloading
1. Go to **View** → **Tool Windows** → **Maven**
2. Click the **"Reload Maven Projects"** button (circular arrow icon)
3. Let it re-download everything

### Java Version Issues
1. **File** → **Project Structure** → **Project**
2. Set **Project SDK** to Java 11 or higher
3. Set **Language Level** to match your Java version

## 📋 Missing Files to Create

After IntelliJ is working, we need to create these missing utility classes:

### 1. SliderWithLabel.java
```java
// Custom UI component for labeled sliders
// Location: src/main/java/com/chesspedagogue/configurator/utils/
```

### 2. Configuration Models
```java
// ConversationTemplate.java - data structure for templates
// ConfigurationManager.java - handles file I/O
// Location: src/main/java/com/chesspedagogue/configurator/models/
```

### 3. Other Tab Classes
```java
// MasterPersonalityTab.java
// GameFlowTab.java
// LiveMonitorTab.java
// AdvancedSettingsTab.java
// Location: src/main/java/com/chesspedagogue/configurator/tabs/
```

## 🎯 Immediate Next Steps After Reboot

1. **Install IntelliJ IDEA Community Edition** (free)
2. **Check Java version** with `java -version` in command prompt
3. **Open this guide** and follow the IntelliJ setup steps
4. **Let me know how it goes** - I'll create the missing utility classes next

## 💡 Alternative: Simple Approach

If IntelliJ/Maven feels overwhelming, I can create a **simpler version** that:
- Uses plain Java (no Maven)
- Just requires Java JDK 11+
- Can be run with a simple double-click
- Still provides the same functionality

Just let me know your preference after trying IntelliJ!

---

**Remember:** The hardest part is the initial setup. Once IntelliJ is running the project, adding new features becomes very easy with the visual interface!