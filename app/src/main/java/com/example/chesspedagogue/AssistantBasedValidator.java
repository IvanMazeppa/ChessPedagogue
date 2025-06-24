package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.List;

/**
 * 🧪 Test-focused validator to verify database improvements
 * 
 * This validator helps test whether:
 * 1. The lastMove field is properly imported from JSON
 * 2. Historical moves are correctly retrieved
 * 3. The database schema changes work as expected
 */
public class AssistantBasedValidator {
    private static final String TAG = "AssistantBasedValidator";
    
    private final Context context;
    private final GameDatabaseHelper gameDatabaseHelper;
    
    public AssistantBasedValidator(Context context) {
        this.context = context;
        this.gameDatabaseHelper = new GameDatabaseHelper(context);
    }
    
    /**
     * 🧪 Test database structure and lastMove field functionality
     */
    public String testDatabaseStructure() {
        Log.d(TAG, "🧪 Starting database structure test...");
        
        StringBuilder report = new StringBuilder();
        report.append("🧪 DATABASE STRUCTURE TEST REPORT\n");
        report.append("=====================================\n\n");
        
        // Test 1: Basic database diagnostics
        report.append("📊 BASIC DIAGNOSTICS:\n");
        report.append(gameDatabaseHelper.getDatabaseDiagnostics());
        report.append("\n");
        
        // Test 2: LastMove field test
        report.append("🎯 LAST_MOVE FIELD TEST:\n");
        report.append(gameDatabaseHelper.testLastMoveField());
        report.append("\n");
        
        // Test 3: Sample historical move lookup
        report.append("🔍 HISTORICAL MOVE LOOKUP TEST:\n");
        String testResult = testHistoricalMoveLookup();
        report.append(testResult);
        report.append("\n");
        
        // Test 4: Import verification
        report.append("📥 IMPORT VERIFICATION:\n");
        String importStatus = verifyImportStatus();
        report.append(importStatus);
        
        String finalReport = report.toString();
        Log.d(TAG, finalReport);
        return finalReport;
    }
    
    /**
     * Test historical move lookup with known positions
     */
    private String testHistoricalMoveLookup() {
        StringBuilder result = new StringBuilder();
        
        try {
            // Test with starting position
            String startingFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            String historicalMove = gameDatabaseHelper.getHistoricalMove(startingFEN, "alekhine");
            
            result.append(String.format("Starting position lookup: %s\n", 
                    historicalMove != null ? "✅ " + historicalMove : "❌ No move found"));
            
            // Test with a few more positions if they exist
            List<GameDatabaseHelper.HistoricalPosition> positions = 
                    gameDatabaseHelper.findSimilarPositions(startingFEN, "alekhine", 3);
            
            result.append(String.format("Similar positions found: %d\n", positions.size()));
            
            for (int i = 0; i < Math.min(3, positions.size()); i++) {
                GameDatabaseHelper.HistoricalPosition pos = positions.get(i);
                result.append(String.format("  Position %d: lastMove='%s', annotation='%s'\n", 
                        i + 1, 
                        pos.lastMove != null ? pos.lastMove : "NULL",
                        pos.annotation != null ? pos.annotation.substring(0, Math.min(30, pos.annotation.length())) + "..." : "NULL"));
            }
            
        } catch (Exception e) {
            result.append("❌ Error during lookup test: ").append(e.getMessage()).append("\n");
        }
        
        return result.toString();
    }
    
    /**
     * Verify that data has been properly imported
     */
    private String verifyImportStatus() {
        StringBuilder result = new StringBuilder();
        
        try {
            // Check if we have any Alekhine data
            boolean hasAlekhineData = gameDatabaseHelper.hasMasterData("alekhine");
            int alekhineCount = gameDatabaseHelper.getMasterPositionCount("alekhine");
            
            result.append(String.format("Alekhine data present: %s\n", hasAlekhineData ? "✅ YES" : "❌ NO"));
            result.append(String.format("Alekhine position count: %d\n", alekhineCount));
            
            // Check total database stats
            int totalPositions = gameDatabaseHelper.getTotalPositionCount();
            result.append(String.format("Total positions in database: %d\n", totalPositions));
            
            if (totalPositions == 0) {
                result.append("\n⚠️  WARNING: Database appears empty!\n");
                result.append("This could mean:\n");
                result.append("1. Data hasn't been imported yet\n");
                result.append("2. Import failed due to schema changes\n");
                result.append("3. Database needs to be rebuilt\n");
                result.append("\nSuggested actions:\n");
                result.append("- Clear database and reimport data\n");
                result.append("- Check asset files exist\n");
                result.append("- Verify JSON format compatibility\n");
            }
            
        } catch (Exception e) {
            result.append("❌ Error during import verification: ").append(e.getMessage()).append("\n");
        }
        
        return result.toString();
    }
    
    /**
     * 🔄 Force database reimport for testing
     */
    public String forceReimportData() {
        Log.d(TAG, "🔄 Starting forced database reimport...");
        
        StringBuilder result = new StringBuilder();
        result.append("🔄 FORCED DATABASE REIMPORT\n");
        result.append("===========================\n\n");
        
        try {
            // Clear existing data
            boolean cleared = gameDatabaseHelper.clearAndReimportAllData();
            result.append(String.format("Database cleared: %s\n", cleared ? "✅ SUCCESS" : "❌ FAILED"));
            
            if (cleared) {
                // Try to reimport Alekhine data
                boolean imported = gameDatabaseHelper.importMasterPositionsFromAssets("alekhine_full_positions.json");
                result.append(String.format("Alekhine import: %s\n", imported ? "✅ SUCCESS" : "❌ FAILED"));
                
                if (imported) {
                    // Verify the import worked
                    int count = gameDatabaseHelper.getMasterPositionCount("alekhine");
                    result.append(String.format("Alekhine positions imported: %d\n", count));
                    
                    // Test the lastMove field
                    result.append("\n🧪 POST-IMPORT lastMove TEST:\n");
                    result.append(gameDatabaseHelper.testLastMoveField());
                }
            }
            
        } catch (Exception e) {
            result.append("❌ Error during forced reimport: ").append(e.getMessage()).append("\n");
        }
        
        String finalResult = result.toString();
        Log.d(TAG, finalResult);
        return finalResult;
    }
}