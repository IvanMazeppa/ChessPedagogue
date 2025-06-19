package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * COMPREHENSIVE PersonalityEngine System Diagnostic
 * 
 * This diagnostic tool traces the complete flow:
 * Position → Database Query → Historical Matches → AI Analysis → Style Application → Final Move
 * 
 * Use this to identify exactly where the PersonalityEngine logic is breaking down.
 */
public class PersonalityEngineSystemDiagnostic {
    private static final String TAG = "PersonalitySystemDiag";
    
    public static class DiagnosticResult {
        public String testPosition;
        public String masterName;
        public int targetElo;
        
        // Database Analysis
        public int totalPositionsInDB;
        public int exactMatches;
        public int similarMatches;
        public int intelligentMatches;
        public List<String> sampleMatchedGames;
        
        // AI Integration
        public boolean assistantAvailable;
        public String aiAnalysisResult;
        public boolean vectorStoreConnected;
        
        // Style Application
        public List<String> stockfishCandidates;
        public String selectedMove;
        public String styleReasoning;
        public float personalityWeight;
        
        // Quality Assessment
        public boolean logicallyConsistent;
        public String qualityIssues;
        public float authenticityScore;
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("=== PersonalityEngine System Diagnostic ===\n");
            sb.append(String.format("Position: %s\n", testPosition));
            sb.append(String.format("Master: %s (Target ELO: %d)\n", masterName, targetElo));
            sb.append("\n--- Database Analysis ---\n");
            sb.append(String.format("Total positions in DB: %d\n", totalPositionsInDB));
            sb.append(String.format("Exact matches: %d\n", exactMatches));
            sb.append(String.format("Similar matches: %d\n", similarMatches));
            sb.append(String.format("Intelligent matches: %d\n", intelligentMatches));
            sb.append("\n--- AI Integration ---\n");
            sb.append(String.format("Assistant available: %s\n", assistantAvailable));
            sb.append(String.format("Vector store connected: %s\n", vectorStoreConnected));
            sb.append("\n--- Style Application ---\n");
            sb.append(String.format("Selected move: %s\n", selectedMove));
            sb.append(String.format("Personality weight: %.2f\n", personalityWeight));
            sb.append("\n--- Quality Assessment ---\n");
            sb.append(String.format("Logically consistent: %s\n", logicallyConsistent));
            sb.append(String.format("Authenticity score: %.2f\n", authenticityScore));
            if (qualityIssues != null && !qualityIssues.isEmpty()) {
                sb.append(String.format("Issues found: %s\n", qualityIssues));
            }
            return sb.toString();
        }
    }
    
    /**
     * Run comprehensive diagnostic on PersonalityEngine system
     */
    public static DiagnosticResult runFullSystemDiagnostic(Context context, String testFen, String masterName, int targetElo) {
        Log.d(TAG, "🔬 STARTING COMPREHENSIVE PERSONALITY ENGINE DIAGNOSTIC");
        Log.d(TAG, "📋 Test Position: " + testFen);
        Log.d(TAG, "🎭 Master: " + masterName + " (Target ELO: " + targetElo + ")");
        
        DiagnosticResult result = new DiagnosticResult();
        result.testPosition = testFen;
        result.masterName = masterName;
        result.targetElo = targetElo;
        
        try {
            // Phase 1: Database Diagnostic
            Log.d(TAG, "🔍 Phase 1: Database Analysis");
            runDatabaseDiagnostic(context, testFen, masterName, result);
            
            // Phase 2: AI Integration Diagnostic  
            Log.d(TAG, "🤖 Phase 2: AI Integration Analysis");
            runAIIntegrationDiagnostic(context, masterName, result);
            
            // Phase 3: Style Application Diagnostic
            Log.d(TAG, "🎨 Phase 3: Style Application Analysis");
            runStyleApplicationDiagnostic(context, testFen, masterName, targetElo, result);
            
            // Phase 4: Quality Assessment
            Log.d(TAG, "📊 Phase 4: Quality Assessment");
            runQualityAssessment(result);
            
            Log.d(TAG, "✅ DIAGNOSTIC COMPLETE");
            Log.d(TAG, result.toString());
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error during diagnostic", e);
            result.qualityIssues = "Diagnostic failed: " + e.getMessage();
        }
        
        return result;
    }
    
    private static void runDatabaseDiagnostic(Context context, String testFen, String masterName, DiagnosticResult result) {
        try {
            GameDatabaseHelper dbHelper = new GameDatabaseHelper(context);
            
            // Check total positions for this master
            result.totalPositionsInDB = dbHelper.getMasterPositionCount(masterName);
            Log.d(TAG, "📊 Total " + masterName + " positions in DB: " + result.totalPositionsInDB);
            
            // Test exact FEN matching
            List<GameDatabaseHelper.HistoricalPosition> exactMatches = 
                dbHelper.findSimilarPositions(testFen, masterName, 50);
            result.exactMatches = exactMatches.size();
            Log.d(TAG, "🎯 Exact FEN matches: " + result.exactMatches);
            
            // Test board similarity matching
            String boardPart = testFen.split(" ")[0];
            // Note: This would require modifying GameDatabaseHelper to expose this test
            Log.d(TAG, "🔍 Board pattern: " + boardPart);
            
            // Analyze quality of matches
            if (!exactMatches.isEmpty()) {
                result.sampleMatchedGames = extractGameInfo(exactMatches.subList(0, Math.min(3, exactMatches.size())));
                Log.d(TAG, "📋 Sample matched games: " + result.sampleMatchedGames);
                
                // Check if matches make logical sense
                analyzeMatchQuality(testFen, exactMatches, result);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Database diagnostic failed", e);
            result.qualityIssues = "Database error: " + e.getMessage();
        }
    }
    
    private static void runAIIntegrationDiagnostic(Context context, String masterName, DiagnosticResult result) {
        try {
            // Test OpenAI Assistant availability
            FineTunedModelManager modelManager = FineTunedModelManager.getInstance(context);
            result.assistantAvailable = modelManager.hasAssistantForMaster(masterName);
            Log.d(TAG, "🤖 Assistant available for " + masterName + ": " + result.assistantAvailable);
            
            // Test Vector Store connection (if applicable)
            result.vectorStoreConnected = checkVectorStoreConnection(masterName);
            Log.d(TAG, "📚 Vector store connected: " + result.vectorStoreConnected);
            
            // Test a sample AI analysis
            if (result.assistantAvailable) {
                // This would require integration with your AI analysis system
                result.aiAnalysisResult = "AI integration test pending";
                Log.d(TAG, "🧠 AI analysis capability: Available");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ AI integration diagnostic failed", e);
            result.qualityIssues = "AI integration error: " + e.getMessage();
        }
    }
    
    private static void runStyleApplicationDiagnostic(Context context, String testFen, String masterName, int targetElo, DiagnosticResult result) {
        try {
            // Test Stockfish integration
            StockfishManager stockfish = new StockfishManager();
            if (stockfish != null) {
                // Get Stockfish candidates
                result.stockfishCandidates = getStockfishMoves(stockfish, testFen, targetElo);
                Log.d(TAG, "♟️ Stockfish candidates: " + result.stockfishCandidates);
                
                // Test PersonalityEngine integration
                PersonalityEngine personalityEngine = PersonalityEngine.getInstance(context, stockfish);
                if (personalityEngine != null) {
                    result.personalityWeight = getPersonalityWeight(personalityEngine);
                    Log.d(TAG, "🎭 Personality weight: " + result.personalityWeight);
                    
                    // Test actual move selection
                    testMoveSelection(personalityEngine, testFen, result);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Style application diagnostic failed", e);
            result.qualityIssues = "Style application error: " + e.getMessage();
        }
    }
    
    private static void runQualityAssessment(DiagnosticResult result) {
        StringBuilder issues = new StringBuilder();
        result.authenticityScore = 1.0f;
        
        // Check database quality
        if (result.totalPositionsInDB == 0) {
            issues.append("No positions in database; ");
            result.authenticityScore -= 0.5f;
        }
        
        if (result.exactMatches > 20) {
            issues.append("Too many exact matches (suspicious); ");
            result.authenticityScore -= 0.3f;
        }
        
        // Check AI integration
        if (!result.assistantAvailable) {
            issues.append("AI assistant not available; ");
            result.authenticityScore -= 0.2f;
        }
        
        // Check logical consistency
        if (result.exactMatches > 0 && result.targetElo < 2200) {
            issues.append("Low-ELO position matching super-GM database (illogical); ");
            result.authenticityScore -= 0.4f;
            result.logicallyConsistent = false;
        } else {
            result.logicallyConsistent = true;
        }
        
        result.qualityIssues = issues.toString();
        Log.d(TAG, "📊 Quality assessment complete - Score: " + result.authenticityScore);
    }
    
    // Helper methods
    private static List<String> extractGameInfo(List<GameDatabaseHelper.HistoricalPosition> positions) {
        // Extract meaningful game information for analysis
        return null; // Implement based on HistoricalPosition structure
    }
    
    private static void analyzeMatchQuality(String testFen, List<GameDatabaseHelper.HistoricalPosition> matches, DiagnosticResult result) {
        // Analyze whether the matches make logical sense
        // Check game quality, tournament level, etc.
    }
    
    private static boolean checkVectorStoreConnection(String masterName) {
        // Test vector store connectivity
        return false; // Implement based on your vector store setup
    }
    
    private static List<String> getStockfishMoves(StockfishManager stockfish, String fen, int targetElo) {
        // Get Stockfish move candidates
        return null; // Implement based on StockfishManager API
    }
    
    private static float getPersonalityWeight(PersonalityEngine engine) {
        // Get current personality weight setting
        return 0.0f; // Implement based on PersonalityEngine API
    }
    
    private static void testMoveSelection(PersonalityEngine engine, String fen, DiagnosticResult result) {
        // Test actual move selection process
        result.selectedMove = "Test pending";
        result.styleReasoning = "Integration test needed";
    }
    
    /**
     * Quick diagnostic for specific issues
     */
    public static void runQuickNameDiagnostic(Context context) {
        Log.d(TAG, "🔍 QUICK NAME DIAGNOSTIC");
        
        GameDatabaseHelper dbHelper = new GameDatabaseHelper(context);
        
        // Check what master names actually exist in database
        try {
            Map<String, Integer> nameCounts = dbHelper.getDatabaseStats();
            Log.d(TAG, "📊 Actual master names in database:");
            for (Map.Entry<String, Integer> entry : nameCounts.entrySet()) {
                Log.d(TAG, "   " + entry.getKey() + ": " + entry.getValue() + " positions");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Name diagnostic failed", e);
        }
    }
    
    /**
     * Test specific position for logical consistency
     */
    public static boolean testPositionLogic(String fen, int targetElo, int matchCount) {
        // Quick logic test: should low-ELO positions match super-GM database?
        if (targetElo < 2200 && matchCount > 5) {
            Log.w(TAG, "⚠️ LOGIC ISSUE: " + targetElo + " ELO position has " + matchCount + " super-GM matches");
            return false;
        }
        return true;
    }
}