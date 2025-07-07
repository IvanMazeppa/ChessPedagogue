# Reasoning Engine Architecture Plan v0.9.10

## 🎯 **Project Overview**

**Goal**: Build a clean, unified reasoning engine system to replace the problematic hybrid PersonalityEngine integration. This new system will handle o4-mini reasoning models with master-specific configurations while maintaining clean, maintainable code.

**Branch**: `reasoning-engine-v0.9.10`

## 🏗️ **Core Architecture Design**

### **Unified ReasoningAgent + Configuration System**

Instead of individual classes per master (OptimizedAlekhineAgent, OptimizedTalAgent, etc.), we use:

1. **Single ReasoningAgent class** - Handles all Responses API communication
2. **Master Configuration System** - Stores personality, instructions, vector stores per master
3. **Adaptive Strength Engine** - Generates ELO-appropriate candidate moves
4. **Central Manager** - Coordinates everything

## 📋 **Implementation Plan**

### **Phase 1: Core Components**

#### **1. UnifiedReasoningAgent.java**
```java
public class UnifiedReasoningAgent {
    private MasterConfiguration currentConfig;
    private final String apiKey;
    
    public void setMaster(String masterName) {
        this.currentConfig = MasterConfigurationManager.getConfig(masterName);
    }
    
    public ReasoningResponse selectMove(String fen, List<CandidateMove> candidates, String gameContext) {
        // Use currentConfig for master-specific behavior
        return makeResponsesAPICall(fen, candidates, currentConfig, gameContext);
    }
    
    private ReasoningResponse makeResponsesAPICall(String fen, List<CandidateMove> candidates, 
                                                 MasterConfiguration config, String context) {
        // Build request using config.systemInstructions, config.vectorStoreId, etc.
        // Make HTTP call to Responses API
        // Parse response and return ReasoningResponse
    }
}
```

#### **2. MasterConfiguration.java**
```java
public class MasterConfiguration {
    private final String masterName;
    private final String reasoningModel;        // "o4-mini", future models
    private final String systemInstructions;   // Master-specific personality
    private final String vectorStoreId;        // Master's game database
    private final List<String> availableFunctions; // Master-specific tools
    private final PersonalityTraits traits;    // Aggression, style preferences
    private final VoiceSettings voiceConfig;   // TTS configuration
    private final int maxOutputTokens;         // Model limits
    private final double temperature;          // Response randomness
    private final String reasoningEffort;      // "low", "medium", "high"
    
    // Builder pattern for clean construction
    public static class Builder {
        // Implementation
    }
}
```

#### **3. MasterConfigurationManager.java**
```java
public class MasterConfigurationManager {
    private static final Map<String, MasterConfiguration> configs = new HashMap<>();
    
    public static void initialize(Context context) {
        // Load all master configurations from assets or hardcoded
        loadAlekhineConfig();
        loadTalConfig();
        loadFischerConfig();
        // ... etc
    }
    
    public static MasterConfiguration getConfig(String masterName) {
        return configs.get(masterName.toLowerCase());
    }
    
    public static boolean supportsReasoning(String masterName) {
        return configs.containsKey(masterName.toLowerCase());
    }
    
    private static void loadAlekhineConfig() {
        MasterConfiguration alekhine = new MasterConfiguration.Builder()
            .masterName("alekhine")
            .reasoningModel("o4-mini")
            .systemInstructions(getAlekhineInstructions())
            .vectorStoreId("vs_685f5e39515481919229a059cd8a2d96")
            .personalityTraits(new PersonalityTraits()
                .aggression(0.8f)
                .calculationDepth(0.9f)
                .riskTolerance(0.7f))
            .voiceConfig(VoiceSettings.russianAccent())
            .maxOutputTokens(25000)
            .temperature(1.0)
            .reasoningEffort("low")
            .build();
        configs.put("alekhine", alekhine);
    }
    
    // Similar methods for other masters...
}
```

#### **4. ReasoningAdaptiveEngine.java**
```java
public class ReasoningAdaptiveEngine {
    private final StockfishManager stockfishManager;
    
    public List<CandidateMove> generateCandidates(String fen, int targetElo, int maxCandidates) {
        // Configure Stockfish for target ELO strength
        configureStockfishForElo(targetElo);
        
        // Get multi-PV analysis
        String analysis = stockfishManager.analyzePosition(fen, maxCandidates);
        
        // Parse into CandidateMove objects with evaluations
        return parseStockfishCandidates(analysis);
    }
    
    private void configureStockfishForElo(int targetElo) {
        // Set UCI_LimitStrength, UCI_Elo, skill level, think time
        // Based on ChessMasterRatings.getStockfishConfigForElo()
    }
    
    public static class CandidateMove {
        public final String move;           // UCI notation
        public final float evaluation;     // Centipawns
        public final int depth;           // Analysis depth
        public final String variation;    // Principal variation
        
        // Constructor, getters, etc.
    }
}
```

#### **5. ReasoningEngineManager.java** 
```java
public class ReasoningEngineManager {
    private final UnifiedReasoningAgent reasoningAgent;
    private final ReasoningAdaptiveEngine adaptiveEngine;
    private final ExecutorService executorService;
    
    public interface ReasoningCallback {
        void onMoveSelected(String move, String explanation, float confidence);
        void onError(String error);
    }
    
    public void calculateMove(String fen, String masterName, int targetElo, 
                            String gameContext, ReasoningCallback callback) {
        
        executorService.execute(() -> {
            try {
                // 1. Generate strength-appropriate candidates
                List<CandidateMove> candidates = adaptiveEngine.generateCandidates(fen, targetElo, 8);
                
                // 2. Configure reasoning agent for master
                reasoningAgent.setMaster(masterName);
                
                // 3. Let reasoning model pick best move
                ReasoningResponse response = reasoningAgent.selectMove(fen, candidates, gameContext);
                
                // 4. Return via callback
                callback.onMoveSelected(response.move, response.explanation, response.confidence);
                
            } catch (Exception e) {
                callback.onError("Reasoning engine failed: " + e.getMessage());
            }
        });
    }
    
    public static boolean supportsReasoning(String masterName) {
        return MasterConfigurationManager.supportsReasoning(masterName);
    }
}
```

### **Phase 2: Game Integration**

#### **Game Mode Integration Points**

**MainActivity/GameViewModel:**
```java
// In GameViewModel.requestPersonalityEngineMove()
if (ReasoningEngineManager.supportsReasoning(currentMaster)) {
    reasoningEngineManager.calculateMove(currentFen, currentMaster, targetElo, 
                                       getPgnContext(), new ReasoningCallback() {
        @Override
        public void onMoveSelected(String move, String explanation, float confidence) {
            // Handle successful move selection
            handleReasoningMove(move, explanation, confidence);
        }
        
        @Override  
        public void onError(String error) {
            // Fallback to traditional PersonalityEngine
            fallbackToPersonalityEngine();
        }
    });
} else {
    // Use traditional PersonalityEngine for non-reasoning masters
    gameRepository.calculatePersonalityMove(currentFen, callback);
}
```

**CompetitiveModeActivity:**
```java
// Similar integration - reasoning engine for supported masters
if (ReasoningEngineManager.supportsReasoning(selectedMaster)) {
    useReasoningEngine = true;
    Log.d(TAG, "🧠 Using reasoning engine for " + selectedMaster);
} else {
    usePersonalityEngine = true;
    Log.d(TAG, "🎭 Using traditional personality engine for " + selectedMaster);
}
```

### **Phase 3: Master Configurations**

#### **Alekhine Configuration**
```java
private static String getAlekhineInstructions() {
    return "You are Alexander Alekhine, World Chess Champion (1927-35, 1937-46). Key traits:\n" +
           "- Aggressive attacking play with sound calculation\n" +
           "- Complex tactical combinations based on solid positional foundations\n" +
           "- Dynamic piece sacrifices when compensation is clear\n" +
           "- Excellent opening preparation and deep endgame technique\n" +
           "- Preference for winning chances over sterile equality\n\n" +
           "CRITICAL REQUIREMENTS:\n" +
           "1. Choose moves that Alekhine would actually play - avoid obvious blunders\n" +
           "2. Consider both tactical opportunities AND positional soundness\n" +
           "3. Respond with moves in UCI format only (e.g., e2e4, g1f3)\n" +
           "4. Balance aggression with practical chess strength\n\n" +
           "Focus on authentic Alekhine-style moves that create winning chances without losing material carelessly.";
}
```

#### **Additional Master Configurations**
- **Tal**: Ultra-aggressive, sacrificial play, high risk tolerance
- **Fischer**: Precise calculation, endgame mastery, positional understanding  
- **Carlsen**: Modern practical approach, endgame technique, intuitive play
- **Kasparov**: Dynamic play, theoretical preparation, fighting spirit

## 🔧 **Advanced Features (Future)**

### **Engine-in-the-Loop Optimization**
- Pre-filter obviously bad moves before reasoning API call
- Dynamic candidate count based on position complexity
- Evaluation thresholds for quality control

### **Multi-Model Support**
- Easy to add new reasoning models (o1, o3, future models)
- Model selection based on position type or master preference
- Performance comparison and automatic model selection

### **Dynamic Difficulty**  
- Real-time ELO adjustment based on player performance
- Position-specific strength adjustment
- Learning player patterns for optimal challenge level

## 📁 **File Structure**

```
app/src/main/java/com/example/chesspedagogue/
├── reasoning/
│   ├── UnifiedReasoningAgent.java
│   ├── ReasoningEngineManager.java
│   ├── ReasoningAdaptiveEngine.java
│   ├── MasterConfiguration.java
│   ├── MasterConfigurationManager.java
│   ├── PersonalityTraits.java
│   ├── ReasoningResponse.java
│   └── CandidateMove.java
└── [existing files...]
```

## 🎯 **Implementation Priority**

### **High Priority (Core System)**
1. ✅ Create MasterConfiguration and MasterConfigurationManager
2. ✅ Build UnifiedReasoningAgent with Responses API integration
3. ✅ Implement ReasoningAdaptiveEngine for candidate generation
4. ✅ Create ReasoningEngineManager as central coordinator

### **Medium Priority (Integration)**  
5. ✅ Integrate with Main Game (primary testing)
6. ✅ Add Competitive Mode support
7. ✅ Configure Alekhine as first reasoning master

### **Low Priority (Cleanup)**
8. ✅ Disable Alekhine in PersonalityEngine (feature flag)
9. ✅ Add Spectator Mode reasoning support
10. ✅ Performance optimization and monitoring

## 🧪 **Testing Strategy**

### **Unit Tests**
- MasterConfigurationManager loading
- ReasoningAdaptiveEngine candidate generation
- UnifiedReasoningAgent API integration

### **Integration Tests**
- Full reasoning pipeline (candidates → selection → game)
- Fallback behavior when reasoning fails
- Performance under different ELO settings

### **Game Tests**
- Complex middlegame positions (where reasoning matters)
- Opening consistency with master style
- Adaptive strength accuracy

## 🚀 **Success Metrics**

- [ ] ✅ Reasoning model moves played correctly in complex positions
- [ ] ✅ Consistent performance across all game modes
- [ ] ✅ Adaptive strength working with reasoning intelligence  
- [ ] ✅ No degradation in existing functionality
- [ ] ✅ Easy addition of new reasoning masters
- [ ] ✅ Performance improvements over current system

## 📊 **Key Benefits of New Architecture**

1. **🚫 No more mysterious overrides** - Clean control flow from candidates → reasoning → game
2. **✅ Single codebase** - One ReasoningAgent handles all masters via configuration
3. **✅ Master-specific behavior** - Unique instructions, vector stores, personality traits per master
4. **✅ Easy to extend** - Add new masters by creating configurations, not new classes
5. **✅ Maintainable** - Central configuration management, clear separation of concerns
6. **✅ Testable** - Isolated components, swap configurations for testing
7. **✅ Performance optimized** - Built for reasoning models from ground up
8. **✅ Future-ready** - Easy integration of new models, advanced techniques

---

**Ready for Implementation**: All components designed, file structure planned, integration points identified.  
**Next Step**: Start with MasterConfigurationManager and MasterConfiguration classes.