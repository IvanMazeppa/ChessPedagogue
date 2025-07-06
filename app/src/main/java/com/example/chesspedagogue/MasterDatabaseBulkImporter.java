package com.example.chesspedagogue;

import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 🎯 Bulk importer for all chess master databases
 * 
 * Imports all available master position files from assets into the combined database.
 * Handles missing files gracefully and provides detailed import statistics.
 */
public class MasterDatabaseBulkImporter {
    private static final String TAG = "BulkImporter";
    
    private final Context context;
    private final GameDatabaseHelper databaseHelper;
    
    // Available masters with files in assets
    private static final String[] AVAILABLE_MASTERS = {
        "alekhine", "anand", "capablanca", "carlsen", "fischer",
        "karpov", "kasparov", "kramnik", "morphy", "tal",
        "dommaraju", "nakamura", "short"
    };
    
    // Masters mapped but files may be missing
    private static final String[] MAPPED_BUT_MISSING = {
        "botvinnik", "lasker"
    };
    
    public MasterDatabaseBulkImporter(Context context) {
        this.context = context;
        this.databaseHelper = new GameDatabaseHelper(context);
    }
    
    /**
     * 🚀 Import all available master databases
     */
    public ImportResults importAllMasters() {
        ImportResults results = new ImportResults();
        
        Log.d(TAG, "🎯 Starting bulk import of all chess master databases...");
        
        // Import available masters
        for (String master : AVAILABLE_MASTERS) {
            ImportResult result = importMasterIfNeeded(master);
            results.addResult(master, result);
            
            if (result.success) {
                Log.d(TAG, "✅ " + master + ": " + result.positionsImported + " positions");
            } else {
                Log.e(TAG, "❌ " + master + ": " + result.errorMessage);
            }
        }
        
        // Check for missing mapped masters
        for (String master : MAPPED_BUT_MISSING) {
            Log.w(TAG, "⚠️ " + master + ": Mapped but file missing");
            results.addMissing(master);
        }
        
        results.logSummary();
        return results;
    }
    
    /**
     * 📊 Import single master if not already present
     */
    private ImportResult importMasterIfNeeded(String masterName) {
        try {
            // Check if already imported
            int existingCount = databaseHelper.getMasterPositionCount(masterName);
            if (existingCount > 0) {
                return new ImportResult(true, existingCount, 
                    "Already imported (" + existingCount + " positions)");
            }
            
            // Get filename
            String filename = getMasterPositionFilename(masterName);
            if (filename == null) {
                return new ImportResult(false, 0, "No filename mapping found");
            }
            
            // Import from assets
            boolean imported = databaseHelper.importMasterPositionsFromAssets(filename);
            if (!imported) {
                return new ImportResult(false, 0, "Import failed - file may not exist");
            }
            
            // Verify import
            int newCount = databaseHelper.getMasterPositionCount(masterName);
            if (newCount == 0) {
                return new ImportResult(false, 0, "Import completed but no positions found");
            }
            
            return new ImportResult(true, newCount, "Successfully imported");
            
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception importing " + masterName, e);
            return new ImportResult(false, 0, "Exception: " + e.getMessage());
        }
    }
    
    /**
     * 📁 Get filename for master (duplicated from PersonalityEngine for independence)
     */
    private String getMasterPositionFilename(String masterName) {
        switch (masterName.toLowerCase()) {
            case "alekhine":
                return "alekhine_full_positions.json";
            case "capablanca":
                return "capablanca_full_positions.json";
            case "anand":
                return "anand_full_positions.json";
            case "kramnik":
                return "SecondaryModelDBs/kramnik_full_positions.json";
            case "carlsen":
                return "carlsen_full_positions.json";
            case "fischer":
                return "fischer_full_positions.json";
            case "tal":
                return "tal_full_positions.json";
            case "kasparov":
                return "kasparov_full_positions.json";
            case "karpov":
                return "karpov_full_positions.json";
            case "lasker":
                return "lasker_full_positions.json";
            case "morphy":
                return "morphy_full_positions.json";
            case "botvinnik":
                return "botvinnik_full_positions.json";
            case "dommaraju":
            case "gukesh":
                return "SecondaryModelDBs/dommaraju_full_positions.json";
            case "nakamura":
            case "hikaru":
                return "SecondaryModelDBs/nakamura_full_positions.json";
            case "short":
            case "nigel":
                return "short_full_positions.json";
            default:
                return masterName.toLowerCase() + "_positions.json";
        }
    }
    
    /**
     * 🔄 Retry failed masters by clearing their data and reimporting
     */
    public ImportResults retryFailedMasters(String[] failedMasters) {
        ImportResults results = new ImportResults();
        
        Log.d(TAG, "🔄 Retrying failed masters: " + java.util.Arrays.toString(failedMasters));
        
        for (String master : failedMasters) {
            // Clear existing data for this master
            clearMasterData(master);
            
            // Retry import
            ImportResult result = importMasterIfNeeded(master);
            results.addResult(master, result);
            
            if (result.success) {
                Log.d(TAG, "✅ RETRY SUCCESS - " + master + ": " + result.positionsImported + " positions");
            } else {
                Log.e(TAG, "❌ RETRY FAILED - " + master + ": " + result.errorMessage);
            }
        }
        
        results.logSummary();
        return results;
    }
    
    /**
     * 🗑️ Clear data for specific master
     */
    private void clearMasterData(String masterName) {
        try {
            Log.d(TAG, "🗑️ Clearing existing data for: " + masterName);
            databaseHelper.getWritableDatabase().delete(
                "master_positions", 
                "master_name = ?", 
                new String[]{masterName}
            );
        } catch (Exception e) {
            Log.e(TAG, "❌ Error clearing data for " + masterName, e);
        }
    }

    /**
     * 📊 Get statistics for all masters in database
     */
    public Map<String, Integer> getAllMasterCounts() {
        Map<String, Integer> counts = new HashMap<>();
        
        for (String master : AVAILABLE_MASTERS) {
            int count = databaseHelper.getMasterPositionCount(master);
            counts.put(master, count);
        }
        
        return counts;
    }
    
    /**
     * 🎯 Results container for import operations
     */
    public static class ImportResults {
        private final Map<String, ImportResult> results = new HashMap<>();
        private final List<String> missingFiles = new ArrayList<>();
        
        public void addResult(String master, ImportResult result) {
            results.put(master, result);
        }
        
        public void addMissing(String master) {
            missingFiles.add(master);
        }
        
        public int getSuccessCount() {
            int count = 0;
            for (ImportResult result : results.values()) {
                if (result.success) count++;
            }
            return count;
        }
        
        public int getTotalPositions() {
            int total = 0;
            for (ImportResult result : results.values()) {
                if (result.success) total += result.positionsImported;
            }
            return total;
        }
        
        public void logSummary() {
            Log.d(TAG, "📊 BULK IMPORT SUMMARY:");
            Log.d(TAG, "✅ Successful imports: " + getSuccessCount() + "/" + results.size());
            Log.d(TAG, "📈 Total positions imported: " + getTotalPositions());
            
            if (!missingFiles.isEmpty()) {
                Log.w(TAG, "⚠️ Missing files: " + missingFiles);
            }
            
            // Log individual results
            for (Map.Entry<String, ImportResult> entry : results.entrySet()) {
                String master = entry.getKey();
                ImportResult result = entry.getValue();
                String status = result.success ? "✅" : "❌";
                Log.d(TAG, status + " " + master + ": " + result.message + 
                      (result.success ? " (" + result.positionsImported + ")" : ""));
            }
        }
        
        public Map<String, ImportResult> getResults() {
            return results;
        }
        
        public List<String> getMissingFiles() {
            return missingFiles;
        }
    }
    
    /**
     * 📝 Individual import result
     */
    public static class ImportResult {
        public final boolean success;
        public final int positionsImported;
        public final String message;
        public final String errorMessage;
        
        public ImportResult(boolean success, int positionsImported, String message) {
            this.success = success;
            this.positionsImported = positionsImported;
            this.message = message;
            this.errorMessage = success ? null : message;
        }
    }
}