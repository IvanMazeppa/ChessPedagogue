package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;

/**
 * Temporary diagnostic class to fix PersonalityEngine database issues
 * Run this once to verify and fix the master database import
 */
public class PersonalityEngineDiagnostic {
    private static final String TAG = "PersonalityDiagnostic";
    
    public static void diagnoseMasterDatabase(Context context, String masterName) {
        Log.d(TAG, "🔍 DIAGNOSING PERSONALITY ENGINE DATABASE FOR: " + masterName);
        
        try {
            GameDatabaseHelper dbHelper = new GameDatabaseHelper(context);
            
            // Step 1: Check if master data exists
            boolean hasData = dbHelper.hasMasterData(masterName);
            int positionCount = dbHelper.getMasterPositionCount(masterName);
            
            Log.d(TAG, "📊 Master '" + masterName + "' - hasData: " + hasData + ", positions: " + positionCount);
            
            if (!hasData || positionCount == 0) {
                Log.d(TAG, "🔧 No data found - forcing database import...");
                
                // Step 2: Force import the master's data
                String filename = getMasterPositionFilename(masterName);
                Log.d(TAG, "📁 Importing from file: " + filename);
                
                boolean imported = dbHelper.importMasterPositionsFromAssets(filename);
                
                if (imported) {
                    // Step 3: Verify the import worked
                    int newCount = dbHelper.getMasterPositionCount(masterName);
                    Log.d(TAG, "✅ IMPORT SUCCESS! " + masterName + " now has " + newCount + " positions");
                    
                    // Step 4: Test a sample query
                    testSampleQuery(dbHelper, masterName);
                    
                } else {
                    Log.e(TAG, "❌ IMPORT FAILED for " + masterName);
                    
                    // Get diagnostics to help debug
                    String diagnostics = dbHelper.getDatabaseDiagnostics();
                    Log.e(TAG, "🔬 Database diagnostics: " + diagnostics);
                }
                
            } else {
                Log.d(TAG, "✅ Data already exists - testing sample query...");
                testSampleQuery(dbHelper, masterName);
            }
            
            // Step 5: Show database stats
            java.util.Map<String, Integer> stats = dbHelper.getDatabaseStats();
            Log.d(TAG, "📊 Complete database stats: " + stats.toString());
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception during diagnosis", e);
        }
    }
    
    private static void testSampleQuery(GameDatabaseHelper dbHelper, String masterName) {
        try {
            Log.d(TAG, "🧪 Testing sample query for " + masterName + "...");
            
            // Test with starting position (should be common)
            String startingFEN = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
            java.util.List<GameDatabaseHelper.HistoricalPosition> results = 
                dbHelper.findSimilarPositions(startingFEN, masterName, 5);
            
            Log.d(TAG, "🧪 Sample query returned " + results.size() + " results");
            
            if (results.size() > 0) {
                Log.d(TAG, "✅ DATABASE QUERY WORKING! Sample position found:");
                GameDatabaseHelper.HistoricalPosition sample = results.get(0);
                Log.d(TAG, "   Master: " + sample.masterName);
                Log.d(TAG, "   FEN: " + sample.fen.substring(0, Math.min(50, sample.fen.length())));
                Log.d(TAG, "   Opponent: " + sample.opponent);
            } else {
                Log.w(TAG, "⚠️ No results for starting position - trying broader query...");
                
                // Test with any position from this master
                java.util.List<GameDatabaseHelper.HistoricalPosition> anyResults = 
                    dbHelper.findSimilarPositions("test", masterName, 1);
                Log.d(TAG, "🧪 Broader query returned " + anyResults.size() + " results");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Error in sample query", e);
        }
    }
    
    private static String getMasterPositionFilename(String masterName) {
        switch (masterName.toLowerCase()) {
            case "carlsen": return "carlsen_full_positions.json";
            case "tal": return "tal_full_positions.json";
            case "fischer": return "fischer_full_positions.json";
            case "kasparov": return "kasparov_full_positions.json";
            case "alekhine": return "alekhine_full_positions.json";
            case "kramnik": return "kramnik_full_positions.json";
            case "karpov": return "karpov_full_positions.json";
            case "capablanca": return "capablanca_full_positions.json";
            case "anand": return "anand_full_positions.json";
            case "morphy": return "morphy_full_positions.json";
            case "lasker": return "lasker_full_positions.json";
            case "botvinnik": return "botvinnik_full_positions.json";
            default: return masterName.toLowerCase() + "_full_positions.json";
        }
    }
}